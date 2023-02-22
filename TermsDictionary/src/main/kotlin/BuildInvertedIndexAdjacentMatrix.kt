fun buildInvertedIndexAdjacentMatrix(termsDictionary: TermsDictionary):
        Pair<ArrayList<Pair<String, ArrayList<Int>>>, ArrayList<Pair<String, Array<Int>>>> {
    val (termsDict, totalNumberOfWords, parsedFiles, _) = termsDictionary

    val adjacentMatrixOfTerms = ArrayList<Pair<String,Array<Int>>>(totalNumberOfWords)
    val invertedIndex = ArrayList<Pair<String, ArrayList<Int>>>(totalNumberOfWords)

    termsDict.forEach { word ->
        adjacentMatrixOfTerms.add(Pair(word, Array(parsedFiles.size) {0}))
        invertedIndex.add(Pair(word, ArrayList()))
        parsedFiles.forEachIndexed  { bookIndex, parsedBook ->
            if(parsedBook.contains(word)) {
                adjacentMatrixOfTerms.last().second[bookIndex] = 1
                invertedIndex.last().second.add(bookIndex+1)
            }
        }
    }
    return Pair(invertedIndex, adjacentMatrixOfTerms)
}

fun writeAdjacentMatrixToFile(adjacentMatrixOfTerms: ArrayList<Pair<String,Array<Int>>>) {
    val adjacentMatrixWriter = resolveFileWriter("AdjacentMatrixOfTerms.txt")
    adjacentMatrixWriter.newLine()
    adjacentMatrixOfTerms.forEach { it ->
        adjacentMatrixWriter.write(it.first + "    ")
        it.second.forEach {
            adjacentMatrixWriter.write("${it}, ")
        }
        adjacentMatrixWriter.newLine()
    }
    adjacentMatrixWriter.close()
    printStatisticsOfFile("AdjacentMatrixOfTerms.txt")
}

fun writeInvertedIndexToFile(invertedIndex: ArrayList<Pair<String, ArrayList<Int>>>) {
    val invertedIndexWriter = resolveFileWriter("InvertedIndex.txt")
    invertedIndex.forEach {
        invertedIndexWriter.write("${it.first}: ")
        it.second.forEach {
            invertedIndexWriter.write("$it, ")
        }
        invertedIndexWriter.newLine()
    }
    invertedIndexWriter.close()
    printStatisticsOfFile("InvertedIndex.txt")
}

