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


    fun calculateSizes(avail:UISize, max:Boolean):UISize {
        val absMinSize = minSize.toAbsolute(avail)
        val absPrefSize = prefSize.toAbsolute(avail)
        val absMaxSize = maxSize.toAbsolute(avail)

        // Parent cannot contain this element
        if(!avail.canContain(absMinSize)) {
            if(max)
                println("Warning: child does not fit within parent")
            else {
                // Expand parent
                val dif = absMinSize - avail
                if(dif.width < 0) dif.width = 0
                if(dif.height < 0) dif.height = 0
                return dif
            }
        }

        // Make this element fit within the available space
        var thisSize = absPrefSize.copy()
        thisSize.fitWithin(avail)
        val thisMaxSize = absMaxSize.copy()
        thisMaxSize.fitWithin(avail)

        // Determine the size of children
        if(children.isNotEmpty()) {
            var done = false
            while(!done) {
                var totalPrefWidth = 0f
                for (c in children) {
                    totalPrefWidth += c.prefSize.toAbsolute(thisSize).width
                }

                for (c in children) {
                    val availWidth = (c.prefSize.toAbsolute(thisSize).width / totalPrefWidth * thisSize.width).toInt()
                    println("AvailWidth: $availWidth")
                    val needs = c.calculateSizes(UISize(availWidth, thisSize.height), false)
                    thisSize += needs
                    if(needs.width == 0 && needs.height == 0) done = true
                }
            }

            // Children do not fit
            if(!thisSize.fitsWithin(thisMaxSize)) {
                if (max || thisMaxSize == absMaxSize) {
                    println("Warning: children do not fit within parent")
                    thisSize = thisMaxSize
                }
                else {
                    // Expand parent
                    return thisMaxSize - thisSize
                }
            }

            // Position children
            var x = 100f
            for(c in children) {
                c.pos.x = x
                c.pos.y = 10f
                x += c.size.width

                println("{${c.pos}, ${c.size}}")
            }
        }

        size = thisSize

        // No extra space is needed
        return UISize()
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