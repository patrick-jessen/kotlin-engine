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
    fun canContain(other:UISize): Boolean {
        return width >= other.width && height >= other.height
    }
    fun fit(other:UISize) {
        if(width < other.width) width = other.width
        if(height < other.height) height = other.height
    }
    fun fitWithin(other:UISize) {
        if(width > other.width) width = other.width
        if(height > other.height) height = other.height
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

enum class UILayout {
    VERTICAL, HORIZONTAL
}

abstract class UIElement(
    private val prefSize:UISize,
    private val minSize:UISize,
    private val maxSize:UISize,
    private val layout:UILayout = UILayout.HORIZONTAL
) {
    protected var size = UISize()
    protected var pos = Vec2()
    private val children = mutableListOf<UIElement>()

    fun calculateMinSize(includeSelf:Boolean = false):UISize {
        size = UISize()
        for(c in children) {
            val cMin = c.calculateMinSize(true)
            size.width += cMin.width
            if(cMin.height > size.height)
                size.height = cMin.height
        }

        if(includeSelf)
            size.fit(minSize.toAbsolute(UISize()))
        return size
    }


    fun calculateSizes(avail:UISize) {
        val calcMinSize = calculateMinSize()
        val absPrefSize = prefSize.toAbsolute(avail)
        val absMaxSize = maxSize.toAbsolute(avail)

        if(!calcMinSize.fitsWithin(avail) || !calcMinSize.fitsWithin(absMaxSize))
            println("Warning: children do not fit")

        // Make this element fit within the available space
        size = absPrefSize
        size.fit(calcMinSize)
        size.fitWithin(absMaxSize)
        size.fitWithin(avail)

        // Determine the size of children
        if(children.isNotEmpty()) {
            // Resize children
            val extraSpace = size - calcMinSize
            while(extraSpace.width > 0 || extraSpace.height > 0) {
                var done = true

                for (c in children) {
                    val childSize = c.prefSize.toAbsolute(size)
                    if (extraSpace.width > 0 && childSize.width > c.size.width) {
                        extraSpace.width--
                        c.size.width++
                        done = false
                    }
                    if (extraSpace.height > 0 && childSize.height > c.size.height) {
                        extraSpace.height--
                        c.size.height++
                        done = false
                    }
                    c.calculateSizes(c.size)
                }
                if(done) break
            }


            // Position children
            var x = 0f
            for(c in children) {
                c.pos.x = x
                c.pos.y = (calcMinSize.height-c.size.height).toFloat()/2
                x += c.size.width
            }
        }
    }

    fun add(child: UIElement):UIElement {
        children.add(child)
        return child
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