package org.patrick.game.engine

import glm_.glm
import glm_.mat4x4.Mat4
import glm_.quat.Quat
import glm_.vec3.Vec3

internal var currentCamera: Camera = Camera(Mat4(),Vec3(),Quat())

class Camera(var projMat: Mat4, var pos:Vec3, var rot:Quat) {
    val viewMat: Mat4
        get() = glm.translate(Mat4(), -pos)
    val viewProjMat: Mat4
        get() = projMat * viewMat

    fun activate() { currentCamera = this }
}