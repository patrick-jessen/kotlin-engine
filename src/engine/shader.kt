package org.patrick.game.engine

import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL31.*
import org.lwjgl.opengl.GL40.GL_TESS_CONTROL_SHADER
import org.lwjgl.opengl.GL40.GL_TESS_EVALUATION_SHADER
import java.io.File

object UniformBuffers {
    internal var ubos = mutableMapOf<String, UniformBuffer>()
    fun add(name:String, bytes:Int) {
        if(Engine.state != EngineState.SETUP)
            throw Exception("Uniform buffers must be set in SETUP state")
        ubos[name] = UniformBuffer(bytes)
    }
    fun set(name:String, data: FloatArray) {
        ubos[name]!!.set(data)
    }
}

internal class UniformBuffer(bytes:Int) {
    private var handle = 0
    internal var index = 0
    init {
        val buf = BufferUtils.createByteBuffer(bytes)

        index = UniformBuffers.ubos.size
        handle = glGenBuffers()
        glBindBuffer(GL_UNIFORM_BUFFER, handle)
        glBufferData(GL_UNIFORM_BUFFER, buf, GL_STATIC_DRAW)
        glBindBufferBase(GL_UNIFORM_BUFFER, index, handle)
        glBindBuffer(GL_UNIFORM_BUFFER, 0)
    }

    fun set(data:FloatArray) {
        glBindBuffer(GL_UNIFORM_BUFFER, handle)
        glBufferSubData(GL_UNIFORM_BUFFER, 0, data)
        glBindBuffer(GL_UNIFORM_BUFFER, 0)
    }
}

object ShaderSettings {
    internal var settings = mutableMapOf<String, ShaderSettingsObject>()
    fun add(name:String, textures: Array<String>) {
        if(Engine.state != EngineState.SETUP)
            throw Exception("Shader settings must be set in SETUP state")
        settings[name] = ShaderSettingsObject(textures)
    }
}
internal class ShaderSettingsObject(val textures:Array<String> = arrayOf())

class Shader internal constructor(file:String): Resource(file, ::Shader) {
    private var handle = 0
    private var uniforms = mutableMapOf<String, Int>()

    init {
        val path = "./assets/shaders/$file/$file"
        val vert = File("${path}_VS.glsl").readText()
        val frag = File("${path}_FS.glsl").readText()
        var tesc = ""
        var tese = ""
        if(File("${path}_TCS.glsl").exists()) {
            tesc = File("${path}_TCS.glsl").readText()
        }
        if(File("${path}_TES.glsl").exists()) {
            tese = File("${path}_TES.glsl").readText()
        }

        createProgram(vert, frag, tesc, tese)

        glUseProgram(handle)
        for((key, ubo) in UniformBuffers.ubos) {
            val ubi = glGetUniformBlockIndex(handle, key)
            if(ubi >= 0)
                glUniformBlockBinding(handle, ubi, ubo.index)
        }

        val settings = ShaderSettings.settings[file] ?: ShaderSettingsObject()
        for((i, s) in settings.textures.withIndex()) {
            set(s, i)
        }

        glUseProgram(0)
    }

    fun use() = glUseProgram(handle)

    fun set(uniform:String, value: IntArray) =
        glUniform1iv(getUniform(uniform), value)

    fun set(uniform:String, value:Float) =
        glUniform1f(getUniform(uniform), value)

    fun set(uniform:String, value:Vec2) =
        glUniform2f(getUniform(uniform), value.x, value.y)

    fun set(uniform:String, value: Vec3) =
        glUniform3f(getUniform(uniform), value.x, value.y, value.z)

    fun set(uniform:String, value: Vec4) =
        glUniform4f(getUniform(uniform), value.x, value.y, value.z, value.w)

    fun set(uniform:String, value:Int) =
        glUniform1i(getUniform(uniform), value)

    fun set(uniform:String, value:Mat4) =
        glUniformMatrix4fv(getUniform(uniform), false, value.toFloatArray())

    private fun compileShader(type:Int, src:String): Int {
        val handle = glCreateShader(type)
        glShaderSource(handle, src)
        glCompileShader(handle)

        val log = glGetShaderInfoLog(handle)
        if(log.isNotBlank()) println(log)

        val status = glGetShaderi(handle, GL_COMPILE_STATUS)
        if(status == GL_FALSE) throw Exception(glGetShaderInfoLog(handle))
        return handle
    }

    private fun createProgram(vertSrc:String, fragSrc:String, tessControl:String, tessEval:String) {
        val vert = compileShader(GL_VERTEX_SHADER, vertSrc)
        val frag = compileShader(GL_FRAGMENT_SHADER, fragSrc)

        handle = glCreateProgram()
        glAttachShader(handle, vert)
        glAttachShader(handle, frag)

        if(tessControl.isNotBlank()) {
            val tesc = compileShader(GL_TESS_CONTROL_SHADER, tessControl)
            glAttachShader(handle, tesc)
        }
        if(tessEval.isNotBlank()) {
            val tese = compileShader(GL_TESS_EVALUATION_SHADER, tessEval)
            glAttachShader(handle, tese)
        }

        glLinkProgram(handle)

        val log = glGetProgramInfoLog(handle)
        if(log.isNotBlank()) println(log)

        val status = glGetProgrami(handle, GL_LINK_STATUS)
        if(status == GL_FALSE) throw Exception(glGetProgramInfoLog(handle))

        glDeleteShader(vert)
        glDeleteShader(frag)
    }

    private fun getUniform(name:String): Int {
        use()
        if(name in uniforms) return uniforms[name]!!

        val ul = glGetUniformLocation(handle, name)
        if(ul == -1) println("Warning: uniform '$name' not found")
        uniforms[name] = ul
        return ul
    }

    override fun destroy() = glDeleteProgram(handle)
}