package org.patrick.game.engine

import com.beust.klaxon.Klaxon
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL30.*
import java.io.File
import java.util.*

class Model internal constructor(file:String): Resource(file, ::Model) {
    private var meshes = mutableListOf<Mesh>()

    init{
        val gltf = Klaxon().parse<GLTF>(File("./assets/models/$file"))!!
        val scene = gltf.scenes[0]
        for(n in scene.nodes) {
            val node = gltf.nodes[n]
            if(node.mesh >= 0) {
                val mesh = gltf.meshes[node.mesh]
                val prim = mesh.primitives[0]
                meshes.add(Mesh(gltf, prim))
            }
        }
    }

    fun draw() = meshes.forEach {it.draw()}
    override fun destroy() = meshes.forEach{it.destroy()}
}

internal class Buffer(uri: String) : Resource("", ::Buffer) {
    internal var handle = 0

    init {
        val f = File("./assets/models/$uri")
        val data = when(f.isFile) {
            true -> f.readBytes()
            false -> Base64.getDecoder().decode(uri.substring(37))
        }
        val buf = BufferUtils.createByteBuffer(data.size).put(data)
        buf.rewind()
        handle = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, handle)
        glBufferData(GL_ARRAY_BUFFER, buf, GL_STATIC_DRAW)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    override fun destroy() = glDeleteBuffers(handle)
}
private class Mesh(gltf: GLTF, mp: GLTFMeshPrimitive) {
    private var handle = 0
    private var vertBuffer:Buffer? = null
    private var normBuffer:Buffer? = null
    private var texCoordBuffer:Buffer? = null
    private var idxBuffer:Buffer? = null
    private var numIndices = 0
    private var compType = 0
    private var offset = 0L

    init {
        handle = glGenVertexArrays()
        glBindVertexArray(handle)
        if("POSITION" in mp.attributes)
            vertBuffer = bindAttributeBuffer(gltf, mp, "POSITION", 0)
        if("NORMAL" in mp.attributes)
            normBuffer = bindAttributeBuffer(gltf, mp, "NORMAL", 1)
        if("TEXCOORD_0" in mp.attributes)
            texCoordBuffer = bindAttributeBuffer(gltf, mp, "TEXCOORD_0", 2)
        val accessor = gltf.accessors[mp.indices]
        val bufView = gltf.bufferViews[accessor.bufferView]
        val buf = gltf.buffers[bufView.buffer]
        buf.buffer!!.capture()
        idxBuffer = buf.buffer
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buf.buffer!!.handle)
        glBindVertexArray(0)
        numIndices = accessor.count
        compType = accessor.componentType
        offset = bufView.byteOffset+accessor.byteOffset
    }

    private fun bindAttributeBuffer(gltf: GLTF, mp:GLTFMeshPrimitive, name:String, attribIdx:Int):Buffer? {
        val accessor = gltf.accessors[mp.attributes[name]!!]
        val bufView = gltf.bufferViews[accessor.bufferView]
        val buf = gltf.buffers[bufView.buffer]
        buf.buffer!!.capture()

        val size = when(accessor.type) {
            "SCALAR"    -> 1
            "VEC2"      -> 2
            "VEC3"      -> 3
            "VEC4"      -> 4
            else        -> throw Exception("Invalid accessor type")
        }

        glBindBuffer(GL_ARRAY_BUFFER, buf.buffer!!.handle)
        glVertexAttribPointer(
            attribIdx, size, accessor.componentType, accessor.normalized,
            bufView.byteStride, bufView.byteOffset + accessor.byteOffset
        )
        glEnableVertexAttribArray(attribIdx)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        return buf.buffer
    }

    fun draw() {
        glBindVertexArray(handle)
        glDrawElements(GL_TRIANGLES, numIndices, compType, offset)
    }

    fun destroy() {
        vertBuffer?.release()
        normBuffer?.release()
        texCoordBuffer?.release()
        idxBuffer?.release()
    }
}

internal data class GLTFScene(val nodes: List<Int>)
internal data class GLTFNode(
    val children: List<Int> = listOf(),
    val matrix: List<Float> = listOf(), // TODO: Identity matrix
    val mesh: Int = -1
)
internal data class GLTFMesh(val primitives: List<GLTFMeshPrimitive>)
internal data class GLTFMeshPrimitive(val attributes: Map<String, Int>, val indices: Int)
internal data class GLTFAccessor(
    val bufferView: Int,
    val byteOffset: Long = 0,
    val componentType: Int,
    val normalized: Boolean = false,
    val count: Int,
    val type: String
)
internal data class GLTFBufferView(
    val buffer: Int,
    val byteOffset: Long,
    val byteLength: Int,
    val byteStride: Int = 0
)
internal data class GLTFBuffer(val uri: String) {
    var buffer:Buffer? = null
        get() {
            if(field == null) field = Buffer(uri)
            return field
        }
}
internal data class GLTF(
    val scenes: List<GLTFScene>,
    val nodes: List<GLTFNode>,
    val meshes: List<GLTFMesh>,
    val bufferViews: List<GLTFBufferView>,
    val accessors: List<GLTFAccessor>,
    val buffers: List<GLTFBuffer>
)