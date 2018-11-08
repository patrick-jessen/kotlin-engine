package org.patrick.game.engine

import glm_.size
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL31.*

class Terrain(val heightMap:Texture) {
    private var vbo = 0
    private var vao = 0

    init {
        val buf = BufferUtils.createFloatBuffer(3)

        vao = glGenVertexArrays()
        glBindVertexArray(vao)

        vbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, buf, GL_STATIC_DRAW)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)
        glEnableVertexAttribArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)

        glBindVertexArray(0)
    }

    fun draw() {
        Asset.shader("terrain").use()
        heightMap.bind(0)

        glBindVertexArray(vao)
        glDrawArraysInstanced(GL_TRIANGLES, 0, 3, 8388608 )
    }
}