package IndexedCollection

import kotlinx.coroutines.*
import java.io.File
import FileParsers.FB2Parser
import printStatisticsOfFile
import resolveFileWriter

suspend fun buildTermsDictionary(collectionDir: String): TermsDictionary = runBlocking {
    val coroutines = mutableListOf<Job>()
    val directory = File(collectionDir).walkTopDown()
    val parsedFiles = Array(directory.count()-1) { ArrayList<String>()}
    val termsDict = HashSet<String>()
    var totalNumberOfWords = 0
    val filesIDs = HashMap<Int, String>()

    directory.forEachIndexed { index, file ->
        if(file.name == "collection") return@forEachIndexed
        filesIDs[index] = file.name
        coroutines.add(
            launch {
                parsedFiles[index-1] = FB2Parser(file.path).parseFile() as ArrayList<String>
            }
        )
    }
    coroutines.joinAll()
    parsedFiles.forEach {
        totalNumberOfWords += it.size
        termsDict.addAll(it)
    }
    return@runBlocking TermsDictionary(termsDict, totalNumberOfWords, parsedFiles, filesIDs)
}

data class TermsDictionary(
    val termsDict: HashSet<String>,
    var totalNumberOfWords: Int,
    val parsedFiles: Array<ArrayList<String>>,
    val filesIDs: HashMap<Int, String>
)

fun writeTermsDictionaryToFile(termsDictionary: TermsDictionary) {
    val writer = resolveFileWriter("TermsDictionary.txt")
    termsDictionary.termsDict.forEach{
        writer.write(it)
        writer.newLine()
    }
    writer.close()
    printStatisticsOfFile("TermsDictionary.txt")
    println("Collection size: ${termsDictionary.totalNumberOfWords} words. Unique words: ${termsDictionary.termsDict.size}")
}