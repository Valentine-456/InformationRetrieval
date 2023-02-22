package FileParsers

interface FileParser {
    val path: String
    suspend fun parseFile(): Any
}

val processWord = fun (word: String): String {
    var wordProcessed = word.trim().lowercase()
    if(wordProcessed.isEmpty()) return ""

    val forbiddenSymbols = arrayOf(',', '.', '\n', '\'', '\"', '?', '!', '“', '”', ';', ':', '(', ')', '‘', '-', '—', '’', '[', ']')
    var hasForbiddenCharOnEnds = true
    while(hasForbiddenCharOnEnds) {
        if(wordProcessed.isEmpty()) return ""

        val isFirstCharForbidden = wordProcessed.first() in forbiddenSymbols
        val isLastCharForbidden = wordProcessed.last() in forbiddenSymbols

        if(isLastCharForbidden)
            wordProcessed = wordProcessed.slice(0 until (wordProcessed.length-1))
        if(wordProcessed.isEmpty()) return ""

        if(isFirstCharForbidden)
            wordProcessed = wordProcessed.slice(1 until wordProcessed.length)
        if(wordProcessed.isEmpty()) return ""

        hasForbiddenCharOnEnds = isFirstCharForbidden || isLastCharForbidden
    }

    return wordProcessed
}