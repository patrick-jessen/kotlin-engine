package org.patrick.game.engine

import glm_.mat4x4.Mat4
import org.patrick.game.engine.mesh.*

object PatchMesh {
    private var mesh = Mesh()

    fun init() {
        val vertices = floatArrayOf(
            0f, 0f, 0f,
            1f, 0f, 0f,
            1f, 1f, 0f,
            0f, 1f, 0f
        )
        val indices = intArrayOf(
            0, 3, 2,
            0, 2, 1
        )
        mesh.init(indices, vertices)
    }

    fun draw() {
        mesh.draw(25, true)
    }
}

class Terrain(val heightMap:Texture, val diffuseTex:Texture) {
    private var shader = Asset.shader("terrain")

    init {
        //QuadBuffer.init()
        PatchMesh.init()
    }

    fun draw(modelMat:Mat4) {
        shader.use()
        shader.set("modelMat", modelMat)

        heightMap.bind(0)
        diffuseTex.bind(1)

        //QuadBuffer.draw(1)
        PatchMesh.draw()
    }
}