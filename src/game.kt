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

var tessLevel = 5f

fun main(args: Array<String>) = Engine.start(::setup, ::run)

fun setup() {
    glClearColor(0.8f, 0.8f, 0.8f, 1.0f)

    ShaderSettings.add("sprite", arrayOf("tex"))

    glPointSize(5f)
    glPatchParameteri(GL_PATCH_VERTICES, 3)
    glPatchParameterfv(GL_PATCH_DEFAULT_OUTER_LEVEL, floatArrayOf(tessLevel, tessLevel, tessLevel, 1f))
    glPatchParameterfv(GL_PATCH_DEFAULT_INNER_LEVEL, floatArrayOf(tessLevel, 1f))
    Asset.shader("terrain").set("tessLevel", tessLevel)
    GLCheckError()
}

fun run() {
    val camera = currentCamera as FPSCamera
    camera.pos = Vec3(0, 5, 15)

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


    Engine.render {
        val tessPos = Vec3(camera.pos.x, 0, camera.pos.z)
        UniformBuffers.set("data3D", tessPos.toFloatArray(), 64L)
        println(tessPos)

        if(Window.keyDown(keyCtrl)) {
            if (Window.keyReleased(keyA)) {
                tessLevel++
                glPatchParameterfv(GL_PATCH_DEFAULT_OUTER_LEVEL, floatArrayOf(tessLevel, tessLevel, tessLevel, 1f))
                glPatchParameterfv(GL_PATCH_DEFAULT_INNER_LEVEL, floatArrayOf(tessLevel, 1f))
                println("Tess: $tessLevel")
                Asset.shader("terrain").set("tessLevel", tessLevel)
            } else if (Window.keyReleased(keyB)) {
                tessLevel--
                glPatchParameterfv(GL_PATCH_DEFAULT_OUTER_LEVEL, floatArrayOf(tessLevel, tessLevel, tessLevel, 1f))
                glPatchParameterfv(GL_PATCH_DEFAULT_INNER_LEVEL, floatArrayOf(tessLevel, 1f))
                println("Tess: $tessLevel")
                Asset.shader("terrain").set("tessLevel", tessLevel)
            }
            if (Window.keyReleased(keyW)) {
                wireframe = !wireframe
                if (wireframe) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
                else glPolygonMode(GL_FRONT_AND_BACK, GL_FILL)
            }
        }
        else {
            var move = 0
            var strafe = 0
            if(Window.keyDown(keyW)) move += 1
            if(Window.keyDown(keyS)) move -= 1
            if(Window.keyDown(keyA)) strafe -= 1
            if(Window.keyDown(keyD)) strafe += 1
            camera.move(Vec2(move, strafe), 0.2f)
        }
        if(Window.mouseButtonPressed(1))
            lastMousePos = Window.mousePos
        if(Window.mouseButtonDown(1)) {
            camera.rotY += 0.0002f * Engine.frameTime * (Window.mousePos - lastMousePos).x
            camera.rotX += 0.0002f * Engine.frameTime * (Window.mousePos - lastMousePos).y
            lastMousePos = Window.mousePos
        }

        terrain.draw(Mat4())
    }
}