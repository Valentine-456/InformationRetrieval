import com.kursx.parser.fb2.FictionBook
import kotlinx.coroutines.*
import org.xml.sax.SAXException
import java.io.File
import java.io.IOException
import javax.xml.parsers.ParserConfigurationException

fun main() = runBlocking {
    val coroutines = mutableListOf<Job>()
    val directory = File("./src/main/resources/collection").walkTopDown()
    val parsedFiles = Array(directory.count()-1) { ArrayList<String>()}
    val termsDict = HashSet<String>()
    var totalNumberOfWords = 0

    directory.forEachIndexed { index, file ->
        if(file.name == "collection") return@forEachIndexed
        coroutines.add(
            launch {
                parsedFiles[index-1] = parseFile(file.path) as ArrayList<String>
            }
        )
    }

    coroutines.joinAll()
    parsedFiles.forEach {
        totalNumberOfWords += it.size
        termsDict.addAll(it)
    }

    val termsDictFile = File("./src/main/resources/TermsDictionary.txt")
    if(termsDictFile.exists()) termsDictFile.delete()
    val writer = termsDictFile.bufferedWriter()
    termsDict.forEach{
        writer.write(it)
        writer.newLine()
    }
    writer.close()

    println("\nTerms dictionary size: ${termsDict.size} words (${termsDictFile.length()} bytes).")
    println("Collection size: $totalNumberOfWords words.")
}

suspend fun parseFile(path: String): Any = coroutineScope {
    try {
        val fb2 = FictionBook(File(path))
        val tokens = ArrayList<String>()
        fb2.body.sections.forEach {
            it.elements.forEach {
                val wordsInLine = it.text.split(" ", "\t")
                wordsInLine.forEach {
                    word -> if(processWord(word) != "") tokens.add(processWord(word))
                }
            }
        }
        println(path.split("\\").last() + ": " + tokens.size + " words.")
        return@coroutineScope tokens
    } catch (e: ParserConfigurationException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: SAXException) {
        e.printStackTrace()
    }
}

fun processWord(word: String): String {
    var wordProcessed = word.trim().lowercase()
    if(wordProcessed.isEmpty()) return ""

    val forbiddenSymbols = arrayOf(',', '.', '\n', '\'', '\"', '?', '!', '“', '”', ';', ':', '(', ')', '‘', '-', '—', '’', '[', ']')
    var hasForbiddenCharOnEnds = true
    while(hasForbiddenCharOnEnds) {
        if(wordProcessed.isEmpty()) return ""

        val isFirstCharForbidden = wordProcessed.first() in forbiddenSymbols
        val isLastCharForbidden = wordProcessed.last() in forbiddenSymbols

        if(isLastCharForbidden)
            wordProcessed = wordProcessed.slice(0 until (wordProcessed.length-1))
        if(wordProcessed.isEmpty()) return ""

        if(isFirstCharForbidden)
            wordProcessed = wordProcessed.slice(1 until wordProcessed.length)
        if(wordProcessed.isEmpty()) return ""

        hasForbiddenCharOnEnds = isFirstCharForbidden || isLastCharForbidden
    }

    return wordProcessed
}
