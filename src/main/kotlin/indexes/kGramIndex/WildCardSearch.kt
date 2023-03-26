package indexes.kGramIndex

import indexes.coordinateInvertedIndex.CoordinateInvertedIndex
import indexes.coordinateInvertedIndex.DistanceSearch
import indexes.termsDictionary.TermsDictionary
import java.util.regex.Pattern

//    println(wildCardSearch.searchByBooleanQuery("( c*h OR ( poland OR asia ) )  OR ( m*y AND sh*y )")))
//    println(wildCardSearch.searchByBooleanQuery("( a*ia OR ( wa*er AND fi*e AND ea*th AND a*r ) )"))
//    println(wildCardSearch.searchByQueryInNeighborhood("ma*ss NEAR1000 h*or NEAR300 sp*ce"))
//    println(wildCardSearch.searchByPhrase("We live on a placid is*d of ignorance, in the m*st of black seas of inf*ty"))
//    println(wildCardSearch.searchByPhrase("And if you are n*t crushed by such a pr*re, it is because the air penetrates the interior of your body with equal pressure. Hence perfect equilibrium between the interior and exterior pressure, which thus neutralise each other, and wh*ch"))
//    println(wildCardSearch.getAllTermVariantsByWildCard("hi*us"))

class WildCardSearch(
    private val termsDictionary: TermsDictionary,
    private val coordinateInvertedIndex: CoordinateInvertedIndex,
    private val kGramIndex: KGramIndex
) : DistanceSearch(termsDictionary, coordinateInvertedIndex) {

    fun getAllTermVariantsByWildCard(wildcard: String): ArrayList<String> {
        var indicesOfTerms = ArrayList<Int>()
        val k = this.kGramIndex.k
        val kGrams = ArrayList<String>()

        if (wildcard.length < k - 1) {
            kGrams.add(" $wildcard ")
        } else {
            var index = k
            kGrams.add(" " + wildcard.slice(0 until (k - 1)))
            while (wildcard.length > index) {
                kGrams.add(wildcard.slice((index - k + 1)..index))
                index++
            }
            kGrams.add(wildcard.slice((wildcard.length - k + 1) until wildcard.length) + " ")
        }

        val kGramsWithoutJoker = kGrams.filter { !it.contains("*") } as ArrayList

        kGramsWithoutJoker.forEach {
            val terms = this.kGramIndex.getListOfIndexesWithKGram(it)
            indicesOfTerms = if (indicesOfTerms.isEmpty()) terms
            else super.intersectLists(indicesOfTerms, terms)
        }

        val preFilteredTerms = kGramIndex.mapIndexesToTerms(indicesOfTerms)
        return this.postfilterTerms(wildcard, preFilteredTerms)
    }

    override fun lookupTermInIndex(token: String): ArrayList<Pair<Int, ArrayList<Int>>> {
        if (!token.contains('*')) return super.lookupTermInIndex(token)

        val termVariants = getAllTermVariantsByWildCard(token)
        if (termVariants.isEmpty()) return ArrayList()

        var result: ArrayList<Pair<Int, ArrayList<Int>>>? = null
        for (i in termVariants.indices) {
            if (i == 0) {
                result = super.lookupTermInIndex(termVariants[i])
                continue
            }
            val nextVariant = super.lookupTermInIndex(termVariants[i])
            result = unionTermVariants(result!!, nextVariant)
        }

        return result!!
    }

    private fun postfilterTerms(term: String, possibleTerms: ArrayList<String>): ArrayList<String> {
        val termParts = term.split("*")
        val termRegex = termParts.joinToString("\\S*")
        val pattern = Pattern.compile(termRegex)

        val result = arrayListOf<String>()
        for (value in possibleTerms) {
            val matcher = pattern.matcher(value)
            if (matcher.matches()) {
                result.add(value)
            }
        }

        return result
    }

    // TODO rename all variables
    private fun unionTermVariants(
        result: ArrayList<Pair<Int, ArrayList<Int>>>,
        nextVariant: ArrayList<Pair<Int, ArrayList<Int>>>
    ): ArrayList<Pair<Int, ArrayList<Int>>> {
        if (result.isEmpty()) return nextVariant
        else if (nextVariant.isEmpty()) return result
        val unionedResult = arrayListOf<Pair<Int, ArrayList<Int>>>()
        val indices1 = result.map { it.first } as ArrayList
        val indices2 = nextVariant.map { it.first } as ArrayList
        val docIndexes = unionLists(indices1, indices2)

        docIndexes.forEach { docIndex ->
            if (docIndex !in indices1) {
                unionedResult.add(Pair(docIndex, nextVariant.find { it.first == docIndex }!!.second))
            } else if (docIndex !in indices2) {
                unionedResult.add(Pair(docIndex, result.find { it.first == docIndex }!!.second))
            } else {
                val positions1 = nextVariant.find { it.first == docIndex }!!.second
                val positions2 = result.find { it.first == docIndex }!!.second
                unionedResult.add(Pair(docIndex, unionLists(positions1, positions2)))
            }
        }

        return unionedResult
    }

}