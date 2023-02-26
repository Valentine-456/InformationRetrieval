package utils

import indexes.coordinateInvertedIndex.CoordinateInvertedIndex
import indexes.invertedIndex.InvertedIndex
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

fun writeInvertedIndexToFile(invertedIndex: InvertedIndex, filename: String = "InvertedIndex.txt") {
    val invertedIndexWriter = resolveFileWriter(filename)
    invertedIndex.index.forEach { (key, value) ->
        invertedIndexWriter.write("$key: ")
        value.forEach {
            invertedIndexWriter.write("$it,")
        }
        invertedIndexWriter.newLine()
    }
    invertedIndexWriter.close()
    printStatisticsOfFile(filename)
}

fun writeCoordinateInvertedIndexToFile(invertedIndex: CoordinateInvertedIndex, filename: String = "CoordinateInvertedIndex.txt") {
    val invertedIndexWriter = resolveFileWriter(filename)
    invertedIndex.index.forEach { (key, value) ->
        invertedIndexWriter.write("$key: ")
        invertedIndexWriter.newLine()
        value.forEach {
            invertedIndexWriter.write("${it.first}:")
            it.second.forEach {
                invertedIndexWriter.write("$it,")
            }
            invertedIndexWriter.newLine()
        }
        invertedIndexWriter.newLine()
    }
    invertedIndexWriter.close()
    printStatisticsOfFile(filename)
}