class BooleanSearch(val termsDictionary: TermsDictionary, val invertedIndex: ArrayList<Pair<String, ArrayList<Int>>>) {
    private val operatorsList = arrayOf("AND", "OR")
    fun searchByQuery(query: String): ArrayList<String> {
        val tokens = query.split(" ").map { token -> token.trim() } as ArrayList
        while("" in tokens) {
            tokens.remove("")
        }
        val result = parseQuery(tokens)
        return result.map { index ->  termsDictionary.filesIDs[index]} as ArrayList<String>
    }

    private fun parseQuery(tokensList: ArrayList<String>): ArrayList<Int> {
        var result: ArrayList<Int> = lookupTermInIndex(tokensList[0].lowercase()).second
        var currentTokenPosition = 1
        while(currentTokenPosition < tokensList.size) {
            if(tokensList[currentTokenPosition] in operatorsList){
                val tokenAfter = tokensList[currentTokenPosition+1]
                val termAfter = lookupTermInIndex(tokenAfter.lowercase())

                result = if(tokensList[currentTokenPosition] == "OR"){
                    unionLists(result, termAfter.second)
                } else {
                    intersectLists(result, termAfter.second)
                }
                currentTokenPosition+=2
            }
        }
        return result
    }
    private fun lookupTermInIndex(token: String): Pair<String, ArrayList<Int>> {
        var term = invertedIndex.find { pair -> pair.first == token }
        if (term == null) {
            term = Pair(token,ArrayList())
        }
        return term
    }
    private fun intersectLists(list1: ArrayList<Int>, list2: ArrayList<Int>): ArrayList<Int> {
        val answer = ArrayList<Int>()
        if(list1.isEmpty() or list2.isEmpty()) return answer
        var position1 = 0
        var position2 = 0
        var iteratingOverLists = true
        while(iteratingOverLists) {
            if(list1[position1] == list2[position2]) {
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
    private fun unionLists(list1: ArrayList<Int>, list2: ArrayList<Int>): ArrayList<Int> {
        val answer = ArrayList<Int>()
        if(list1.isEmpty() ) return list2
        else if(list2.isEmpty()) return list1
        var position1 = 0
        var position2 = 0
        var iteratingOverLists = true
        while(iteratingOverLists) {
            if(list1[position1] == list2[position2]) {
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
        while((position1 < list1.size)) {
            answer.add(list1[position1])
            position1++
        }
        while((position2 < list2.size)) {
            answer.add(list2[position2])
            position2++
        }
        return answer
    }
}