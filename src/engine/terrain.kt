package org.patrick.game.engine

import glm_.mat4x4.Mat4
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL43.*
import org.patrick.game.engine.ui.QuadBuffer


class Terrain(val heightMap:Texture, val diffuseTex:Texture) {
    private var shader = Asset.shader("terrain")

    init {
        QuadBuffer.init()
    }

    fun draw(modelMat:Mat4) {
        shader.use()
        shader.set("modelMat", modelMat)

        heightMap.bind(0)
        diffuseTex.bind(1)

        QuadBuffer.draw(1)
    }
}