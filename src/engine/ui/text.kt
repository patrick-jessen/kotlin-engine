package org.patrick.game.engine.ui

import glm_.func.common.ceil
import glm_.vec2.Vec2
import glm_.vec4.Vec4
import org.patrick.game.engine.Asset
import java.nio.IntBuffer

private fun calcTextUISize(text:String, fontSize:Int): UISize {
    val ratios = doubleArrayOf(
        // symbols
        0.28,0.28,0.43,0.57,0.57,0.77,0.77,0.28,0.3,0.3,0.43,0.6,0.28,0.37,0.28,0.4,
        // numbers
        0.57,0.57,0.57,0.57,0.57,0.57,0.57,0.57,0.57,0.57,
        // symbols
        0.28,0.28,0.6,0.6,0.6,0.5,0.7,
        // uppercase letters
        0.73,0.57,0.6,0.7,0.53,0.43,0.7,0.73,0.27,0.27,0.6,0.53,0.9,0.7,0.8,0.53,0.8,0.54,0.53,0.47,0.7,0.6,0.93,0.63,0.54,0.6,
        // symbols
        0.3,0.54,0.3,0.6,0.5,0.4,
        // lowercase letters
        0.47,0.54,0.46,0.58,0.53,0.26,0.58,0.54,0.24,0.24,0.48,0.24,0.87,0.54,0.57,0.59,0.59,0.33,0.43,0.33,0.54,0.53,0.77,0.53,0.5,0.47,
        // symbols
        0.3,0.6,0.3,0.6
    )

    var width = 0.0
    for(c in text) {
        width += fontSize * ratios[c.toInt()-32]
    }
    return UISize(width.ceil.toInt(), fontSize)
}

class Text(
    private val text:String,
    private val color: Vec4 = Vec4(0,0,0,1),
    private val fontSize: Int = 16
): UIElement(
    calcTextUISize(text,fontSize),
    calcTextUISize(text,fontSize),
    calcTextUISize(text,fontSize)
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

    override fun draw(pos: Vec2) {
        shader.set("str", data)
        shader.set("color", color)
        shader.set("pos", pos)
        shader.set("size", fontSize)

        font.bind(0)

        QuadBuffer.draw(text.length)
    }
}