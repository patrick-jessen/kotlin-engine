package org.patrick.game.engine

import glm_.func.common.ceil
import org.patrick.game.engine.gl.*
import java.awt.image.DataBufferByte
import java.awt.image.DataBufferInt
import javax.imageio.ImageIO
import java.io.File

class Texture internal constructor(file:String): Resource(file, ::Texture) {
    private var tex = GLTexture()

    init {
        val data:IntArray
        val width:Int
        val height:Int
        var format = GLTexture.Format.RGBA

        val extension = file.substring(file.lastIndexOf("."))
        when(extension) {
            ".png" -> {
                val image = ImageIO.read(File("./assets/textures/$file"))
                width = image.width
                height = image.height

                // determine format
                format = when(image.colorModel.numComponents) {
                    1 -> GLTexture.Format.R16
                    4 -> GLTexture.Format.RGBA
                    else -> throw Exception("Unsupported color model: $file")
                }

                // allocate buffer
                var bits = 0
                for(comp in 0 until image.colorModel.numComponents)
                    bits += image.colorModel.getComponentSize(comp)

                val size = (width*height * (bits.toFloat()/Int.SIZE_BITS)).ceil.toInt()
                data = IntArray(size)

                // populate buffer
                var i = 0
                var byte = 0
                val pixel = IntArray(4)
                for(h in 0 until height) {
                    for(w in 0 until width) {
                        image.raster.getPixel(w, h, pixel)
                        when(image.colorModel.numComponents) {
                            1 -> {
                                val lower = pixel[0] and 0xff
                                val upper = (pixel[0] shr 8) and 0xff

                                data[i] = data[i] or (lower shl (byte*8))
                                byte++
                                data[i] = data[i] or (upper shl (byte*8))
                                byte++
                                if(byte == 4) {
                                    i++
                                    byte = 0
                                }
                            }
                            4 -> {
                                data[i] = (pixel[0] shl 0) or (pixel[1] shl 8) or (pixel[2] shl 16) or (pixel[3] shl 24)
                                i++
                            }
                        }
                    }
                }
            }
            else -> throw Exception("Texture file not supported: $file")
        }

        tex.upload(width, height, data, format)
        GLCheckError("Failed to create texture: $file")
    }

    fun bind(pos:Int) = tex.bind(pos)
    override fun destroy() = tex.free()
}