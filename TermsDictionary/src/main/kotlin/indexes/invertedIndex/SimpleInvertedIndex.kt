package indexes.invertedIndex

import indexes.termsDictionary.TermsDictionary
import utils.printStatisticsOfFile
import utils.resolveFileWriter
import java.util.*
import kotlin.collections.ArrayList

typealias SimpleInvertedIndex = TreeMap<String, ArrayList<Int>>

fun buildInvertedIndex(termsDictionary: TermsDictionary): SimpleInvertedIndex {
    val (termsDict, totalNumberOfWords, parsedFiles, _) = termsDictionary
    val uniqueWordsByDocument = parsedFiles.map {list ->
        val set = hashSetOf<String>()
        set.addAll(list)
        set
    }
    val invertedIndex = TreeMap<String, ArrayList<Int>>()
    termsDict.forEach { word ->
        val listOfDocuments = arrayListOf<Int>()
        uniqueWordsByDocument.forEachIndexed  { bookIndex, parsedBook ->
            if(parsedBook.contains(word)) {
                listOfDocuments.add(bookIndex+1)
            }
        }
        invertedIndex[word] = listOfDocuments
    }
    return invertedIndex
}

fun writeInvertedIndexToFile(invertedIndex: SimpleInvertedIndex) {
    val invertedIndexWriter = resolveFileWriter("InvertedIndex.txt")
    invertedIndex.forEach { (key, value) ->
        invertedIndexWriter.write("$key: ")
        value.forEach {
            invertedIndexWriter.write("$it,")
        }
        invertedIndexWriter.newLine()
    }
    invertedIndexWriter.close()
    printStatisticsOfFile("InvertedIndex.txt")
}