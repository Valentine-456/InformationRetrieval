import indexes.invertedIndex.testBooleanSearch
import indexes.spimi.testSPIMI
import indexes.termsDictionary.testTermsDictionary

fun main() {
    testSPIMI()
    val startTime = System.nanoTime()
    val termsDict = testTermsDictionary()
    testBooleanSearch(termsDict)
    val stopTime = System.nanoTime()
    println("Execution time in milliseconds: ${(stopTime - startTime) / 1000_000.0}")

}
