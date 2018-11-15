package org.patrick.game.engine.ui

import org.patrick.game.engine.Window

object UI {
    var root: UIElement? = null

    fun calculateSizes() {
        root?.calculateSizes(UISize(Window.size.first,Window.size.second), UISize(Window.size.first,Window.size.second))
    }
}