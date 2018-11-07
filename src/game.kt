package org.patrick.game

import org.lwjgl.opengl.GL11.*
import glm_.*
import glm_.mat4x4.Mat4
import org.patrick.game.engine.*

fun main(args: Array<String>) = Window.open(::setup, ::render, ::destroy)

var model = Model()
var shader = Shader()
var tex = Texture()

fun setup() {
    glClearColor(0.8f, 0.8f, 0.8f, 1.0f)

    model = getModel("./assets/models/monkey.gltf")
    shader = getShader("./assets/shaders/default")
    tex = getTexture("./assets/textures/CesiumLogoFlat.png")

    val projMat = glm.perspective(glm.PIf / 2, 800f / 600f, 0.1f, 100f)
    val viewMat = glm.translate(Mat4(), 0f, 0f, -5f)
    val viewProjMat = projMat * viewMat
    shader.set("viewProjMat", viewProjMat)
    shader.set("tex", 0)

    tex.bind(0)
}
var rot = 0f
fun render() {
    val modelMat = glm.eulerAngleY(rot)
    rot += 0.0001f
    shader.set("modelMat", modelMat)

    shader.use()
    model.draw()
}

fun destroy() {
    model.dereference()
    shader.dereference()
    tex.dereference()
}