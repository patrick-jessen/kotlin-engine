package org.patrick.game.engine.mesh

import org.lwjgl.opengl.GL43.*

class Mesh {
    private var vao = 0
    private var ibo = 0
    private var vbo = 0
    private var numIndices = 0
    private var numVertices = 0

    fun init(indices:IntArray? = null, vertices:FloatArray? = null) {
        if(vao != 0) return

        vao = glGenVertexArrays()
        glBindVertexArray(vao)

        if(vertices != null) {
            vbo = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, vbo)
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)
            glEnableVertexAttribArray(0)
            glBindBuffer(GL_ARRAY_BUFFER, 0)
            numVertices = vertices.size
        }
        if(indices != null) {
            ibo = glGenBuffers()
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)
            numIndices = indices.size
        }

        glBindVertexArray(0)
    }

    fun draw(numInstances:Int = 1, tessellation:Boolean = false) {
        glBindVertexArray(vao)
        val modeGL = when(tessellation) {
            true -> GL_PATCHES
            false -> GL_TRIANGLES
        }
        if(ibo == 0)glDrawArraysInstanced(modeGL, 0, numVertices, numInstances)
        else        glDrawElementsInstanced(modeGL, numIndices, GL_UNSIGNED_INT, 0, numInstances)
    }

    fun free() {
        glDeleteBuffers(ibo)
        glDeleteBuffers(vbo)
        glDeleteVertexArrays(vao)
    }
}