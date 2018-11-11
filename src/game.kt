package org.patrick.game

import org.lwjgl.opengl.GL11.*
import glm_.*
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import org.patrick.game.engine.*
import org.patrick.game.engine.ui.Sprite
import org.patrick.game.engine.ui.Text

fun main(args: Array<String>) = Engine.start(::setup, ::run)

fun setup() {
    UniformBuffers.set("data2D", glm.ortho(0f, 800f, 600f, 0f, 0f, 1f).toFloatArray())

    glClearColor(0.8f, 0.8f, 0.8f, 1.0f)
    //glPolygonMode( GL_FRONT_AND_BACK, GL_LINE)
}

fun run() {
    val camera = Camera(glm.PIf / 3, Vec3(0, 50, 100))
    camera.activate()

    val terrain = Terrain(Asset.texture("terrain-height.png"), Asset.texture("terrain-diffuse.png"))
    val panel = Sprite(size = Vec2(65, 25),
        slicePoints = Vec4(8,8,8,8)
    )

    var rot = 0f

    Engine.render {
        UniformBuffers.set("data3D", currentCamera.viewProjMat.toFloatArray())

        val modelMat = glm.eulerAngleY(rot)
        var deltaRot = 0.01f * Engine.frameTime
        if(Window.mouseButtonReleased(0))
            deltaRot = -deltaRot
        else if(!Window.mouseButtonReleased(1))
            deltaRot = 0f

        rot += deltaRot
        terrain.draw(modelMat)

        GUI {
            panel.draw()
            Text("${Engine.fps.toInt()} fps", Vec2(5, 5)).draw()
        }
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