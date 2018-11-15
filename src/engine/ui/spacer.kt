package org.patrick.game.engine.ui

import glm_.vec2.Vec2
import glm_.vec4.Vec4

class UISpacer(val debug:Boolean = false) : UIElement(
    prefSize = UISize(1f,1f),
    minSize = UISize(),
    maxSize = UISize(1f,1f)
) {
    override fun draw(pos: Vec2) {
        if(debug) {
            Sprite(color = Vec4(1,0,0,1)).draw(pos, size.toVec2())
        }
    }
}