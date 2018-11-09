package org.patrick.game.engine

import org.lwjgl.opengl.GL11.*
import kotlin.reflect.KFunction

enum class EngineState { UNINITIALIZED, SETUP, RUNNING }

object Engine {
    var state: EngineState = EngineState.UNINITIALIZED
        private set

    fun start(setupFn: ()->Unit, runFn: ()->Unit) {
        Window.open()
        state = EngineState.SETUP
        setupFn()
        state = EngineState.RUNNING
        runFn()
    }

    fun render(renderFn: ()->Unit) {
        while(!Window.shouldClose()) {
            Window.update()
            renderFn()
            calcFPS();
        }

        Window.close()
    }

    private fun calcFPS() {
        frameCount++
        if (frameCount > 100) {
            frameCount = 0
            var frameTime = (System.currentTimeMillis() - lastTime) / 100
            if (frameTime == 0L) frameTime = 1L
            lastTime = System.currentTimeMillis()
            fps = 1000f / frameTime
        }
    }
    var fps = 0f
        private set
    private var lastTime = 0L
    private var frameCount = 0
}