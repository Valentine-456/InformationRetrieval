package indexes.invertedIndex

import indexes.termsDictionary.TermsDictionary
import utils.findClosingBracketPosition

open class BooleanSearch(
    private val termsDictionary: TermsDictionary,
    private val invertedIndex: InvertedIndex
) {
    private val operatorsList = arrayOf("AND", "OR")
    open fun searchByQuery(query: String): ArrayList<String> {
        val tokens = query.split(" ").map { token -> token.trim() } as ArrayList
        while ("" in tokens) {
            tokens.remove("")
        }
        val result = parseQuery(tokens)
        return result.map { index -> termsDictionary.filesIDs[index] } as ArrayList<String>
    }

    private fun parseQuery(tokensList: ArrayList<String>): ArrayList<Int> {
        var result: ArrayList<Int>
        var currentTokenPosition: Int
        result = if (tokensList[0] == "(") {
            val closingBracketPosition = findClosingBracketPosition(tokensList)
            currentTokenPosition = closingBracketPosition + 1
            parseQuery(tokensList.slice(1 until closingBracketPosition) as ArrayList<String>)
        } else {
            currentTokenPosition = 1
            lookupTermInIndex(tokensList[0].lowercase())
        }

        while (currentTokenPosition < tokensList.size - 1) {
            if (tokensList[currentTokenPosition] in operatorsList) {
                val tokenAfter = tokensList[currentTokenPosition + 1]
                var intermediateResult: ArrayList<Int>
                var currentTokenShift: Int
                if (tokenAfter == "(") {
                    val closingBracketPosition = findClosingBracketPosition(
                        tokensList.slice(
                            currentTokenPosition + 1 until tokensList.size
                        ) as ArrayList<String>
                    )
                    intermediateResult = parseQuery(
                        tokensList.slice(
                            currentTokenPosition + 2 until closingBracketPosition + currentTokenPosition + 1
                        ) as ArrayList<String>
                    )

                    currentTokenShift = closingBracketPosition + 2
                } else {
                    intermediateResult = lookupTermInIndex(tokenAfter.lowercase())
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

    protected fun lookupTermInIndex(token: String): ArrayList<Int> {
        var term = invertedIndex.index[token]
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
}