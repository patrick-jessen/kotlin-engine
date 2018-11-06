package org.patrick.game

import org.lwjgl.opengl.GL11.*
import org.patrick.game.gltf.*
import glm_.*
import glm_.mat4x4.Mat4

fun main(args: Array<String>) = Window.open(::setup, ::render)

var model: Model? = null
var shader = Shader()


fun setup() {
    glClearColor(0.8f, 0.8f, 0.8f, 1.0f)

    model = loadModel("./assets/BoxTextured.gltf")
    shader.load(vertSrc, fragSrc)

    val projMat = glm.perspective(glm.PIf / 2, 800f / 600f, 0.1f, 100f)
    val viewMat = glm.translate(Mat4(), 0f, 0f, -5f)
    val viewProjMat = projMat * viewMat
    shader.set("viewProjMat", viewProjMat)
    shader.set("tex", 0)

    loadTex()
}
var red = 0f
var rot = 0f

fun render() {
    shader.set("red", red)
    red += 0.0001f
    if(red > 1) red = 0f

    val modelMat = glm.eulerAngleY(rot)
    rot += 0.0001f
    shader.set("modelMat", modelMat)

    shader.use()
    model!!.draw()
}

const val vertSrc = """
#version 330 core
layout (location = 0) in vec3 vertPos;
layout (location = 1) in vec3 vertNorm;
layout (location = 2) in vec2 vertUV;

uniform mat4 viewProjMat;
uniform mat4 modelMat;

out vec3 fragNorm;
out vec2 fragUV;

void main() {
  gl_Position = viewProjMat * modelMat * vec4(vertPos, 1.0);
  fragNorm = vec3(modelMat * vec4(vertNorm, 1.0));
  fragUV = vertUV;
}"""

const val fragSrc = """
#version 330 core
out vec4 fragColor;

in vec3 fragNorm;
in vec2 fragUV;
uniform sampler2D tex;

void main() {
  float power = dot(fragNorm, normalize(vec3(1, 1, 1)));
  //fragColor = vec4(fragUV.x * power, fragUV.y * power, 0, 1);
  fragColor = texture(tex, fragUV) * max(power, 0.2);
}
"""

val vertexData = floatArrayOf(
    -1.0f, -1.0f, 0.0f,
    1.0f, -1.0f, 0.0f,
    0.0f,  1.0f, 0.0f
)