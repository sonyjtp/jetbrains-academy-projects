package com.jba.projects


import java.io.File
import java.io.FileInputStream
import java.time.Instant
import java.time.ZoneId
import java.util.zip.InflaterInputStream

fun main() {
    println("Enter .git directory location:")
    val dir = File(readln())
    println("Enter git object hash:")
    val objHash = readln()
    val file = File("$dir/objects/${objHash.substring(0, 2)}/${objHash.substring(2)}")
    decompress(file)
}

private fun decompress(file: File) {
    val iis = InflaterInputStream(FileInputStream(file))
    var bytes = iis.readBytes()
    val header = mutableListOf<Char>()
    val body = mutableListOf<Char>()
    val lines = mutableMapOf<String, String>()
    var isHeader = true
    var type = ""
    while (bytes.isNotEmpty()) {
        for (byte in bytes) {
            if (isHeader) {
                if (byte.toInt() == 0) {
                    isHeader = false
                    continue
                }
                header.add(Char(byte.toInt()))
                type = header.joinToString("").split(" ").take(1)[0]
            } else {
                if (byte.toInt() < 20) {
                    addToLines(type, lines, String(body.toCharArray()).split(" ", limit = 2))
                    body.clear()
                } else body.add(Char(byte.toInt()))
            }
        }
        bytes = iis.readBytes()
    }
    println("*${type.uppercase()}*")
    printBody(lines)
}

private fun addToLines(type: String, lines: MutableMap<String, String>, parts: List<String>) {
    if (type == "blob") {
        lines["blob"] = lines["blob"]?.let {
            it + "\n" + parts.joinToString(" ")
        } ?: parts.joinToString(" ")
    } else if (parts.size > 1) {
        when (parts[0]) {
            "tree" -> lines[parts[0]] = parts[1]
            "parent" -> lines["parents"] = lines["parents"]?.let { it + " | " + parts[1] } ?: parts[1]
            "author", "committer" -> lines.getOrPut(parts[0]) { "${User(parts)}" }
            else -> lines["commit message"] = lines["commit message"]?.let {
                it + "\n" + parts.joinToString(" ")
            } ?: ("\n" + parts.joinToString(" "))
        }
    }
}

private fun printBody(lines: MutableMap<String, String>) {
    lines.forEach { (key, value) ->
        when (key) {
            "blob" -> println(value)
            else -> println("$key: $value")
        }
    }
}

class User(parts: List<String>) {
    private val name: String
    private val email: String
    private val type: String
    private val upsertTime: String

    init {
        this.type = parts[0]
        val userDetails = parts[1].split(' ')
        this.name = userDetails[0]
        this.email = userDetails[1].substring(1, userDetails[1].length - 1)
        this.upsertTime = epochToIso(userDetails[2].toLong(), userDetails[3])
    }

    private fun epochToIso(epoch: Long, zoneId: String): String {
        val instant = Instant.ofEpochSecond(epoch).atZone(ZoneId.of(zoneId)).toLocalDateTime()
        return "${instant.toLocalDate()} ${instant.toLocalTime()} ${zoneId.substring(0, 3)}:${zoneId.substring(3)}"
    }

    override fun toString(): String {
        return when (type) {
            "author" -> "$name $email original timestamp: $upsertTime"
            else -> "$name $email commit timestamp: $upsertTime"
        }
    }
}
