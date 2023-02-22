import kotlinx.coroutines.*

fun main() = runBlocking {
    val termsDictionary = buildTermsDictionary("./src/main/resources/collection")
    val (invertedIndex, adjacentMatrixOfTerms) = buildInvertedIndexAdjacentMatrix(termsDictionary)

    writeTermsDictionaryToFile(termsDictionary)
    writeAdjacentMatrixToFile(adjacentMatrixOfTerms)
    writeInvertedIndexToFile(invertedIndex)
}
