package org.patrick.game.engine.ui

import glm_.vec2.Vec2
import org.patrick.game.engine.Window

object UI {
    var root: UIElement? = null

    fun calculateSizes() {
        root?.calculateSizes(UISize(Window.size.first,Window.size.second), UISize(Window.size.first,Window.size.second))
    }

    fun onClick(pos: Vec2) {
        root?.handleMouseClick(pos)
    }
}