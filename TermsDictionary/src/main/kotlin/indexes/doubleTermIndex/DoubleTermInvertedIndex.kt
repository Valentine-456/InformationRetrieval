package indexes.doubleTermIndex

import indexes.invertedIndex.InvertedIndex
import indexes.termsDictionary.TermsDictionary

class DoubleTermInvertedIndex: InvertedIndex() {
    override fun buildInvertedIndex(termsDictionary: TermsDictionary): InvertedIndex {
        val transformedTermsDictionary = this.transformTermsDictionary(termsDictionary)
        super.buildInvertedIndex(transformedTermsDictionary)
        return this
    }

    private fun transformTermsDictionary(termsDictionary: TermsDictionary): TermsDictionary {
        val (_, numberOfTerms, parsedFiles, booksIDs) = termsDictionary
        val transformedTermsDict = hashSetOf<String>()
        var newNumberOfTerms = numberOfTerms
        val transformedParsedFiles = (parsedFiles.map { wordList ->
            val transformedWordList = arrayListOf<String>()
            for(index in 0 until wordList.size-1) {
                transformedWordList.add(wordList[index] + " " + wordList[index+1])
            }
            transformedWordList
        }.toTypedArray())
        transformedParsedFiles.forEach {
            newNumberOfTerms -= 1
            transformedTermsDict.addAll(it)
        }
        return TermsDictionary(transformedTermsDict, newNumberOfTerms, transformedParsedFiles, booksIDs)
    }
}
