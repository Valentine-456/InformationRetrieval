import indexes.kGramIndex.KGramIndex
import indexes.termsDictionary.buildTermsDictionary
import indexes.termsDictionary.writeTermsDictionaryToFile
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val termsDictionary = buildTermsDictionary("./src/main/resources/collection")
    writeTermsDictionaryToFile(termsDictionary)

    val kGram2Index = KGramIndex(2)
    kGram2Index.buildInvertedIndex(termsDictionary)
    val res1 = kGram2Index.getListOfIndexesWithKGram("r ")
    kGram2Index.mapIndexesToTerms(res1).forEach {
        print("$it, ")
    }
    print("\n")
    val res2 = kGram2Index.getListOfIndexesWithKGram(" r")
    kGram2Index.mapIndexesToTerms(res2).forEach {
        print("$it, ")
    }
    print("\n")

    val kGram3Index = KGramIndex()
    kGram3Index.buildInvertedIndex(termsDictionary)
    val res3 = kGram3Index.getListOfIndexesWithKGram("ad ")
    kGram3Index.mapIndexesToTerms(res3).forEach {
        print("$it, ")
    }
    print("\n")
    val res4 = kGram3Index.getListOfIndexesWithKGram(" he")
    kGram3Index.mapIndexesToTerms(res4).forEach {
        print("$it, ")
    }
    print("\n")
    val res5 = kGram3Index.getListOfIndexesWithKGram("the")
    kGram3Index.mapIndexesToTerms(res5).forEach {
        print("$it, ")
    }
    print("\n")
}
