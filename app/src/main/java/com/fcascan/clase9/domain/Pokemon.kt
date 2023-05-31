package com.fcascan.clase9.domain

class Pokemon {
    var id: Int = 0
    var name: Map<String, String>? = null
    var type: List<String>? = null
    var base: Map<String, Int>? = null
    var imageURL: String? = null

    override fun toString(): String {
        return """#$id - ${name?.get("english")}"""
    }
}