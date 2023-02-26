package utils

import java.io.BufferedWriter
import java.io.File

fun resolveFileWriter(filename: String): BufferedWriter {
    val file = File("./src/main/resources/$filename")
    if (file.exists()) file.delete()
    return file.bufferedWriter()
}

fun printStatisticsOfFile(filename: String) {
    val file = File("./src/main/resources/$filename")
    println("\n${file.name}'s size: (${file.length()} bytes).")
}