package org.patrick.game.engine

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13.*
import javax.imageio.ImageIO
import java.io.File
import java.nio.ByteBuffer

object TextureSettings {
    internal var settings = mutableMapOf<String, TextureSettingsObject>()
    fun add(
        name:String,
        sWrap:Int     = GL_CLAMP_TO_EDGE,
        tWrap:Int     = GL_CLAMP_TO_EDGE,
        minFilter:Int = GL_LINEAR,
        magFilter:Int = GL_LINEAR,
        internalFormat:Int = GL_RGBA,
        format:Int    = GL_RGBA,
        type:Int      = GL_UNSIGNED_BYTE
    ) {
        if(Engine.state != EngineState.SETUP)
            throw Exception("Texture settings must be set in SETUP state")
        settings[name] = TextureSettingsObject(sWrap, tWrap, minFilter, magFilter, internalFormat, format, type)
    }
}
internal class TextureSettingsObject(
    val sWrap:Int     = GL_CLAMP_TO_EDGE,
    val tWrap:Int     = GL_CLAMP_TO_EDGE,
    val minFilter:Int = GL_LINEAR,
    val magFilter:Int = GL_LINEAR,
    val internalFormat:Int = GL_RGBA,
    val format:Int    = GL_RGBA,
    val type:Int      = GL_UNSIGNED_BYTE
)

class Texture internal constructor(file:String): Resource(file, ::Texture) {
    private var handle = 0

    init {
        val settings = TextureSettings.settings[file] ?: TextureSettingsObject()

        val image = ImageIO.read(File("./assets/textures/$file"))
        val pixels = IntArray(image.width * image.height)
        image.getRGB(0, 0, image.width, image.height, pixels, 0, image.width)

        handle = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, handle)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, settings.sWrap)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, settings.tWrap)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, settings.minFilter)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, settings.magFilter)
        glTexImage2D(GL_TEXTURE_2D, 0, settings.internalFormat, image.width, image.height, 0, settings.format,  settings.type, pixels)
        glBindTexture(GL_TEXTURE_2D, 0)

        println("${image.width} x ${image.height}")
        checkGLError()
    }

    fun bind(pos:Int) {
        glActiveTexture(GL_TEXTURE0 + pos)
        glBindTexture(GL_TEXTURE_2D, handle)
    }

    override fun destroy() = glDeleteTextures(handle)
}