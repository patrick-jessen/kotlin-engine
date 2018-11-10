package org.patrick.game.engine

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13.*
import javax.imageio.ImageIO
import java.io.File

class TextureSettings(
    val sWrap:Int     = GL_CLAMP_TO_EDGE,
    val tWrap:Int     = GL_CLAMP_TO_EDGE,
    val minFilter:Int = GL_LINEAR,
    val magFilter:Int = GL_LINEAR
)

class Texture internal constructor(file:String, settings:TextureSettings): Resource(file, ::Texture) {
    private var handle = 0

    init {
        val image = ImageIO.read(File("./assets/textures/$file"))
        val pixels = IntArray(image.width * image.height)
        image.getRGB(0, 0, image.width, image.height, pixels, 0, image.width)

        handle = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, handle)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, settings.sWrap)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, settings.tWrap)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, settings.minFilter)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, settings.magFilter)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.width, image.height, 0, GL_RGBA,  GL_UNSIGNED_BYTE, pixels)
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    fun bind(pos:Int) {
        glActiveTexture(GL_TEXTURE0 + pos)
        glBindTexture(GL_TEXTURE_2D, handle)
    }

    override fun destroy() = glDeleteTextures(handle)
}