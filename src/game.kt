package org.patrick.game

import org.lwjgl.opengl.GL11.*
import glm_.*
import glm_.mat4x4.Mat4
import org.patrick.game.engine.*

fun main(args: Array<String>) = Window.open(::setup, ::render, ::destroy)

var model: Model? = null
var shader: Shader? = null
var tex: Texture? = null
var terrain: Terrain? = null

fun setup() {
    glClearColor(0.8f, 0.8f, 0.8f, 1.0f)

//    model = Asset.model("monkey.gltf")
//    shader = Asset.shader("default")
//    tex = Asset.texture("CesiumLogoFlat.png")

    val projMat = glm.perspective(glm.PIf / 2, 800f / 600f, 0.1f, 100f)
    val viewMat = glm.translate(Mat4(), -1f, -1f, -1f)
    val viewProjMat = projMat * viewMat
//    shader!!.set("viewProjMat", viewProjMat)
//    shader!!.set("tex", 0)
//
//    tex!!.bind(0)

    terrain = Terrain(Asset.texture("height.png"))
    Asset.shader("terrain").set("viewProjMat", viewProjMat)
    Asset.shader("terrain").set("tex", 0)
}
var rot = 0f
var camY = -0.7f
fun render() {
    val projMat = glm.perspective(glm.PIf / 2, 800f / 600f, 0.1f, 100000f)
    val viewMat = glm.translate(Mat4(), 0f, -200f, -1500f)
    val viewProjMat = projMat * viewMat
    Asset.shader("terrain").set("viewProjMat", viewProjMat)

    //camY += 0.00001f

    val modelMat = glm.eulerAngleY(rot)
    rot += 0.01f
//    shader!!.set("modelMat", modelMat)

//    shader!!.use()
//    tex!!.bind(0)
//    model!!.draw()

    //val modelMat = Mat4()
    Asset.shader("terrain").set("modelMat", modelMat)
    terrain!!.draw()
}

fun destroy() {

}