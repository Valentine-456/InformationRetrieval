package indexes.invertedIndex

import indexes.termsDictionary.TermsDictionary
import java.util.*
import kotlin.collections.ArrayList

open class InvertedIndex() {
    val index: TreeMap<String, ArrayList<Int>> = TreeMap()

    open fun buildInvertedIndex(termsDictionary: TermsDictionary): InvertedIndex {
        val (termsDict, _, parsedFiles, _) = termsDictionary
        val uniqueWordsByDocument = parsedFiles.map {list ->
            val set = hashSetOf<String>()
            set.addAll(list)
            set
        }
        termsDict.forEach { word ->
            val listOfDocuments = arrayListOf<Int>()
            uniqueWordsByDocument.forEachIndexed  { bookIndex, parsedBook ->
                if(parsedBook.contains(word)) {
                    listOfDocuments.add(bookIndex+1)
                }
            }
            this.index[word] = listOfDocuments
        }
        return this
    }

}