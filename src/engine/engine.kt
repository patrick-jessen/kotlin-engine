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
            //printFPS()
        }

        Window.close()
    }

    fun printFPS() {
        frameCount++
        if (frameCount > 100) {
            frameCount = 0
            var frameTime = (System.currentTimeMillis() - lastTime) / 100
            if (frameTime == 0L) frameTime = 1L
            lastTime = System.currentTimeMillis()
            println("$frameTime ms (${1000 / frameTime} fps)")
        }
    }
    private var lastTime = 0L
    private var frameCount = 0
}