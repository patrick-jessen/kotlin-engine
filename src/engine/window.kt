package org.patrick.game.engine

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil.NULL

object Window {
    var size = Pair(800, 600)
    var title = "New Window"
    private var handle = 0L

    internal fun open() {
        if(!glfwInit()) throw Exception("Unable to initialize window")

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)

        handle = glfwCreateWindow(size.first, size.second, title, NULL, NULL)
        if(handle == NULL) throw Exception("Failed to create window")

        glfwMakeContextCurrent(handle)
        GL.createCapabilities()

        glEnable(GL_CULL_FACE)
        glEnable(GL_DEPTH_TEST)
    }

    internal fun shouldClose(): Boolean = glfwWindowShouldClose(handle)

    internal fun update() {
        glfwSwapBuffers(handle)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glfwPollEvents()
    }

    internal fun close() {
        glfwDestroyWindow(handle)
        glfwTerminate()
    }
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