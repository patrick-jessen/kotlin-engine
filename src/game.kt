package org.patrick.game

import glm_.*
import glm_.quat.Quat
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL43.*
import org.patrick.game.engine.*
import org.patrick.game.engine.gl.GLCheckError
import org.patrick.game.engine.ui.*

fun main(args: Array<String>) = Engine.start(::setup, ::run)

fun setup() {
    glClearColor(0.8f, 0.8f, 0.8f, 1.0f)

    ShaderSettings.add("sprite", arrayOf("tex"))

    glPointSize(5f)
    glPatchParameteri(GL_PATCH_VERTICES, 3)
    GLCheckError()
}

fun run() {
    val camera = Camera(glm.PIf / 3, Vec3(0, 1, 4))
    camera.activate()

    val terrain = Terrain(Asset.texture("terrain/height.png"), Asset.texture("terrain/diffuse.png"))

    var rotX = 0f
    var rotY = 0f
    var lastMousePos = Vec2()

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
        UniformBuffers.set("data3D", currentCamera.viewProjMat.toFloatArray())

        var rot = Quat.angleAxis(rotX, Vec3(1, 0,0))
        rot = rot * Quat.angleAxis(rotY, Vec3(0, 1, 0))
        var modelMat = rot.toMat4()
        if(Window.mouseButtonPressed(0))
            lastMousePos = Window.mousePos
        if(Window.mouseButtonDown(0)) {
            rotY +=  0.001f * Engine.frameTime * (Window.mousePos - lastMousePos).x
            rotX +=  0.001f * Engine.frameTime * (Window.mousePos - lastMousePos).y
            lastMousePos = Window.mousePos
        }
        currentCamera.pos.z -= Window.scroll * 2 * Engine.frameTime

        if(Window.keyDown(65)) {
            Asset.shader("terrain").set("res", 0)
        }
        else if(Window.keyDown(66)) {
            Asset.shader("terrain").set("res", 1)
        }
        else if(Window.keyDown(67)) {
            Asset.shader("terrain").set("res", 2)
        }

        if(Window.keyReleased(87)) {
            wireframe = !wireframe
            if(wireframe) glPolygonMode( GL_FRONT_AND_BACK, GL_LINE)
            else glPolygonMode( GL_FRONT_AND_BACK, GL_FILL)
        }

        terrain.draw(modelMat)
    }
}