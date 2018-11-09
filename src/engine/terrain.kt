package org.patrick.game.engine

import glm_.mat4x4.Mat4
import org.patrick.game.engine.ui.QuadBuffer

class Terrain(val heightMap:Texture, val diffuseTex:Texture) {
    private var vbo = 0
    private var vao = 0
    private var shader: Shader? = null

    init {
        shader = Asset.shader("terrain")
        shader!!.use()
        shader!!.set("heightMap", 0)
        shader!!.set("diffuseTex", 1)
        QuadBuffer.init()
    }

    fun draw(modelMat:Mat4) {
        shader!!.use()
        shader!!.set("modelMat", modelMat)

        heightMap.bind(0)
        diffuseTex.bind(1)

        QuadBuffer.draw(719104)
    }
}