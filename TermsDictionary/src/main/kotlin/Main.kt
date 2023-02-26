import indexes.coordinateInvertedIndex.CoordinateInvertedIndex
import indexes.doubleTermIndex.DoubleTermInvertedIndex
import indexes.invertedIndex.InvertedIndex
import indexes.termsDictionary.buildTermsDictionary
import indexes.termsDictionary.writeTermsDictionaryToFile
import kotlinx.coroutines.*
import utils.writeCoordinateInvertedIndexToFile
import utils.writeInvertedIndexToFile

fun main() = runBlocking {
    val termsDictionary = buildTermsDictionary("./src/main/resources/collection")
    writeTermsDictionaryToFile(termsDictionary)

    val invertedIndex = InvertedIndex().buildInvertedIndex(termsDictionary)
    writeInvertedIndexToFile(invertedIndex)

    val doubleTermsInvertedIndex = DoubleTermInvertedIndex()
        doubleTermsInvertedIndex.buildInvertedIndex(termsDictionary)
    writeInvertedIndexToFile(doubleTermsInvertedIndex, "DoubleTermInvertedIndex.txt")

    val coordinateInvertedIndex = CoordinateInvertedIndex().buildInvertedIndex(termsDictionary)
    writeCoordinateInvertedIndexToFile(coordinateInvertedIndex)

}
