package com.bizilabs.streeek.lib.remote.helpers

import com.bizilabs.streeek.lib.remote.client.TestJson
import com.goncalossilva.resources.Resource

class JsonLoader {
    private val json = TestJson

    fun load(file: String): String {
        val loader = Resource("src/commonTest/resources/$file")
        return loader.readText()
    }

    internal inline fun <reified R : Any> load(file: String) = this.load(file).convertToDataClass<R>()

    internal inline fun <reified R : Any> String.convertToDataClass(): R {
        return json.decodeFromString<R>(this)
    }
}
