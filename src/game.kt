package org.patrick.game

import glm_.*
import glm_.quat.Quat
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import org.lwjgl.opengl.GL30.*
import org.patrick.game.engine.*
import org.patrick.game.engine.ui.*

fun main(args: Array<String>) = Engine.start(::setup, ::run)

fun setup() {
    glClearColor(0.8f, 0.8f, 0.8f, 1.0f)
    //glPolygonMode( GL_FRONT_AND_BACK, GL_LINE)

    ShaderSettings.add("sprite", arrayOf("tex"))
    TextureSettings.add("questlog/close.png", format = GL_BGRA)
}

fun run() {
    val camera = Camera(glm.PIf / 3, Vec3(0, 0, 1000))
    camera.activate()

    val terrain = Terrain(Asset.texture("terrain-height.png"), Asset.texture("terrain-diffuse.png"))

    var rotX = 0f
    var rotY = 0f
    var lastMousePos = Vec2()

    //UI ///////////////////////////////////////////////////////
    val panelSprite = Sprite(Asset.texture("questlog/panel.png"))

    val ui = UIElement(align = UIAlign.MIDDLE_LEFT)
    val questLog = ui.add(UISprite(
        prefSize = UISize(0.5f, 300),
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
    closeContainer.add(UISprite(
        prefSize = UISize(24, 22),
        sprite = Sprite(Asset.texture("questlog/close.png"))
    ))

    UI.root = ui
    UI.calculateSizes()

    //////////////////////////////////////////////////////////

    Engine.render {
        UniformBuffers.set("data3D", currentCamera.viewProjMat.toFloatArray())

        var rot = Quat.angleAxis(rotX, Vec3(1, 0,0))
        rot = rot * Quat.angleAxis(rotY, Vec3(0, 1, 0))
        val modelMat = rot.toMat4()
        if(Window.mouseButtonPressed(0))
            lastMousePos = Window.mousePos
        if(Window.mouseButtonDown(0)) {
            rotY +=  0.001f * Engine.frameTime * (Window.mousePos - lastMousePos).x
            rotX +=  0.001f * Engine.frameTime * (Window.mousePos - lastMousePos).y
            lastMousePos = Window.mousePos
        }
        currentCamera.pos.z -= Window.scroll * 2 * Engine.frameTime
        terrain.draw(modelMat)
    }
}