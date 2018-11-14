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

class UISize {
    private var relWidth = false
    private var relHeight = false
    private var fwidth = 0f
    private var fheight = 0f

    var width:Int
        set(value) {
            if(relWidth) throw Exception("Width is relative")
            fwidth = value.toFloat()
        }
        get() {
            if(relWidth) throw Exception("Width is relative")
            return fwidth.toInt()
        }
    var height:Int
        set(value) {
            if(relHeight) throw Exception("Height is relative")
            fheight = value.toFloat()
        }
        get() {
            if(relHeight) throw Exception("Height is relative")
            return fheight.toInt()
        }

    constructor()
    constructor(width:Int, height:Int) {
        this.fwidth = width.toFloat()
        this.fheight = height.toFloat()
    }
    constructor(width:Int, height:Float) {
        relHeight = true
        this.fwidth = width.toFloat()
        this.fheight = height
    }
    constructor(width:Float, height:Int) {
        relWidth = true
        this.fwidth = width
        this.fheight = height.toFloat()
    }
    constructor(width:Float, height:Float) {
        relWidth = true
        relHeight = true
        this.fwidth = width
        this.fheight = height
    }

    fun isAbsolute() = relWidth && relHeight

    fun fitsWithin(other:UISize): Boolean {
        return width <= other.width && height <= other.height
    }
    fun fit(other:UISize) {
        if(width < other.width) width = other.width
        if(height < other.height) height = other.height
    }

    fun toVec2():Vec2 = Vec2(width, height)
    fun toAbsolute(relativeTo:UISize): UISize {
        var w = fwidth
        var h = fheight
        if(relWidth) w *= relativeTo.width
        if(relHeight) h *= relativeTo.height
        return UISize(w.toInt(), h.toInt())
    }


    fun copy():UISize {
        val new = UISize(width, height)
        new.relWidth = relWidth
        new.relHeight = relHeight
        return new
    }
    operator fun plusAssign(other: UISize) {
        fwidth += other.fwidth
        fheight += other.fheight
    }
    operator fun minus(other: UISize):UISize {
        return UISize(width - other.width, height - other.height)
    }
    override fun toString():String = "{$width,$height}"
}

abstract class UIElement(
    private val prefSize:UISize,
    private val minSize:UISize,
    private val maxSize:UISize
) {
    private var parent:UIElement? = null
    protected var size:UISize = prefSize
    protected var pos:Vec2 = Vec2()
    private val children = mutableListOf<UIElement>()

    fun calculateSizesVertical() {
        val parentSize = size

        calc@ while(true) {
            val calcMinSize = UISize()
            val calcPrefSize = UISize()
            var finalSize = UISize()

            // Calculate minimum size needed to fit children
            for (c in children) {
                val childMinSize = c.minSize.toAbsolute(parentSize)
                val childPrefSize = c.prefSize.toAbsolute(parentSize)
                calcMinSize.width += childMinSize.width
                calcPrefSize.width += childPrefSize.width
                if (childMinSize.height > calcMinSize.height)
                    calcMinSize.height = childMinSize.height
                if (childPrefSize.height > calcPrefSize.height)
                    calcPrefSize.height = childPrefSize.height

                // Set child to its preferred size
                c.size = c.prefSize.toAbsolute(parentSize)
            }

            finalSize = calcPrefSize

            // Parent cannot fit children
            if (!calcMinSize.fitsWithin(parentSize)) {
                if (!calcMinSize.fitsWithin(maxSize))
                    println("Warning: children do not fit in parent")
                else {
                    parentSize.fit(calcMinSize)
                    continue@calc // recalculate
                }
            }

            // Resize children
            while(!finalSize.fitsWithin(parentSize)) {
                var maxed = true
                var dW = (finalSize.width - parentSize.width) / children.size
                if(dW == 0) dW = 1

                for (c in children) {
                    val childMinSize = c.minSize.toAbsolute(parentSize)
                    val childPrefSize = c.prefSize.toAbsolute(parentSize)

                    val deltaPref = childPrefSize - childMinSize
                    if (deltaPref.width < dW) {
                        c.size.width = c.minSize.width
                        finalSize.width -= deltaPref.width
                    } else {
                        maxed = false
                        c.size.width -= dW
                        finalSize.width -= dW
                    }
                }

                if(maxed) break
            }

            // Position children
            var x = 0f
            for (c in children) {
                c.pos.x = x
                c.pos.y = (finalSize.height - c.size.height).toFloat() / 2
                x += c.size.width
            }
            break@calc
        }
    }

    fun calculateSizes() {
        calculateSizesVertical()
        return
/*
        val calcMinSize = minSize.copy()

        calc@ while(true) {
            // Keeps track of EXTRA space
            val rowSizes = mutableListOf<UISize>()
            var currSize = calcMinSize.copy()
            rowSizes.add(currSize)

            val rows = mutableListOf<MutableList<UIElement>>()
            var currRow = mutableListOf<UIElement>()
            rows.add(currRow)


            for(c in children) {
                var cPrefSize = c.prefSize.copy()

                if(cPrefSize.width <= 1f) cPrefSize.width *= calcMinSize.width
                if(cPrefSize.height <= 1f) cPrefSize.height *= calcMinSize.height

                // Is there width enough for child on this row?
                if(currSize.width - cMinWidth < 0) {

                    // Child is wider than parent? - resize parent
                    if(cMinWidth > calcMinSize.width) {
                        calcMinSize.width = cMinWidth
                        continue@calc // recalculate
                    }

                    // Make a new row
                    currSize = calcMinSize.copy()
                    rowSizes.add(currSize)
                    currRow = mutableListOf()
                    rows.add(currRow)
                }
                // Is the row tall enough for child?
                if(currSize.height - cMinHeight < 0) {
                    // Child is taller than parent? - resize parent
                    if(cMinHeight > calcMinSize.height) {
                        calcMinSize.height = cMinHeight
                        continue@calc // recalculate
                    }

                    // Adjust row height
                    currSize.height = cMinHeight
                }

                // Set child to its minimum size
                c.size.width = cMinWidth
                c.size.height = cMinHeight

                // Add child to row
                currSize.width -= cMinWidth
                currSize.height -= cMinHeight
                currRow.add(c)
            }

            // Resize children
            for((i, row) in rows.withIndex()) {
                val s = rowSizes[i]

                while (s.width > 0) {
                    var maxed = true
                    val dW = s.width / row.size

                    for (c in row) {
                        val deltaPref = c.prefSize.width - c.minSize.width
                        if (deltaPref < dW) {
                            c.size.width = c.prefSize.width
                            s.width -= deltaPref
                        } else {
                            maxed = false
                            s.width -= dW
                            c.size.width += dW
                        }
                    }

                    if (maxed) break
                }
            }

            // Position children
            val rowHeight = size.height / rows.size
            for((i,row) in rows.withIndex()) {
                var x = 0f

                for (c in row) {
                    c.pos.x = x + rowSizes[i].width/2
                    c.pos.y = i * rowHeight + (rowHeight-c.size.height)/2
                    x += c.size.width

                    println("${c.size}")
                }
                println("H: ${rowSizes[i].height}")
            }


            if(!calcMinSize.fitsWithin(maxSize))
                println("WARNING: children do not fit within parent")
            else
                size = calcMinSize

            break
        }*/
    }

    fun add(el: UIElement):UIElement {
        children.add(el)
        return el
    }

    fun render() {
        draw()
        for (c in children)
            c.render()
    }
    abstract fun draw()
}

class Sprite(
    size:UISize = UISize(1f,1f),
    minSize:UISize = size,
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