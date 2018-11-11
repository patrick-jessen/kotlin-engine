package org.patrick.game.engine

import glm_.glm
import glm_.mat4x4.Mat4
import glm_.quat.Quat
import glm_.vec3.Vec3
import glm_.vec4.Vec4

private const val nearClip = 0.1f
private const val farClip = 100000f
internal var currentCamera: Camera = Camera()

class Camera(
    private val fov:Float = glm.PIf/3,
    var pos:Vec3 = Vec3(),
    var rot:Quat = glm.quatIdentity()
) {
    private var viewPortSize:Pair<Int,Int> = Pair(0,0)
    private var projMat:Mat4 = Mat4()
        get() {
            if(viewPortSize == Window.size) return field
            viewPortSize = Window.size
            val aspect = viewPortSize.first.toFloat() / viewPortSize.second
            field = glm.perspective(fov, aspect, nearClip, farClip)
            return field
        }

    val viewMat: Mat4
        get() = rot.toMat4() *  glm.translate(Mat4(), -pos)
    val viewProjMat: Mat4
        get() = projMat * viewMat

    fun activate() { currentCamera = this }
}