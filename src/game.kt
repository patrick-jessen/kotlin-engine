package org.patrick.game

import glm_.*
import glm_.mat4x4.Mat4
import glm_.quat.Quat
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL43.*
import org.patrick.game.engine.*
import org.patrick.game.engine.gl.GLCheckError
import org.patrick.game.engine.ui.*

const val keyCtrl = 341
const val keyW = 87
const val keyA = 65
const val keyB = 66
const val keyD = 68
const val keyS = 83

fun main(args: Array<String>) = Engine.start(::setup, ::run)

fun setup() {
    glClearColor(0.8f, 0.8f, 0.8f, 1.0f)

    ShaderSettings.add("sprite", arrayOf("tex"))

    glPointSize(5f)
    glPatchParameteri(GL_PATCH_VERTICES, 3)
    glPatchParameterfv(GL_PATCH_DEFAULT_OUTER_LEVEL, floatArrayOf(1f, 1f, 1f, 1f))
    glPatchParameterfv(GL_PATCH_DEFAULT_INNER_LEVEL, floatArrayOf(1f, 1f))
    GLCheckError()
}

fun run() {
    val camera = Camera(glm.PIf / 3, Vec3(5, 0, 15))
    camera.activate()

    val terrain = Terrain(Asset.texture("terrain/height.png"), Asset.texture("terrain/diffuse.png"))

    var rotX = 0f
    var rotY = 0f
    var lastMousePos = Window.mousePos

    //UI ///////////////////////////////////////////////////////
    val panelSprite = Sprite(Asset.texture("questlog/panel.png"))

    val ui = UIElement(align = UIAlign.MIDDLE_LEFT)
    val questLog = ui.add(UISprite(
        prefSize = UISize(0.2f, 300),
        align = UIAlign.TOP_LEFT,
        layout = UILayout.VERTICAL,
        sprite = panelSprite
    ))

    val header = questLog.add(UIElement(
        prefSize = UISize(1f, 26),
        align = UIAlign.TOP_CENTER
    ))
    val headerTextPanel = header.add(UISprite(
        prefSize = UISize(1f, 1f),
        sprite = panelSprite
    ))
    headerTextPanel.add(Text("Quest Log", color = Vec4(1,1,1,1)))
    val closeContainer = header.add(UISprite(
        prefSize = UISize(28, 1f),
        sprite = panelSprite
    ))
    val closeBtn = closeContainer.add(UISprite(
        prefSize = UISize(24, 22),
        sprite = Sprite(Asset.texture("questlog/close.png"))
    ))
    closeBtn.onClick = {println("Close clicked")}

    //UI.root = ui
    UI.calculateSizes()

    //////////////////////////////////////////////////////////
    var wireframe = false
    var tessLevel = 1f

    Engine.render {
        UniformBuffers.set("data3D", currentCamera.viewProjMat.toFloatArray())

        if(Window.keyDown(keyCtrl)) {
            if (Window.keyReleased(keyA)) {
                tessLevel++
                glPatchParameterfv(GL_PATCH_DEFAULT_OUTER_LEVEL, floatArrayOf(tessLevel, tessLevel, tessLevel, 1f))
                glPatchParameterfv(GL_PATCH_DEFAULT_INNER_LEVEL, floatArrayOf(tessLevel, 1f))
            } else if (Window.keyReleased(keyB)) {
                tessLevel--
                glPatchParameterfv(GL_PATCH_DEFAULT_OUTER_LEVEL, floatArrayOf(tessLevel, tessLevel, tessLevel, 1f))
                glPatchParameterfv(GL_PATCH_DEFAULT_INNER_LEVEL, floatArrayOf(tessLevel, 1f))
            }
            if (Window.keyReleased(keyW)) {
                wireframe = !wireframe
                if (wireframe) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
                else glPolygonMode(GL_FRONT_AND_BACK, GL_FILL)
            }
        }
        else {
            var moveVec = Vec3()
            if(Window.keyDown(keyW)) {
                val v = glm.rotate(camera.rot, Vec3(0, 0, 1))
                v.z = -v.z
                moveVec = moveVec + v
            }
            if(Window.keyDown(keyS)) {
                val v = glm.rotate(camera.rot, Vec3(0, 0, -1))
                v.z = -v.z
                moveVec = moveVec + v
            }
            if(Window.keyDown(keyA))
                moveVec = moveVec + glm.rotate(camera.rot, Vec3(-1, 0, 0))
            if(Window.keyDown(keyD))
                moveVec = moveVec + glm.rotate(camera.rot, Vec3(1, 0, 0))

            camera.pos = camera.pos + (moveVec * 0.1f)
        }
        if(Window.mouseButtonPressed(0))
            lastMousePos = Window.mousePos
        if(Window.mouseButtonDown(0)) {
            rotY += 0.0001f * Engine.frameTime * (Window.mousePos - lastMousePos).x
            rotX += 0.0001f * Engine.frameTime * (Window.mousePos - lastMousePos).y
            lastMousePos = Window.mousePos

            val rot = Quat.angleAxis(rotX, Vec3(1, 0, 0))
            camera.rot = rot * Quat.angleAxis(rotY, Vec3(0, 1, 0))
        }


//        var rot = Quat.angleAxis(rotX, Vec3(1, 0,0))
//        rot = rot * Quat.angleAxis(rotY, Vec3(0, 1, 0))
//        var modelMat = rot.toMat4()
//        if(Window.mouseButtonPressed(0))
//            lastMousePos = Window.mousePos
//        if(Window.mouseButtonDown(0)) {
//            rotY +=  0.001f * Engine.frameTime * (Window.mousePos - lastMousePos).x
//            rotX +=  0.001f * Engine.frameTime * (Window.mousePos - lastMousePos).y
//            lastMousePos = Window.mousePos
//        }
//        currentCamera.pos.z -= Window.scroll * 2 * Engine.frameTime
        val modelMat = Mat4()
        terrain.draw(modelMat)
    }
}