package org.patrick.game.engine.ui

import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec4.Vec4
import org.patrick.game.engine.Asset
import org.patrick.game.engine.Shader
import java.nio.FloatBuffer

class Text(val text:String, val pos: Vec2 = Vec2(), val color: Vec4 = Vec4(1,0,0,1)) {
    private var shader = Asset.shader("text")
    private var font = Asset.texture("font.png")

    init {
        shader.set("font", 0)
        QuadBuffer.init()
    }

    fun draw() {
        val data = Mat4()
        for((i, c) in text.withIndex()) {
            data[i%4][i/4] = c.toFloat()
        }

        shader.set("str", data)
        shader.set("color", color)
        shader.set("pos", pos)

        font.bind(0)

        QuadBuffer.draw(text.length)
    }
}