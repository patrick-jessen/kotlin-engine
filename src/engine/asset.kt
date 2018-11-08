package org.patrick.game.engine

import kotlin.reflect.KFunction

object Asset {
    private val textures = mutableMapOf<String, Texture>()
    private val shaders = mutableMapOf<String, Shader>()
    private val models = mutableMapOf<String, Model>()

    fun texture(file:String): Texture {
        return when(file in textures) {
            true -> textures[file]!!
            false -> {
                val tex = Texture(file)
                textures[file] = tex
                tex
            }
        }
    }
    fun shader(file:String): Shader {
        return when(file in shaders) {
            true -> shaders[file]!!
            false -> {
                val shader = Shader(file)
                shaders[file] = shader
                shader
            }
        }
    }
    fun model(file:String): Model {
        return when(file in models) {
            true -> models[file]!!
            false -> {
                val model = Model(file)
                models[file] = model
                model
            }
        }
    }

    internal fun remove(id:String, type:KFunction<Any>) {
        when(type) {
            ::Texture -> textures.remove(id)
            ::Shader -> shaders.remove(id)
            ::Model -> models.remove(id)
        }
    }
}

abstract class Resource internal constructor(private val id:String, private val type:KFunction<Any>) {
    private var refCount = 0

    internal fun capture() = refCount++
    internal fun release() {
        if(--refCount == 0) {
            Asset.remove(id, type)
            destroy()
        }
    }
    protected abstract fun destroy()
}