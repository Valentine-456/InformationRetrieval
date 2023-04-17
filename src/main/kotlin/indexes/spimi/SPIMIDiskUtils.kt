package indexes.spimi

import utils.FileReaderBytes
import utils.FileWriterBytes
import java.io.BufferedWriter
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*

class SPIMIDiskUtils(private val tempDir: String = "./src/main/resources/temp/") {
    companion object {
        var blockCounter = 1
    }

    init {
        val dir = File(tempDir)
        if (dir.exists()) dir.deleteRecursively()
        dir.mkdir()
    }

    fun writeBlock(map: TreeMap<String, ArrayList<Int>>) {
        val blockFile = this.createBlock()
        val writer = FileWriterBytes(blockFile)
        this.appendToBlock(writer, map)
        writer.close()
    }

    fun appendToBlock(writerBytes: FileWriterBytes, map: TreeMap<String, ArrayList<Int>>) {
        map.forEach { (token, postings) ->
            writerBytes.appendBytesToFile(token.toByteArray(StandardCharsets.UTF_8).size)
            writerBytes.appendBytesToFile(token)
            writerBytes.appendBytesToFile(postings.size)
            postings.forEach {
                writerBytes.appendBytesToFile(it)
            }
        }
    }

    fun appendTextViaBufferedWriter(bufferedWriter: BufferedWriter, map: TreeMap<String, ArrayList<Int>>) {
        map.forEach { (token, postings) ->
            bufferedWriter.write("$token: ")
            postings.forEach {
                bufferedWriter.write("$it,")
            }
            bufferedWriter.newLine()
        }
        bufferedWriter.flush()
    }

    private fun createBlock(): File {
        val newBlockFile = File("$tempDir/block${blockCounter}")
        newBlockFile.createNewFile()
        blockCounter++
        return newBlockFile
    }

    fun getReadingBlocksList(): java.util.ArrayList<FileReaderBytes> {
        val dir = File("$tempDir/").listFiles()
        val blockFiles = dir.map { file ->
            FileReaderBytes(file)
        } as ArrayList
        return blockFiles
    }

    fun readTermFromBlockFile(reader: FileReaderBytes): Pair<String, ArrayList<Int>>? {
        var length = reader.readIntFromFile()
        if (length == 0) return null
        val term = reader.readStringFromFile(length)

        length = reader.readIntFromFile()
        val postings = arrayListOf<Int>()
        for (i in 0 until length) {
            val position = reader.readIntFromFile()
            postings.add(position)
        }

        return Pair(term, postings)
    }
}
