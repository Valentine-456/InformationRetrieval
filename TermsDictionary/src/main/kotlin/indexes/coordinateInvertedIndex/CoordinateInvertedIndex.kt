package indexes.coordinateInvertedIndex

import indexes.termsDictionary.TermsDictionary
import java.util.*
import kotlin.collections.ArrayList

class CoordinateInvertedIndex{
    val index: TreeMap<String, ArrayList<Pair<Int, ArrayList<Int>>>> = TreeMap()

    fun buildInvertedIndex(termsDictionary: TermsDictionary): CoordinateInvertedIndex {
        val (termsDict, _, parsedFiles, _) = termsDictionary
        val uniqueWordsByDocument = parsedFiles.map {list ->
            val set = hashSetOf<String>()
            set.addAll(list)
            set
        }

        termsDict.forEach { word ->
            val listOfDocuments = arrayListOf<Pair<Int, ArrayList<Int>>>()
            uniqueWordsByDocument.forEachIndexed  { bookIndex, parsedBook ->
                if(parsedBook.contains(word)) {
                    val occurrenceList = arrayListOf<Int>()
                    parsedFiles[bookIndex].forEachIndexed { index, s ->
                        if(s == word) occurrenceList.add(index)
                    }
                    listOfDocuments.add(Pair(bookIndex+1, occurrenceList))
                }
            }
            this.index[word] = listOfDocuments
        }
        return this
    }
}