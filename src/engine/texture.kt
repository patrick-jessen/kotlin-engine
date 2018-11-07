package org.patrick.game.engine

import org.lwjgl.opengl.GL13.*
import javax.imageio.ImageIO
import java.io.File

private val cache = mutableMapOf<String, Texture>()
fun getTexture(file:String): Texture {
    val tex: Texture?
    if (file in cache)
        tex = cache[file]!!
    else {
        tex = Texture(file)
        cache[file] = tex
    }
    tex.reference()
    return tex
}

private fun loadTexture(file:String):Int {
    val image = ImageIO.read(File(file))
    val pixels = IntArray(image.width * image.height)
    image.getRGB(0, 0, image.width, image.height, pixels, 0, image.width)

    val handle = glGenTextures()
    glBindTexture(GL_TEXTURE_2D, handle)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.width, image.height, 0, GL_RGBA,  GL_UNSIGNED_BYTE, pixels)
    glBindTexture(GL_TEXTURE_2D, 0)
    return handle
}

class Texture: RefCounter {
    private var file = ""
    private var handle = 0

    constructor()
    internal constructor(file:String) {
        this.file = file
        handle = loadTexture(file)
    }

    fun bind(pos:Int) {
        glActiveTexture(GL_TEXTURE0 + pos)
        glBindTexture(GL_TEXTURE_2D, handle)
    }

    override fun destroy() {
        glDeleteTextures(handle)
        cache.remove(file)
    }
}