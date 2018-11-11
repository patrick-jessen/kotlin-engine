package org.patrick.game.engine

import glm_.random
import org.lwjgl.opengl.GL11.*
import java.util.*
import kotlin.random.Random
import kotlin.reflect.KFunction

enum class EngineState { UNINITIALIZED, SETUP, RUNNING }

object Engine {
    var state: EngineState = EngineState.UNINITIALIZED
        private set

    fun start(setupFn: ()->Unit, runFn: ()->Unit) {
        state = EngineState.SETUP
        Window.open()
        setupFn()
        state = EngineState.RUNNING
        runFn()
    }

    fun render(renderFn: ()->Unit) {
        while(!Window.shouldClose()) {
            Window.update()
            renderFn()
            calcFrameTime()
        }

        Window.close()
    }

    private fun calcFrameTime() {
        frameTime = (System.nanoTime() - lastTime).toFloat() / 1000000f
        if (frameTime == 0f) frameTime = 1f
        lastTime = System.nanoTime()
    }
    private var lastTime = 0L
    var frameTime = 1f
    var fps = 0f
        get() = 1000f/ frameTime
}