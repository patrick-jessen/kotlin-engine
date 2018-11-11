package org.patrick.game.engine

import glm_.glm
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL30.*

enum class VideoMode { WINDOWED, FULLSCREEN, WINDOWED_FULLSCREEN }

object Window {
    var size = Pair(800, 600)
    var videoMode = VideoMode.WINDOWED
    var title = "New Window"
    private var handle = 0L

    internal fun open() {
        if(!glfwInit()) throw Exception("Unable to initialize window")

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)

        handle = when(videoMode) {
            VideoMode.WINDOWED -> glfwCreateWindow(size.first, size.second, title, 0L, 0L)
            VideoMode.FULLSCREEN -> glfwCreateWindow(size.first, size.second, title, glfwGetPrimaryMonitor(), 0L)
            VideoMode.WINDOWED_FULLSCREEN -> {
                glfwWindowHint(GLFW_DECORATED, 0)
                glfwCreateWindow(size.first, size.second, title, 0L, 0L)
            }
        }
        if(handle == 0L) throw Exception("Failed to create window")
        glfwMakeContextCurrent(handle)
        GL.createCapabilities()
        initGL()

        glfwSetFramebufferSizeCallback(handle, ::onResize)
        glfwSetKeyCallback(handle, ::onKey)
//        winHandle.SetKeyCallback(keyCallback)
//        winHandle.SetMouseButtonCallback(mouseButtonCallback)
//        winHandle.SetCursorPosCallback(cursorPosCallback)
//        winHandle.SetScrollCallback(scrollCallback)
        glfwSwapInterval(1)

        if(videoMode == VideoMode.WINDOWED_FULLSCREEN)
            glfwMaximizeWindow(handle)
    }

    private fun initGL() {
        UniformBuffers.add("data3D", 64)
        UniformBuffers.add("data2D", 64)

        glEnable(GL_CULL_FACE)
        glEnable(GL_DEPTH_TEST)
    }

    internal fun shouldClose(): Boolean = glfwWindowShouldClose(handle)

    internal fun update() {
        keysPressed.clear()
        keysReleased.clear()

        glfwSwapBuffers(handle)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glfwPollEvents()
    }

    internal fun close() {
        glfwDestroyWindow(handle)
        glfwTerminate()
    }

    private fun onResize(win:Long, width:Int, height:Int) {
        glViewport(0, 0, width, height)
        UniformBuffers.set("data2D", glm.ortho(0f, width.toFloat(), height.toFloat(), 0f, 0f, 1f).toFloatArray())
        size = Pair(width, height)
    }
    private fun onKey(win:Long, key:Int, code:Int, action:Int, modifiers:Int) {
        if(action == 1) {
            keysDown[key] = true
            keysPressed[key] = true
        }
        else if(action == 0) {
            keysDown[key] = false
            keysReleased[key] = true
        }
    }

    private var keysPressed = mutableMapOf<Int, Boolean>()
    private var keysReleased = mutableMapOf<Int, Boolean>()
    private var keysDown = mutableMapOf<Int, Boolean>()
    fun keyDown(k:Int):Boolean = keysDown[k] ?: false
    fun keyPressed(k:Int):Boolean = keysPressed[k] ?: false
    fun keyReleased(k:Int):Boolean = keysReleased[k] ?: false
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