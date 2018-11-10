package org.patrick.game

import org.lwjgl.opengl.GL11.*
import glm_.*
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import org.patrick.game.engine.*
import org.patrick.game.engine.ui.Panel
import org.patrick.game.engine.ui.Text

fun main(args: Array<String>) = Engine.start(::setup, ::run)

fun setup() {
    UniformBuffers.add("data3D", 64)
    UniformBuffers.add("data2D", 64)
    UniformBuffers.set("data2D", glm.ortho(0f, 800f, 600f, 0f, 0f, 1f).toFloatArray())

    glClearColor(0.8f, 0.8f, 0.8f, 1.0f)
}

fun run() {
    val projMat = glm.perspective(glm.PIf / 3, 800f / 600f, 0.1f, 100000f)
    val camera = Camera(projMat, Vec3(0, 100, 500), glm.quatIdentity())
    camera.activate()

    val terrain = Terrain(Asset.texture("terrain-height.png"), Asset.texture("terrain-diffuse.png"))
    val panel = Panel(Vec2(100, 100), Vec2(400, 200))

    var rot = 0f

    Engine.render {
        UniformBuffers.set("data3D", currentCamera.viewProjMat.toFloatArray())

        val modelMat = glm.eulerAngleY(rot)
        rot  += 0.0001f * Engine.frameTime
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