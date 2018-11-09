package org.patrick.game.engine

import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL31.*
import java.io.File
import java.nio.ByteBuffer

val uniformBuffers = mutableListOf<UniformBuffer>()

class UniformBuffer(val name:String, bytes:Int) {
    var handle = 0
    init {
        val buf = BufferUtils.createByteBuffer(bytes)

        handle = glGenBuffers()
        glBindBuffer(GL_UNIFORM_BUFFER, handle)
        glBufferData(GL_UNIFORM_BUFFER, buf, GL_STATIC_DRAW)
        glBindBufferBase(GL_UNIFORM_BUFFER, uniformBuffers.size, handle)
        glBindBuffer(GL_UNIFORM_BUFFER, 0)
    }

    fun set(data:ByteBuffer) {
        glBindBuffer(GL_UNIFORM_BUFFER, handle)
        glBufferSubData(GL_UNIFORM_BUFFER, 0, data)
        glBindBuffer(GL_UNIFORM_BUFFER, 0)
    }
    fun set(data:FloatArray) {
        glBindBuffer(GL_UNIFORM_BUFFER, handle)
        glBufferSubData(GL_UNIFORM_BUFFER, 0, data)
        glBindBuffer(GL_UNIFORM_BUFFER, 0)
    }
}

class Shader internal constructor(file:String): Resource(file, ::Shader) {
    private var handle = 0
    private var uniforms = mutableMapOf<String, Int>()

    init {
        val vert = File("./assets/shaders/$file.vert").readText()
        val frag = File("./assets/shaders/$file.frag").readText()
        createProgram(vert, frag)

        glUseProgram(handle)
        for((i, ubo) in uniformBuffers.withIndex()) {
            val ubi = glGetUniformBlockIndex(handle, ubo.name)
            glUniformBlockBinding(handle, ubi, i)
        }
        glUseProgram(0)
    }

    fun use() = glUseProgram(handle)

    fun set(uniform:String, value:Float) =
        glUniform1f(getUniform(uniform), value)

    fun set(uniform:String, value:Vec2) =
        glUniform2f(getUniform(uniform), value.x, value.y)

    fun set(uniform:String, value: Vec3) =
        glUniform3f(getUniform(uniform), value.x, value.y, value.z)

    fun set(uniform:String, value:Int) =
        glUniform1i(getUniform(uniform), value)

    fun set(uniform:String, value:Mat4) =
        glUniformMatrix4fv(getUniform(uniform), false, value.toFloatArray())

    private fun compileShader(type:Int, src:String): Int {
        val handle = glCreateShader(type)
        glShaderSource(handle, src)
        glCompileShader(handle)

        println(glGetShaderInfoLog(handle))

        val status = glGetShaderi(handle, GL_COMPILE_STATUS)
        if(status == GL_FALSE) throw Exception(glGetShaderInfoLog(handle))
        return handle
    }

    private fun createProgram(vertSrc:String, fragSrc:String) {
        val vert = compileShader(GL_VERTEX_SHADER, vertSrc)
        val frag = compileShader(GL_FRAGMENT_SHADER, fragSrc)

        handle = glCreateProgram()
        glAttachShader(handle, vert)
        glAttachShader(handle, frag)
        glLinkProgram(handle)

        println(glGetProgramInfoLog(handle))

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