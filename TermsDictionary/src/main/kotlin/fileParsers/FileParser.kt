package fileParsers

interface FileParser {
    val path: String
    suspend fun parseFile(): Any
}
