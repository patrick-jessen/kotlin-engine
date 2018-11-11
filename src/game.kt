package org.patrick.game

import org.lwjgl.opengl.GL11.*
import glm_.*
import glm_.quat.Quat
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30.*
import org.patrick.game.engine.*
import org.patrick.game.engine.ui.Sprite
import org.patrick.game.engine.ui.Text

fun main(args: Array<String>) = Engine.start(::setup, ::run)

fun setup() {
    UniformBuffers.set("data2D", glm.ortho(0f, 800f, 600f, 0f, 0f, 1f).toFloatArray())

    glClearColor(0.8f, 0.8f, 0.8f, 1.0f)
    //glPolygonMode( GL_FRONT_AND_BACK, GL_LINE)

    TextureSettings.add("h.png",
        internalFormat= GL_RGBA,
        format = GL_RGBA,
        type = GL_FLOAT,
        minFilter = GL_NEAREST,
        magFilter = GL_NEAREST
    )
}

fun run() {
    val camera = Camera(glm.PIf / 3, Vec3(0, 0, 1000))
    camera.activate()

    val terrain = Terrain(Asset.texture("h.png"), Asset.texture("terrain-diffuse.png"))

    var rotX = 0f
    var rotY = 0f
    var lastMousePos = Vec2()

    Engine.render {
        UniformBuffers.set("data3D", currentCamera.viewProjMat.toFloatArray())

        var rot = Quat.angleAxis(rotX, Vec3(1, 0,0))
        rot = rot * Quat.angleAxis(rotY, Vec3(0, 1, 0))
        val modelMat = rot.toMat4()
        if(Window.mouseButtonPressed(0))
            lastMousePos = Window.mousePos
        if(Window.mouseButtonDown(0)) {
            rotY +=  0.001f * Engine.frameTime * (Window.mousePos - lastMousePos).x
            rotX +=  0.001f * Engine.frameTime * (Window.mousePos - lastMousePos).y
            lastMousePos = Window.mousePos
        }
        currentCamera.pos.z -= Window.scroll * 2 * Engine.frameTime


        terrain.draw(modelMat)

//        GUI {
//            Sprite(size = Vec2(65, 25), slicePoints = Vec4(8,8,8,8)).draw()
//            Text("${Engine.fps.toInt()} fps", Vec2(5, 5)).draw()
//        }
    }
}

fun GUI(fn:()->Unit) {
    glDisable(GL_DEPTH_TEST)
    glEnable(GL_BLEND)
    glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    fn()
    glDisable(GL_BLEND)
    glEnable(GL_DEPTH_TEST)
}