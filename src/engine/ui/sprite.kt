package org.patrick.game.engine.ui

import glm_.vec2.Vec2
import glm_.vec4.Vec4
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL31.*
import org.lwjgl.opengl.GL40.GL_PATCHES
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
        //glDrawArraysInstanced(GL_TRIANGLES, 0, 6, count)
        glDrawArraysInstanced(GL_PATCHES, 0, 6, count)
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

class Sprite(
    private val texture: Texture = Asset.texture("panel.png"),
    private val color:Vec4 = Vec4(1, 1, 1, 1),
    private val slicePoints: Vec4 = Vec4(8,8,8,8)
) {
    private val shader = Asset.shader("sprite")

    init { QuadBuffer.init() }
    internal fun draw(pos:Vec2, size:Vec2) {
        shader.use()
        shader.set("pos", pos)
        shader.set("size", size)
        shader.set("slicePoints", slicePoints)
        shader.set("color", color)
        texture.bind(0)
        if(slicePoints.length2() > 0)
            QuadBuffer.draw(9)
        else
            QuadBuffer.draw(1)
    }
}

class UISprite(
    prefSize:UISize = UISize(1f,1f),
    minSize:UISize = prefSize,
    maxSize:UISize = prefSize,
    layout: UILayout = UILayout.HORIZONTAL,
    align: UIAlign = UIAlign.MIDDLE_CENTER,
    val sprite:Sprite = Sprite()
): UIElement(prefSize, minSize, maxSize, layout, align)
{
    override fun draw(pos:Vec2) {
        sprite.draw(pos, size.toVec2())
    }
}