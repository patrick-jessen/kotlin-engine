package org.patrick.game.engine.gl

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL43.*

class GLTexture {
    private var handle = 0

    fun upload(
        width   :Int,
        height  :Int,
        data    :IntArray,
        format  :Format   = Format.RGBA,
        wrap    :Wrap     = Wrap.REPEAT,
        filter  :Filter   = Filter.LINEAR
    ) {
        val wrapGL = when(wrap) {
            Wrap.REPEAT     -> GL_REPEAT
            Wrap.CLAMP      -> GL_CLAMP_TO_EDGE
        }
        val minFilterGL = when(filter) {
            Filter.NEAREST  -> GL_NEAREST
            Filter.LINEAR   -> GL_LINEAR
            Filter.BILINEAR -> GL_LINEAR_MIPMAP_NEAREST
            Filter.TRILINEAR-> GL_LINEAR_MIPMAP_LINEAR
        }
        val magFilterGL = when(filter) {
            Filter.NEAREST  -> GL_NEAREST
            else -> GL_LINEAR
        }
        val internalFormatGL = when(format) {
            Format.RGBA -> GL_RGBA
            Format.R16  -> GL_R16
        }
        val formatGL = when(format) {
            Format.RGBA -> GL_RGBA
            Format.R16  -> GL_RED
        }
        val typeGL = when(format) {
            Format.RGBA -> GL_UNSIGNED_BYTE
            Format.R16  -> GL_UNSIGNED_SHORT
        }

        handle = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, handle)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapGL)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapGL)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilterGL)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilterGL)
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormatGL, width, height, 0, formatGL, typeGL, data)

        if(filter == Filter.BILINEAR || filter == Filter.TRILINEAR)
            glGenerateMipmap(handle)

        glBindTexture(GL_TEXTURE_2D, 0)
    }

    fun bind(pos:Int) {
        glActiveTexture(GL_TEXTURE0 + pos)
        glBindTexture(GL_TEXTURE_2D, handle)
    }

    fun free() = glDeleteTextures(handle)

    enum class Wrap     { REPEAT, CLAMP }
    enum class Filter   { NEAREST, LINEAR, BILINEAR, TRILINEAR }
    enum class Format   { RGBA, R16 }
}