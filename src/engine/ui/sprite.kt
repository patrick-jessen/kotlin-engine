package org.patrick.game.engine.ui

import glm_.vec2.Vec2
import glm_.vec4.Vec4
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL31.*
import org.patrick.game.engine.Asset
import org.patrick.game.engine.Texture
import java.lang.Exception

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

class UISize(var width:Float = 0f, var height:Float = 0f) {
    fun toVec2():Vec2 = Vec2(width, height)
    fun fitsWithin(other:UISize): Boolean {
        return width <= other.width && height <= other.height
    }
    operator fun plusAssign(other: UISize) {
        width += other.width
        height += other.height
    }
    override fun toString():String = "{$width,$height}"
}

abstract class UIElement(
    private val prefSize:UISize,
    private val minSize:UISize,
    private val maxSize:UISize
) {
    protected var size:UISize = prefSize
    protected var pos:Vec2 = Vec2()
    private val children = mutableListOf<UIElement>()

    init {
        if(!prefSize.fitsWithin(maxSize))
            throw Exception("prefSize may not be larger than maxSize")
        if(!minSize.fitsWithin(prefSize))
            throw Exception("prefSize may not be smaller than minSize")
    }

    fun calculateSizes() {
        var width = size.width
        val rowWidths = mutableListOf<Float>()

        val rows = mutableListOf<MutableList<UIElement>>()
        var currRow = mutableListOf<UIElement>()
        rows.add(currRow)


        for(c in children) {
            if(width-c.minSize.width < 0) {
                if(c.minSize.width > size.width) {
                    size.width = c.minSize.width
                    calculateSizes()
                    return
                }

                rowWidths.add(width)
                width = size.width
                currRow = mutableListOf()
                rows.add(currRow)
            }

            width -= c.minSize.width
            c.size = c.minSize
            c.size.height = c.prefSize.height

            currRow.add(c)
        }
        rowWidths.add(width)

        println("$rowWidths")

        for((i, row) in rows.withIndex()) {
            var w = rowWidths[i]

            while (w > 0) {
                var maxed = true
                val dW = w / row.size

                for (c in row) {
                    val deltaPref = c.prefSize.width - c.minSize.width
                    if (deltaPref < dW) {
                        c.size.width = c.prefSize.width
                        w -= deltaPref
                    } else {
                        maxed = false
                        w -= dW
                        c.size.width += dW
                    }
                }

                if (maxed) break
            }
        }

        for((ri,row) in rows.withIndex()) {
            var x = 0f
            for ((i, c) in row.withIndex()) {
                println("Child$i: ${c.size}")

                c.pos.x = x + rowWidths[ri]/2
                c.pos.y = 25f*ri
                x += c.size.width
            }
        }
    }

    fun add(el: UIElement) = children.add(el)

    fun render() {
        draw()
        for (c in children)
            c.render()
    }
    abstract fun draw()
}

class Sprite(
    size:UISize = UISize(100f,100f),
    minSize:UISize = UISize(),
    maxSize:UISize = size,
    private val color:Vec4 = Vec4(1, 1, 1, 1),
    private val texture: Texture = Asset.texture("panel.png"),
    private val slicePoints: Vec4 = Vec4()
): UIElement(size, minSize, maxSize)
{
    private var shader = Asset.shader("sprite")

    init {
        shader.set("tex", 0)
        QuadBuffer.init()
    }

    override fun draw() {
        shader.use()
        shader.set("pos", pos)
        shader.set("size", size.toVec2())
        shader.set("slicePoints", slicePoints)
        shader.set("color", color)

        texture.bind(0)

        if(slicePoints.length2() > 0)
            QuadBuffer.draw(9)
        else
            QuadBuffer.draw(1)
    }
}