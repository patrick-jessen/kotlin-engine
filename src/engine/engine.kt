package org.patrick.game.engine

import glm_.random
import org.lwjgl.opengl.GL11.*
import org.patrick.game.engine.ui.UI
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
            renderUI()
            calcFrameTime()
        }

        Window.close()
    }

    private fun renderUI() {
        glDisable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
        glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        UI.root?.render()
        glDisable(GL_BLEND)
        glEnable(GL_DEPTH_TEST)
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