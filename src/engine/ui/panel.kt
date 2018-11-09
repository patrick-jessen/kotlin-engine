package org.patrick.game.engine.ui

import glm_.vec2.Vec2
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL31.*
import org.patrick.game.engine.Asset
import org.patrick.game.engine.Shader
import org.patrick.game.engine.Texture

object QuadBuffer {
    private var vao = 0
    private var vbo = 0

    fun init() {
        if(vao > 0) return

        val buf = BufferUtils.createFloatBuffer(12).put(
            floatArrayOf(
                0f,0f,
                0f,1f,
                1f,0f,
                1f,1f,
                1f,0f,
                0f,1f
            )
        )
        buf.rewind()

        vao = glGenVertexArrays()
        glBindVertexArray(vao)

        vbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, buf, GL_STATIC_DRAW)
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0)
        glEnableVertexAttribArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)

        glBindVertexArray(0)
    }

    fun draw(count:Int) {
        glBindVertexArray(vao)
        glDrawArraysInstanced(GL_TRIANGLES, 0, 6, count)
    }
}

class Panel(val pos:Vec2, val size:Vec2, val texture: Texture = Asset.texture("panel.png")) {
    private var shader: Shader? = null

    init {
        shader = Asset.shader("panel")
        shader!!.set("tex", 0)
        QuadBuffer.init()
    }

    fun draw() {
        shader!!.use()
        shader!!.set("pos", pos)
        shader!!.set("size", size)

        texture.bind(0)

        QuadBuffer.draw(1)
    }
}