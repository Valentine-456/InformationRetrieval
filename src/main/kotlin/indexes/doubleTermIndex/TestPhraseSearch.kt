package indexes.doubleTermIndex

import indexes.termsDictionary.TermsDictionary

fun testPhraseSearch(termsDictionary: TermsDictionary) {
    val doubleTermInvertedIndex = DoubleTermInvertedIndex()
    doubleTermInvertedIndex.buildInvertedIndex(termsDictionary)
    val phraseSearch = PhraseSearch(termsDictionary, doubleTermInvertedIndex)

    println(phraseSearch.searchByQuery("I declare after all there is no enjoyment like reading"))
    println(phraseSearch.searchByQuery("We live in society"))
    println(phraseSearch.searchByQuery("We live on a placid island of ignorance, in the midst of black seas of infinity"))
}