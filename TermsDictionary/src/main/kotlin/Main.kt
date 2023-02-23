import kotlinx.coroutines.*

fun main() = runBlocking {
    val termsDictionary = buildTermsDictionary("./src/main/resources/collection")
    val invertedIndex = buildInvertedIndex(termsDictionary)
    writeInvertedIndexToFile(invertedIndex)

    val booleanSearch = BooleanSearch(termsDictionary, invertedIndex)
    println(booleanSearch.searchByQuery("fire AND horror"))
    println(booleanSearch.searchByQuery("rust OR iron"))
    println(booleanSearch.searchByQuery("war"))
    println(booleanSearch.searchByQuery("romania OR china AND europe"))
    println(booleanSearch.searchByQuery("vampire"))

}
