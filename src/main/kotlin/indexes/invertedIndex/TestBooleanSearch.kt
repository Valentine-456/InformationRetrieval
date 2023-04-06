package indexes.invertedIndex

import indexes.termsDictionary.TermsDictionary

fun testBooleanSearch(termsDictionary: TermsDictionary) {
    val invertedIndex = InvertedIndex()
    invertedIndex.buildInvertedIndex(termsDictionary)
    val booleanSearch = BooleanSearch(termsDictionary, invertedIndex)

    println(booleanSearch.searchByQuery("war   "))
    println(booleanSearch.searchByQuery("coal AND ( asia OR europe )"))
    println(booleanSearch.searchByQuery("( vampire OR ( tears AND death ) ) OR chaos"))
    println(booleanSearch.searchByQuery("chaos OR ( vampire OR ( tears AND death ) )"))
    println(booleanSearch.searchByQuery("( czech OR ( poland OR asia ) ) AND ( germany AND france ) OR ( mary AND shelly )"))
}