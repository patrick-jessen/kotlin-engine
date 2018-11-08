package org.patrick.game.engine

import org.lwjgl.opengl.GL13.*
import javax.imageio.ImageIO
import java.io.File

class Texture internal constructor(file:String): Resource(file, ::Texture) {
    private var handle = 0

    init {
        val image = ImageIO.read(File(file))
        val pixels = IntArray(image.width * image.height)
        image.getRGB(0, 0, image.width, image.height, pixels, 0, image.width)

        handle = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, handle)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.width, image.height, 0, GL_RGBA,  GL_UNSIGNED_BYTE, pixels)
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    fun bind(pos:Int) {
        glActiveTexture(GL_TEXTURE0 + pos)
        glBindTexture(GL_TEXTURE_2D, handle)
    }

    override fun destroy() = glDeleteTextures(handle)
}