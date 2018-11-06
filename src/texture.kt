package org.patrick.game

import org.lwjgl.opengl.GL13.*
import javax.imageio.ImageIO
import java.io.File


fun loadTex() {
    val imageFile = File("./assets/CesiumLogoFlat.png")
    val image = ImageIO.read(imageFile)

    val pixels = IntArray(image.width * image.height)
    image.getRGB(0, 0, image.width, image.height, pixels, 0, image.width)

    val tex = glGenTextures()
    glActiveTexture(GL_TEXTURE0)
    glBindTexture(GL_TEXTURE_2D, tex)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.width, image.height, 0, GL_RGBA,  GL_UNSIGNED_BYTE, pixels)

    //glBindTexture(GL_TEXTURE_2D, 0)
}