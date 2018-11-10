package org.patrick.game.engine.ui

import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec4.Vec4
import org.patrick.game.engine.Asset
import org.patrick.game.engine.Shader
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Text(
    private val text:String,
    private val pos: Vec2 = Vec2(),
    private val color: Vec4 = Vec4(0,0,0,1),
    private val size: Int = 16
) {
    private var shader = Asset.shader("text")
    private var font = Asset.texture("font.png")
    private var data = intArrayOf()

    init {
        if(text.length > 64*4)
            throw Exception("Text supports a max string length of ${64*4}")

        shader.set("font", 0)
        QuadBuffer.init()

        val data = IntBuffer.allocate(64)
        for(i in 0 until text.length step 4) {
            var dword = (text[i].toInt() shl 24)
            if(i+1 < text.length)
                dword = dword or (text[i+1].toInt() shl 16)
            if(i+2 < text.length)
                dword = dword or (text[i+2].toInt() shl 8)
            if(i+3 < text.length)
                dword = dword or text[i+3].toInt()
            data.put(dword)
        }

        data.rewind()
        this.data = data.array()
    }

    fun draw() {
        shader.set("str", data)
        shader.set("color", color)
        shader.set("pos", pos)
        shader.set("size", size)

        font.bind(0)

        QuadBuffer.draw(text.length)
    }
}