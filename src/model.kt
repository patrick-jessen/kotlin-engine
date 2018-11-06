package org.patrick.game

import com.beust.klaxon.Klaxon
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL30.*
import java.io.File
import java.util.*

private data class GLTFScene(val nodes: List<Int>)
private data class GLTFNode(
    val children: List<Int> = listOf(),
    val matrix: List<Float> = listOf(), // TODO: Identity matrix
    val mesh: Int = -1
)
private data class GLTFMesh(val primitives: List<GLTFMeshPrimitive>)
private data class GLTFMeshPrimitive(
    val attributes: Map<String, Int>,
    val indices: Int
) {
    fun createAttribute(gltf: GLTF, name:String, attribIdx:Int): Int {
        val accessor = gltf.accessors[attributes[name]!!]
        val bufView = gltf.bufferViews[accessor.bufferView]
        val buf = gltf.buffers[bufView.buffer]

        val size = when(accessor.type) {
            "SCALAR"    -> 1
            "VEC2"      -> 2
            "VEC3"      -> 3
            "VEC4"      -> 4
            else        -> throw Exception("Invalid accessor type")
        }

        glBindBuffer(GL_ARRAY_BUFFER, buf.handle)
        glVertexAttribPointer(
            attribIdx, size, accessor.componentType, accessor.normalized,
            bufView.byteStride, bufView.byteOffset + accessor.byteOffset
        )
        glEnableVertexAttribArray(attribIdx)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        return buf.handle
    }
    fun create(gltf: GLTF):Mesh {
        val handle = glGenVertexArrays()
        glBindVertexArray(handle)

        if("POSITION" in attributes) createAttribute(gltf, "POSITION", 0)
        if("NORMAL" in attributes) createAttribute(gltf, "NORMAL", 1)
        if("TEXCOORD_0" in attributes) createAttribute(gltf, "TEXCOORD_0", 2)

        val accessor = gltf.accessors[indices]
        val bufView = gltf.bufferViews[accessor.bufferView]
        val buf = gltf.buffers[bufView.buffer]

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buf.handle)
        glBindVertexArray(0)

        return Mesh(handle, accessor.count, accessor.componentType, bufView.byteOffset+accessor.byteOffset)
    }
}
private data class GLTFAccessor(
    val bufferView: Int,
    val byteOffset: Long = 0,
    val componentType: Int,
    val normalized: Boolean = false,
    val count: Int,
    val type: String
)
private data class GLTFBufferView(
    val buffer: Int,
    val byteOffset: Long,
    val byteLength: Int,
    val byteStride: Int = 0
)
private data class GLTFBuffer(val uri: String) {
    var handle: Int = 0
        get() {
            if(field == 0) {
                val f = File("./assets/$uri")
                val data = when(f.isFile) {
                    true -> f.readBytes()
                    false -> Base64.getDecoder().decode(uri.substring(37))
                }

                val buf = BufferUtils.createByteBuffer(data.size).put(data)
                buf.rewind()

                field = glGenBuffers()
                glBindBuffer(GL_ARRAY_BUFFER, field)
                glBufferData(GL_ARRAY_BUFFER, buf, GL_STATIC_DRAW)
                glBindBuffer(GL_ARRAY_BUFFER, 0)
            }
            return field
        }
}
private data class GLTF(
    val scenes: List<GLTFScene>,
    val nodes: List<GLTFNode>,
    val meshes: List<GLTFMesh>,
    val bufferViews: List<GLTFBufferView>,
    val accessors: List<GLTFAccessor>,
    val buffers: List<GLTFBuffer>
)

class Mesh(val va:Int, val numIdices:Int, val compType:Int, val offset:Long) {
    fun draw() {
        glBindVertexArray(va)
        glDrawElements(GL_TRIANGLES, numIdices, compType, offset)
    }
}

class Model(private val meshes: List<Mesh>) {
    fun draw() = meshes.forEach {it.draw()}
}

fun loadModel(file: String): Model {
    val gltf = Klaxon().parse<GLTF>(File(file))!!

    val meshes = mutableListOf<Mesh>()

    val scene = gltf.scenes[0]
    for(n in scene.nodes) {
        val node = gltf.nodes[n]
        if(node.mesh >= 0) {
            val mesh = gltf.meshes[node.mesh]
            val prim = mesh.primitives[0]
            meshes.add(prim.create(gltf))
        }
    }

    return Model(meshes)
}

fun checkGLError() {
    when(glGetError()) {
        GL_INVALID_ENUM         -> throw Exception("GL_INVALID_ENUM")
        GL_INVALID_VALUE        -> throw Exception("GL_INVALID_VALUE")
        GL_INVALID_OPERATION    -> throw Exception("GL_INVALID_OPERATION")
        GL_STACK_OVERFLOW       -> throw Exception("GL_STACK_OVERFLOW")
        GL_STACK_UNDERFLOW      -> throw Exception("GL_STACK_UNDERFLOW")
        GL_OUT_OF_MEMORY        -> throw Exception("GL_OUT_OF_MEMORY")
        GL_INVALID_FRAMEBUFFER_OPERATION -> throw Exception("GL_INVALID_FRAMEBUFFER_OPERATION")
    }
}