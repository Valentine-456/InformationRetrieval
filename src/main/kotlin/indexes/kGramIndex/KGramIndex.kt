package indexes.kGramIndex

import indexes.invertedIndex.InvertedIndex
import indexes.termsDictionary.TermsDictionary
import java.util.*

// val kGram2Index = KGramIndex(2)
//    kGram2Index.buildInvertedIndex(termsDictionary)
//    val res1 = kGram2Index.getListOfIndexesWithKGram("r ")
//    kGram2Index.mapIndexesToTerms(res1).forEach {
//        print("$it, ")
//    }
//    print("\n")
//    val res2 = kGram2Index.getListOfIndexesWithKGram(" r")
//    kGram2Index.mapIndexesToTerms(res2).forEach {
//        print("$it, ")
//    }
//    print("\n")
//
//
//    val kGram3Index = KGramIndex()
//    kGram3Index.buildInvertedIndex(termsDictionary)
//    val res3 = kGram3Index.getListOfIndexesWithKGram("ad ")
//    kGram3Index.mapIndexesToTerms(res3).forEach {
//        print("$it, ")
//    }
//    print("\n")
//    val res4 = kGram3Index.getListOfIndexesWithKGram(" he")
//    kGram3Index.mapIndexesToTerms(res4).forEach {
//        print("$it, ")
//    }
//    print("\n")

class KGramIndex(private val k: Int = 3) : InvertedIndex() {
    private val termsListOrdered: TreeMap<Int, String> = TreeMap()

    override fun buildInvertedIndex(termsDictionary: TermsDictionary): InvertedIndex {
        val termsDict = termsDictionary.termsDict
        val uniqueKGrams = HashSet<String>()
        termsDict.forEachIndexed { index, term ->
            this.termsListOrdered[index] = term
            val kGramsOfTerm = splitIntoKGrams(term)
            uniqueKGrams.addAll(kGramsOfTerm)
        }
        uniqueKGrams.forEach { kGram ->
            val termsWithKGram = arrayListOf<Int>()
            this.termsListOrdered.entries.forEach { (key, value) ->
                val valueWithPadding = " $value "
                if (valueWithPadding.contains(kGram)) termsWithKGram.add(key)
            }
            super.index[kGram] = termsWithKGram
        }
        return this
    }

    fun getListOfIndexesWithKGram(kGram: String): ArrayList<Int> {
        return super.index[kGram] ?: arrayListOf()
    }

    fun mapIndexesToTerms(indexes: ArrayList<Int>): ArrayList<String> {
        return indexes.map { this.termsListOrdered[it] ?: "" } as ArrayList<String>
    }

    private fun splitIntoKGrams(term: String): ArrayList<String> {
        val kGrams = arrayListOf<String>()
        if (term.length < k - 1) {
            kGrams.add(" $term ")
            return kGrams
        }

        var index = k
        kGrams.add(" " + term.slice(0 until (k - 1)))
        while (term.length > index) {
            kGrams.add(term.slice((index - k + 1)..index))
            index++
        }
        kGrams.add(term.slice((term.length - k + 1) until term.length) + " ")
        return kGrams
    }
}