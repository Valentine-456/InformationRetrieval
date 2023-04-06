import indexes.coordinateInvertedIndex.testDistanceSearch
import indexes.doubleTermIndex.testPhraseSearch
import indexes.invertedIndex.testBooleanSearch
import indexes.kGramIndex.testKGramIndex
import indexes.kGramIndex.testWildCardSearch
import indexes.termsBTree.testTermsBTree
import indexes.termsDictionary.testTermsDictionary

fun main() {
    val termsDictionary = testTermsDictionary()
    testBooleanSearch(termsDictionary)
    testPhraseSearch(termsDictionary)
    testDistanceSearch(termsDictionary)
    testTermsBTree(termsDictionary)
    val kGramIndex = testKGramIndex(termsDictionary)
    testWildCardSearch(termsDictionary, kGramIndex)
}
