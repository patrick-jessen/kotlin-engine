package org.patrick.game.engine

import glm_.glm
import glm_.mat4x4.Mat4
import glm_.quat.Quat
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import kotlin.math.cos

private const val nearClip = 0.1f
private const val farClip = 100000f
internal var currentCamera: Camera = FPSCamera()

abstract class Camera(private val fov:Float) {
    private var viewPortSize:Pair<Int,Int> = Pair(0,0)
    private var projMat:Mat4 = Mat4()
        get() {
            if(viewPortSize == Window.size) return field
            viewPortSize = Window.size
            val aspect = viewPortSize.first.toFloat() / viewPortSize.second
            field = glm.perspective(fov, aspect, nearClip, farClip)
            return field
        }

    abstract val viewMat: Mat4
    val viewProjMat: Mat4
        get() = projMat * viewMat

    fun activate() { currentCamera = this }
}

class FPSCamera(
    fov:Float = glm.PIf/4,
    var pos:Vec3 = Vec3(),
    rotX:Float = 0f,
    var rotY:Float = 0f
):Camera(fov) {
    override val viewMat: Mat4
        get() = glm.rotateX(Mat4(), rotX) * glm.rotateY(Mat4(), rotY) * glm.translate(Mat4(), -pos)

    var rotX:Float = rotX
        set(value) {
            field = when {
                value > glm.HPIf -> glm.HPIf
                value < -glm.HPIf -> -glm.HPIf
                else -> value
            }
        }

    fun move(dir: Vec2, speed:Float) {
        if(dir.length2() == 0f) return

        val cosY = glm.cos(rotY)
        val sinY = glm.sin(rotY)
        val cosX = glm.cos(rotX)
        val sinX = glm.sin(rotX)

        val move = Vec3(cosX * sinY, -sinX,-cosX * cosY)
        val strafe = Vec3(cosY,0, sinY)

        val total = move*dir.x + strafe*dir.y
        pos = pos + total.normalizeAssign() * speed
    }
}