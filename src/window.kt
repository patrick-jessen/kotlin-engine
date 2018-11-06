package org.patrick.game

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.NULL

object Window {
    var size = Pair(800, 600)
    var title = "New Window"
    private var handle = 0L

    fun open(setupFn:()->Unit, renderFn:()->Unit) {
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
        setupFn()

        while (!glfwWindowShouldClose(handle)) {
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
            renderFn()
            glfwSwapBuffers(handle)
            glfwPollEvents()
        }

        glfwDestroyWindow(handle)
        glfwTerminate()
    }
}