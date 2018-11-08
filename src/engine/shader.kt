package org.patrick.game.engine

import glm_.mat4x4.Mat4
import org.lwjgl.opengl.GL20.*
import java.io.File

class Shader internal constructor(file:String): Resource(file, ::Shader) {
    private var handle = 0
    private var uniforms = mutableMapOf<String, Int>()

    init {
        val vert = File("./assets/shaders/$file.vert").readText()
        val frag = File("./assets/shaders/$file.frag").readText()
        createProgram(vert, frag)
    }

    fun use() = glUseProgram(handle)

    fun set(uniform:String, value:Float) =
        glUniform1f(getUniform(uniform), value)

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

        uniforms[name] = glGetUniformLocation(handle, name)
        return uniforms[name]!!
    }

    override fun destroy() = glDeleteProgram(handle)
}