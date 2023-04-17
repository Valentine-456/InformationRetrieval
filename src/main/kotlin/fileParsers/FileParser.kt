package fileParsers

interface FileParser {
    val path: String
    fun parseFile(): Any
}
