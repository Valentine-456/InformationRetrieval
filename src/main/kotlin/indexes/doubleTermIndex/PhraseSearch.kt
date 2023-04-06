package indexes.doubleTermIndex

import indexes.invertedIndex.BooleanSearch
import indexes.termsDictionary.TermsDictionary
import utils.processWord

class PhraseSearch(
    private val termsDictionary: TermsDictionary,
    private val invertedIndex: DoubleTermInvertedIndex
) : BooleanSearch(termsDictionary, invertedIndex) {
    override fun searchByQuery(query: String): ArrayList<String> {
        val tokens = query.split(" ").map { token -> processWord(token.trim().lowercase()) } as ArrayList
        while ("" in tokens) {
            tokens.remove("")
        }
        val doubleTermTokens = arrayListOf<String>()
        for (i in 0 until tokens.size - 1) {
            doubleTermTokens.add(tokens[i] + " " + tokens[i + 1])
        }
        val result = parseQuery(doubleTermTokens)
        return result.map { index -> termsDictionary.filesIDs[index] } as ArrayList<String>
    }

    private fun parseQuery(tokens: ArrayList<String>): ArrayList<Int> {
        var result = super.lookupTermInIndex(tokens[0])
        if (tokens.size > 1) {
            for (i in 1 until tokens.size) {
                val intermediateResult = super.lookupTermInIndex(tokens[i])
                result = super.intersectLists(result, intermediateResult)
            }
        }
        return result
    }
}