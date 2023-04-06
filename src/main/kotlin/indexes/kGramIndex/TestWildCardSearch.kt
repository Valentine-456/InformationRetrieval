package indexes.kGramIndex

import indexes.coordinateInvertedIndex.CoordinateInvertedIndex
import indexes.termsDictionary.TermsDictionary

fun testKGramIndex(termsDictionary: TermsDictionary): KGramIndex {
    val kGram2Index = KGramIndex(2)
    kGram2Index.buildInvertedIndex(termsDictionary)
    val res1 = kGram2Index.getListOfIndexesWithKGram("r ")
    println(kGram2Index.mapIndexesToTerms(res1))
    val res2 = kGram2Index.getListOfIndexesWithKGram(" r")
    println(kGram2Index.mapIndexesToTerms(res2))

    val kGram3Index = KGramIndex()
    kGram3Index.buildInvertedIndex(termsDictionary)
    val res3 = kGram3Index.getListOfIndexesWithKGram("ad ")
    println(kGram3Index.mapIndexesToTerms(res3))
    val res4 = kGram3Index.getListOfIndexesWithKGram(" he")
    println(kGram3Index.mapIndexesToTerms(res4))
    return kGram3Index
}

fun testWildCardSearch(termsDictionary: TermsDictionary, kGramIndex: KGramIndex) {
    val coordinateInvertedIndex = CoordinateInvertedIndex()
    coordinateInvertedIndex.buildInvertedIndex(termsDictionary)
    val wildCardSearch = WildCardSearch(termsDictionary, coordinateInvertedIndex, kGramIndex)

    println(wildCardSearch.searchByBooleanQuery("( c*h OR ( poland OR asia ) )  OR ( m*y AND sh*y )"))
    println(wildCardSearch.searchByBooleanQuery("( a*ia OR ( wa*er AND fi*e AND ea*th AND a*r ) )"))
    println(wildCardSearch.searchByQueryInNeighborhood("ma*ss NEAR1000 h*or NEAR300 sp*ce"))
    println(wildCardSearch.searchByPhrase("We live on a placid is*d of ignorance, in the m*st of black seas of inf*ty"))
    println(wildCardSearch.searchByPhrase("And if you are n*t crushed by such a pr*re, it is because the air penetrates the interior of your body with equal pressure. Hence perfect equilibrium between the interior and exterior pressure, which thus neutralise each other, and wh*ch"))
    println(wildCardSearch.getAllTermVariantsByWildCard("hi*us"))

}