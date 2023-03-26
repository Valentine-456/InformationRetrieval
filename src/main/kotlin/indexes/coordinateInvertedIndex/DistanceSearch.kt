package indexes.coordinateInvertedIndex

import indexes.termsDictionary.TermsDictionary
import utils.findClosingBracketPosition
import utils.processWord
import kotlin.math.abs

//    println("Boolean queries:")
//    println(distanceSearch.searchByBooleanQuery("( czech OR ( poland OR asia ) ) AND ( germany AND france ) OR ( mary AND shelly )"))
//    println("Neighbourhood queries:")
//    println(distanceSearch.searchByQueryInNeighborhood("madness NEAR1000 horror NEAR300 space"))
//    println(distanceSearch.searchByQueryInNeighborhood("god NEAR1000 horror NEAR200 human"))
//    println("Phrase queries:")
//    println(distanceSearch.searchByPhrase("I declare after all there is no enjoyment like reading"))
//    println(distanceSearch.searchByPhrase("We live on a placid island of ignorance, in the midst of black seas of infinity"))
//    println(distanceSearch.searchByPhrase("And if you are not crushed by such a pressure, it is because the air penetrates the interior of your body with equal pressure. Hence perfect equilibrium between the interior and exterior pressure, which thus neutralise each other, and which"))

open class DistanceSearch(
    private val termsDictionary: TermsDictionary,
    private val coordinateIndex: CoordinateInvertedIndex
) {
    protected val booleanOperationList = arrayOf("AND", "OR")
    protected val nearOperator = "^NEAR[0-9]+\$".toRegex()

    open fun searchByPhrase(query: String): ArrayList<String> {
        val tokens = preprocessQuery(query.lowercase()).map(processWord) as ArrayList
        val tokensWithOperators = arrayListOf<String>()
        tokens.forEachIndexed { index, token ->
            tokensWithOperators.add(token)
            if (index < tokens.size - 1) {
                tokensWithOperators.add("NEAR1")
            }
        }
        val result = parseQueryInNeighborhood(tokensWithOperators)
        return result.map { index -> termsDictionary.filesIDs[index] } as ArrayList<String>
    }

    open fun searchByQueryInNeighborhood(query: String): ArrayList<String> {
        val tokens = this.preprocessQuery(query)
        val result = parseQueryInNeighborhood(tokens)
        return result.map { index -> termsDictionary.filesIDs[index] } as ArrayList<String>
    }

    protected open fun parseQueryInNeighborhood(tokensList: ArrayList<String>): ArrayList<Int> {
        val positionsOfOperatorsInQuery = arrayListOf<Int>()
        tokensList.forEachIndexed { index, token ->
            if (token.matches(nearOperator)) positionsOfOperatorsInQuery.add(index)
        }
        positionsOfOperatorsInQuery.reverse()
        var intermediateResult: ArrayList<Pair<Int, ArrayList<Int>>>? = null
        for (index in positionsOfOperatorsInQuery) {
            val operator = tokensList[index]
            val distance = operator.slice(4 until operator.length).toInt()
            val targetTerm = lookupTermInIndex(tokensList[index - 1])
            val inNeighborhoodTerm = intermediateResult ?: lookupTermInIndex(tokensList[index + 1])
            intermediateResult = findInNeighbourhood(targetTerm, inNeighborhoodTerm, distance)
        }
        val result = intermediateResult ?: arrayListOf()
        return result.map { it.first } as ArrayList<Int>
    }

    open fun searchByBooleanQuery(query: String): ArrayList<String> {
        val tokens = this.preprocessQuery(query)
        val result = parseBooleanQuery(tokens)
        return result.map { index -> termsDictionary.filesIDs[index] } as ArrayList<String>
    }

    protected open fun parseBooleanQuery(tokensList: ArrayList<String>): ArrayList<Int> {
        var result: ArrayList<Int>
        var currentTokenPosition: Int
        result = if (tokensList[0] == "(") {
            val closingBracketPosition = findClosingBracketPosition(tokensList)
            currentTokenPosition = closingBracketPosition + 1
            parseBooleanQuery(tokensList.slice(1 until closingBracketPosition) as ArrayList<String>)
        } else {
            currentTokenPosition = 1
            lookupTermInIndex(tokensList[0].lowercase()).map { it.first } as ArrayList<Int>
        }

        while (currentTokenPosition < tokensList.size - 1) {
            if (tokensList[currentTokenPosition] in booleanOperationList) {
                val tokenAfter = tokensList[currentTokenPosition + 1]
                var intermediateResult: ArrayList<Int>
                var currentTokenShift: Int
                if (tokenAfter == "(") {
                    val closingBracketPosition = findClosingBracketPosition(
                        tokensList.slice(
                            currentTokenPosition + 1 until tokensList.size
                        ) as ArrayList<String>
                    )
                    intermediateResult = parseBooleanQuery(
                        tokensList.slice(
                            currentTokenPosition + 2 until closingBracketPosition + currentTokenPosition + 1
                        ) as ArrayList<String>
                    )

                    currentTokenShift = closingBracketPosition + 2
                } else {
                    intermediateResult = lookupTermInIndex(tokenAfter.lowercase()).map { it.first } as ArrayList<Int>
                    currentTokenShift = 2
                }
                result = if (tokensList[currentTokenPosition] == "OR") {
                    unionLists(result, intermediateResult)
                } else {
                    intersectLists(result, intermediateResult)
                }

                currentTokenPosition += currentTokenShift
            } else currentTokenPosition++
        }
        return result
    }

    protected fun preprocessQuery(query: String): ArrayList<String> {
        val tokens = query.split(" ").map { token -> token.trim() } as ArrayList
        while ("" in tokens) {
            tokens.remove("")
        }
        return tokens
    }

    protected open fun lookupTermInIndex(token: String): ArrayList<Pair<Int, ArrayList<Int>>> {
        var term = coordinateIndex.index[token]
        if (term == null) {
            term = ArrayList()
        }
        return term
    }

    protected fun intersectLists(list1: ArrayList<Int>, list2: ArrayList<Int>): ArrayList<Int> {
        val answer = ArrayList<Int>()
        if (list1.isEmpty() or list2.isEmpty()) return answer
        var position1 = 0
        var position2 = 0
        var iteratingOverLists = true
        while (iteratingOverLists) {
            if (list1[position1] == list2[position2]) {
                answer.add(list1[position1])
                position1++
                position2++
            } else {
                if (list1[position1] < list2[position2]) position1++ else position2++
            }
            iteratingOverLists = (position1 < list1.size) and (position2 < list2.size)
        }
        return answer
    }

    protected fun unionLists(list1: ArrayList<Int>, list2: ArrayList<Int>): ArrayList<Int> {
        val answer = ArrayList<Int>()
        if (list1.isEmpty()) return list2
        else if (list2.isEmpty()) return list1
        var position1 = 0
        var position2 = 0
        var iteratingOverLists = true
        while (iteratingOverLists) {
            if (list1[position1] == list2[position2]) {
                answer.add(list1[position1])
                position1++
                position2++
            } else {
                if (list1[position1] < list2[position2]) {
                    answer.add(list1[position1])
                    position1++
                } else {
                    answer.add(list2[position2])
                    position2++
                }
            }
            iteratingOverLists = (position1 < list1.size) and (position2 < list2.size)
        }
        while ((position1 < list1.size)) {
            answer.add(list1[position1])
            position1++
        }
        while ((position2 < list2.size)) {
            answer.add(list2[position2])
            position2++
        }
        return answer
    }

    protected fun findInNeighbourhood(
        list1: ArrayList<Pair<Int, ArrayList<Int>>>,
        list2: ArrayList<Pair<Int, ArrayList<Int>>>,
        distance: Int
    ): ArrayList<Pair<Int, ArrayList<Int>>> {
        val answer = ArrayList<Pair<Int, ArrayList<Int>>>()
        if (list1.isEmpty() or list2.isEmpty()) return answer
        for (currentDocument in list1) {
            val similarDocument = list2.find { it.first == currentDocument.first }
            if (similarDocument == null) continue
            val neighboringPositions = arrayListOf<Int>()
            var position1 = 0
            var position2 = 0
            var iteratingOverLists = true
            while (iteratingOverLists) {
                val currentDistance = abs(currentDocument.second[position1] - similarDocument.second[position2])
                if (currentDistance <= distance) {
                    neighboringPositions.add(currentDocument.second[position1])
                    position1++
                    position2++
                } else {
                    if (currentDocument.second[position1] < similarDocument.second[position2]) position1++
                    else position2++
                }
                iteratingOverLists =
                    (position1 < currentDocument.second.size) and (position2 < similarDocument.second.size)
            }
            if (neighboringPositions.isNotEmpty()) {
                answer.add(Pair(currentDocument.first, neighboringPositions))
            }
        }
        return answer
    }
}