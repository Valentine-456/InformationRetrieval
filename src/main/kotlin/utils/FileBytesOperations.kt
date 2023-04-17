package utils

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

class FileWriterBytes(private val file: File) {
    private val path = file.toPath()
    private val fos = BufferedOutputStream(FileOutputStream(file))

    fun appendBytesToFile(text: String) {
        val byteArray = text.toByteArray(StandardCharsets.UTF_8)
        fos.write(byteArray)
    }

    fun appendBytesToFile(int: Int) {
        val bb = ByteBuffer.allocate(4)
        bb.putInt(int)
        bb.rewind()
        fos.write(bb.array())
    }

    fun close() = this.fos.close()
}

class FileReaderBytes(private val file: File) {
    var isClosed = false
    private val fis = FileInputStream(file)
    private var position: Long = 0

    fun readIntFromFile(): Int {
        val bytes = ByteBuffer.allocate(4)
        fis.channel.read(bytes, position)
        position += 4
        bytes.rewind()
        return bytes.int
    }

    fun readStringFromFile(length: Int): String {
        val bytesTerm = ByteBuffer.allocate(length)
        fis.channel.read(bytesTerm, position)
        position += length
        bytesTerm.rewind()
        return String(bytesTerm.array(), StandardCharsets.UTF_8)
    }

    fun close() {
        this.fis.close()
        this.isClosed = true
    }
}


