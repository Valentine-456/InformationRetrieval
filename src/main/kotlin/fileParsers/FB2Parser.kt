package fileParsers

import com.kursx.parser.fb2.FictionBook
import org.xml.sax.SAXException
import utils.processWord
import java.io.File
import java.io.IOException
import javax.xml.parsers.ParserConfigurationException

class FB2Parser(override val path: String) : FileParser {
    override fun parseFile(): Any {
        val tokens = ArrayList<String>()
        try {
            val fb2 = FictionBook(File(path))
            fb2.body.sections.forEach {
                it.elements.forEach {
                    val wordsInLine = it
                        .text
                        .split(
                            " ",
                            "\n",
                            "\t",
                            ",—",
                            ".—",
                            ".-",
                            "—",
                            ",-",
                            "”—",
                            "—“",
                            "!—",
                            "-?",
                            "--",
                            "?—",
                            ":—",
                            " ",
                            "\"—",
                            "—\""
                        )
                    wordsInLine.forEach { word ->
                        if (processWord(word) != "") tokens.add(processWord(word))
                    }
                }
            }
            println(path.split("\\").last() + ": " + tokens.size + " words.")
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: SAXException) {
            e.printStackTrace()
        }
        return tokens
    }

}