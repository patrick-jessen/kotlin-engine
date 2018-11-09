package org.patrick.game

import org.lwjgl.opengl.GL11.*
import glm_.*
import glm_.mat4x4.Mat4
import glm_.quat.Quat
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import org.patrick.game.engine.*
import org.patrick.game.engine.ui.Panel

fun main(args: Array<String>) = Window.open(::setup, ::render, ::destroy)

var terrain: Terrain? = null
var camera: Camera? = null
var panel: Panel? = null

fun setup() {
    uniformBuffers.add(UniformBuffer("data3D", 64))
    uniformBuffers.add(UniformBuffer("data2D", 64))
    uniformBuffers[1].set(
        glm.ortho(0f, 800f, 0f, 600f, 0f, 1f).toFloatArray()
    )


    glClearColor(0.8f, 0.8f, 0.8f, 1.0f)

    val projMat = glm.perspective(glm.PIf / 3, 800f / 600f, 0.1f, 100000f)
    camera = Camera(projMat, Vec3(0, 200, 1000), glm.quatIdentity())
    camera!!.activate()

    terrain = Terrain(
        Asset.texture("terrain-height.png"),
        Asset.texture("terrain-diffuse.png")
    )

    panel = Panel(Vec2(0, 0), Vec2(200, 40))

}
var rot = 0f
var camY = -0.7f

var lastTime = System.currentTimeMillis()
var frameCount = 0
var campos = Vec3(0, -200, -1000)

fun render() {
    uniformBuffers[0].set(currentCamera.viewProjMat.toFloatArray())


    val modelMat = glm.eulerAngleY(rot)
    rot += 0.0006f
    //camera!!.pos = glm.rotateY(Vec3(), camera!!.pos, 0.0005f)
    //camera!!.rot = glm.rotate(camera!!.rot, -0.005f, Vec3(0, 1, 0))
    //camera!!.viewMat = glm.lookAt(campos, Vec3(), Vec3(0,1,0))
    terrain!!.draw(modelMat)

    renderUI()

    frameCount++
    if(frameCount > 100) {
        frameCount = 0
        var frameTime = (System.currentTimeMillis() - lastTime) / 100
        if(frameTime == 0L) frameTime = 1L
        //println("$frameTime ms (${1000/frameTime} fps)")
        lastTime = System.currentTimeMillis()
    }
}

fun destroy() {}

fun renderUI() {
    glEnable(GL_BLEND)
    glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    panel!!.draw()
    glDisable(GL_BLEND)
}