package org.patrick.game.engine.gl

import org.lwjgl.opengl.GL43.*


fun GLCheckError(msg:String="") {
    var err = when(glGetError()) {
        GL_INVALID_ENUM -> "GL_INVALID_ENUM"
        GL_INVALID_VALUE -> "GL_INVALID_VALUE"
        GL_INVALID_OPERATION -> "GL_INVALID_OPERATION"
        GL_STACK_OVERFLOW -> "GL_STACK_OVERFLOW"
        GL_STACK_UNDERFLOW -> "GL_STACK_UNDERFLOW"
        GL_OUT_OF_MEMORY -> "GL_OUT_OF_MEMORY"
        GL_INVALID_FRAMEBUFFER_OPERATION -> "GL_INVALID_FRAMEBUFFER_OPERATION"
        0 -> return
        else -> "UNKNOWN_ERROR"
    }
    if(msg.isNotEmpty()) err += " - $msg"
    throw Exception(err)
}