package org.patrick.game.engine

import org.lwjgl.opengl.GL30.*

abstract class RefCounter {
    private var counter = 0
    internal fun reference() = counter++
    fun dereference() {
        if(--counter == 0) destroy()
    }
    protected abstract fun destroy()
}

fun checkGLError() {
    when(glGetError()) {
        GL_INVALID_ENUM -> throw Exception("GL_INVALID_ENUM")
        GL_INVALID_VALUE -> throw Exception("GL_INVALID_VALUE")
        GL_INVALID_OPERATION -> throw Exception("GL_INVALID_OPERATION")
        GL_STACK_OVERFLOW -> throw Exception("GL_STACK_OVERFLOW")
        GL_STACK_UNDERFLOW -> throw Exception("GL_STACK_UNDERFLOW")
        GL_OUT_OF_MEMORY -> throw Exception("GL_OUT_OF_MEMORY")
        GL_INVALID_FRAMEBUFFER_OPERATION -> throw Exception("GL_INVALID_FRAMEBUFFER_OPERATION")
    }
}