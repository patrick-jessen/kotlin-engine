package org.patrick.game.engine

import glm_.mat4x4.Mat4
import glm_.size
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL31.*

class Terrain(val heightMap:Texture, val diffuseTex:Texture) {
    private var vbo = 0
    private var vao = 0
    private var shader: Shader? = null

    init {
        shader = Asset.shader("terrain")
        shader!!.use()
        shader!!.set("heightMap", 0)
        shader!!.set("diffuseTex", 1)

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

    fun draw(modelMat:Mat4) {
        shader!!.use()
        shader!!.set("modelMat", modelMat)

        heightMap.bind(0)
        diffuseTex.bind(1)

        glBindVertexArray(vao)
        glDrawArraysInstanced(GL_TRIANGLES, 0, 3, 1438208 )
    }
}