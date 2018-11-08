package org.patrick.game

import org.lwjgl.opengl.GL11.*
import glm_.*
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import org.patrick.game.engine.*

fun main(args: Array<String>) = Window.open(::setup, ::render, ::destroy)

var terrain: Terrain? = null
var camera: Camera? = null

fun setup() {
    glClearColor(0.8f, 0.8f, 0.8f, 1.0f)

    val projMat = glm.perspective(glm.PIf / 2, 800f / 600f, 0.1f, 100000f)
    camera = Camera(projMat, Vec3(0, 50, 500), glm.quatIdentity())
    camera!!.activate()

    terrain = Terrain(
        Asset.texture("terrain-height.png"),
        Asset.texture("terrain-diffuse.png")
    )
}
var rot = 0f
var camY = -0.7f

var lastTime = System.currentTimeMillis()
var frameCount = 0

fun render() {
    val modelMat = glm.eulerAngleY(rot)
    rot += 0.0002f

    terrain!!.draw(modelMat)

    frameCount++
    if(frameCount > 100) {
        frameCount = 0
        val frameTime = (System.currentTimeMillis() - lastTime) / 100
        println("$frameTime ms (${1000/frameTime} fps)")
        lastTime = System.currentTimeMillis()
    }
}

fun destroy() {}