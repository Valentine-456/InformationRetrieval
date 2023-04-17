package indexes.spimi

import fileParsers.FB2ParserSPIMI
import fileParsers.FileParser
import utils.FileReaderBytes
import utils.FileWriterBytes
import utils.listsUnion
import java.io.BufferedWriter
import java.io.File
import java.util.*

class SPIMI(
    private val collectionDir: String = "./src/main/resources/collection", private val tokensChunkSize: Int = 20000
) {
    private val filesIDs = HashMap<String, Int>()
    private val invertedIndex = TreeMap<String, ArrayList<Int>>()
    val diskUtils = SPIMIDiskUtils()

    init {
        val directory = File(collectionDir).walkTopDown()
        directory.forEachIndexed { index, file ->
            if (file.name == "collection") return@forEachIndexed
            filesIDs[file.name] = index
        }
    }

    fun execute(
        outputDestination: String = "./src/main/resources/InvertedIndexSPIMI",
        binaryOutput: Boolean = true
    ) {
        this.invertSPIMI()
        this.mergeSPIMI(outputDestination, binaryOutput)
    }

    private fun invertSPIMI() {
        val filesIdsSorted = ArrayList<Int>()
        filesIDs.forEach { (filename, int) -> filesIdsSorted.add(int) }
        filesIdsSorted.sort()
        filesIdsSorted.forEach { id ->

            val filename = this.filesIDs.entries.find { it.value == id }?.key!!
            val fileParser: FileParser? = FB2ParserSPIMI("$collectionDir/$filename")
            val tokens = fileParser?.parseFile() as ArrayList<String>
            var position = 0
            while (position != -1) {
                position = this.invertFile(
                    filename,
                    tokens,
                    position
                )
                Runtime.getRuntime().gc()
            }
        }
        writeBlockToDisk(this.invertedIndex)
    }

    private fun invertFile(
        filename: String,
        tokens: ArrayList<String>,
        positionParam: Int = 0
    ): Int {
        var position = positionParam

        while (position < tokens.size) {
            if (this.invertedIndex.size >= tokensChunkSize) {
                writeBlockToDisk(this.invertedIndex)
                return position
            }

            val token = tokens[position]
            if (this.invertedIndex[token] == null) {
                this.invertedIndex[token] = arrayListOf(filesIDs[filename]!!)
            } else {
                if (!this.invertedIndex[token]!!.contains(filesIDs[filename])) this.invertedIndex[token]!!.add(filesIDs[filename]!!)
            }
            position++
        }
        return -1
    }

    private fun writeBlockToDisk(map: TreeMap<String, ArrayList<Int>>) {
        this.diskUtils.writeBlock(map)
        print("Block written to disk: ")
        println(map.size)
        map.clear()
        Runtime.getRuntime().gc()
    }

    private fun mergeSPIMI(outputDestination: String, binaryOutput: Boolean) {
        var uniqueWordsCount = 0
        val outputFile = File(outputDestination)
        val invertedIndexWriter: Any = if (binaryOutput) FileWriterBytes(outputFile) else outputFile.bufferedWriter()

        val readingBlocksList = diskUtils.getReadingBlocksList().toMutableList()
        val nextTermsInBlocks = readingBlocksList.map {
            this.diskUtils.readTermFromBlockFile(it)
        }.toMutableList()
        var hasNextTerm = true

        while (hasNextTerm) {
            val (
                smallestTerm,
                blockFilesWithSmallestTerms
            ) = this.findSmallestTerm(nextTermsInBlocks)

            this.addProcessedTermToInvertedIndex(
                smallestTerm.first,
                blockFilesWithSmallestTerms,
                nextTermsInBlocks
            )
            uniqueWordsCount++

            if (this.invertedIndex.size >= this.tokensChunkSize) {
                println("Added to inverted index: $uniqueWordsCount new terms.")
                if (binaryOutput)
                    this.diskUtils.appendToBlock(invertedIndexWriter as FileWriterBytes, this.invertedIndex)
                else
                    this.diskUtils.appendTextViaBufferedWriter(
                        invertedIndexWriter as BufferedWriter,
                        this.invertedIndex
                    )
                invertedIndex.clear()
            }

            this.pruneFinishedBlockReaders(readingBlocksList, nextTermsInBlocks, blockFilesWithSmallestTerms)
            if (readingBlocksList.isEmpty()) hasNextTerm = false
        }

        if (binaryOutput) {
            this.diskUtils.appendToBlock(invertedIndexWriter as FileWriterBytes, this.invertedIndex)
            invertedIndexWriter.close()
        } else {
            this.diskUtils.appendTextViaBufferedWriter(invertedIndexWriter as BufferedWriter, this.invertedIndex)
            invertedIndexWriter.close()
        }
        this.invertedIndex.clear()
        println("Unique words: $uniqueWordsCount")
    }

    private fun findSmallestTerm(nextTermsInBlocks: MutableList<Pair<String, ArrayList<Int>>?>):
            Pair<Pair<String, ArrayList<Int>>, ArrayList<Int>> {
        var smallestTerm = nextTermsInBlocks[0]!!
        val blockFilesWithSmallestTerms = arrayListOf(0)
        for (i in nextTermsInBlocks.indices) {
            if (i == 0) continue
            if (smallestTerm.first.compareTo(nextTermsInBlocks[i]!!.first) == 0)
                blockFilesWithSmallestTerms.add(i)
            if (smallestTerm.first.compareTo(nextTermsInBlocks[i]!!.first) > 0) {
                smallestTerm = nextTermsInBlocks[i]!!
                blockFilesWithSmallestTerms.clear()
                blockFilesWithSmallestTerms.add(i)
            }
        }
        return Pair(smallestTerm, blockFilesWithSmallestTerms)
    }

    private fun pruneFinishedBlockReaders(
        readingBlocksList: MutableList<FileReaderBytes>,
        nextTermsInBlocks: MutableList<Pair<String, ArrayList<Int>>?>,
        blockFilesWithSmallestTerms: ArrayList<Int>
    ) {
        val fileReadersToRemove = arrayListOf<Int>()
        for (i in readingBlocksList.indices) {
            if (i in blockFilesWithSmallestTerms) {
                nextTermsInBlocks[i] = this.diskUtils.readTermFromBlockFile(readingBlocksList[i])
                if (nextTermsInBlocks[i] == null) {
                    fileReadersToRemove.add(i)
                }
            }
        }
        for (i in fileReadersToRemove.reversed()) {
            nextTermsInBlocks.removeAt(i)
            readingBlocksList[i].close()
            readingBlocksList.removeAt(i)
        }
    }

    private fun addProcessedTermToInvertedIndex(
        smallestTerm: String,
        blockFilesWithSmallestTerms: ArrayList<Int>,
        nextTermsInBlocks: MutableList<Pair<String, ArrayList<Int>>?>,
    ) {
        var totalTermPostings = arrayListOf<Int>()
        for (i in blockFilesWithSmallestTerms) {
            if (i == 0) totalTermPostings = nextTermsInBlocks[i]!!.second
            totalTermPostings = listsUnion(totalTermPostings, nextTermsInBlocks[i]!!.second)
        }
        this.invertedIndex[smallestTerm] = totalTermPostings
    }
}