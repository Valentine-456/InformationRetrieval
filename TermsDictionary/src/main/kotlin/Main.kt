import indexes.coordinateInvertedIndex.CoordinateInvertedIndex
import indexes.doubleTermIndex.DoubleTermInvertedIndex
import indexes.doubleTermIndex.PhraseSearch
import indexes.invertedIndex.InvertedIndex
import indexes.termsDictionary.buildTermsDictionary
import indexes.termsDictionary.writeTermsDictionaryToFile
import kotlinx.coroutines.*
import utils.writeCoordinateInvertedIndexToFile
import utils.writeInvertedIndexToFile

fun main(): Unit = runBlocking {
    val termsDictionary = buildTermsDictionary("./src/main/resources/collection")
    writeTermsDictionaryToFile(termsDictionary)

    val invertedIndex = InvertedIndex().buildInvertedIndex(termsDictionary)
    writeInvertedIndexToFile(invertedIndex)

    val doubleTermsInvertedIndex = DoubleTermInvertedIndex()
        doubleTermsInvertedIndex.buildInvertedIndex(termsDictionary)
    writeInvertedIndexToFile(doubleTermsInvertedIndex, "DoubleTermInvertedIndex.txt")

    val phraseSearch = PhraseSearch(termsDictionary, doubleTermsInvertedIndex)
    println(phraseSearch.searchByQuery("I declare after all there is no enjoyment like reading"))
    println(phraseSearch.searchByQuery("We live in society"))
    println(phraseSearch.searchByQuery("We live on a placid island of ignorance, in the midst of black seas of infinity"))

}
