package Utils

fun main() {
    val list = arrayListOf("(" ,"vampire", "chaos", "OR" ,")", "OR", "(", "tears", "AND", "death", ")", "OR", "(", "tears", "AND", "death", ")")
    print(findClosingBracketPosition(list.slice(6 until list.size) as ArrayList<String>))
}

fun findClosingBracketPosition (tokens: ArrayList<String>): Int {
    var nestedBracketsLevel = 1
    var currentTokenPosition = 0
    while(nestedBracketsLevel > 0) {
        currentTokenPosition++
        val token = tokens[currentTokenPosition]
        if (token == "(") nestedBracketsLevel++
        else if (token == ")") nestedBracketsLevel--
    }
    return currentTokenPosition
}

