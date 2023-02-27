package fileParsers

import com.kursx.parser.fb2.FictionBook
import kotlinx.coroutines.*
import org.xml.sax.SAXException
import java.io.File
import java.io.IOException
import javax.xml.parsers.ParserConfigurationException
import utils.processWord

class FB2Parser(override val path: String) : FileParser {
    override suspend fun parseFile(): Any = coroutineScope {
        try {
            val fb2 = FictionBook(File(path))
            val tokens = ArrayList<String>()
            fb2.body.sections.forEach {
                it.elements.forEach {
                    val wordsInLine = it.text.split(" ", "\t", ",—", ".—", ".-", ",-", "”—", "—“")
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

}