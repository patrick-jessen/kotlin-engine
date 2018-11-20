package org.patrick.game.engine

import glm_.mat4x4.Mat4
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL43.*
import org.patrick.game.engine.ui.QuadBuffer


object PatchMesh {
    var vbo = 0
    var ibo = 0
    var vao = 0

    fun init() {
        if(vao != 0) return

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

        vao = glGenVertexArrays()
        glBindVertexArray(vao)

        vbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)
        glEnableVertexAttribArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)

        ibo = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)

        glBindVertexArray(0)
    }

    fun draw() {
        glBindVertexArray(vao)
        glDrawElementsInstanced(GL_PATCHES, 6, GL_UNSIGNED_INT, 0, 5*5)
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