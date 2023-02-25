import IndexedCollection.buildTermsDictionary
import Search.BooleanSearch
import kotlinx.coroutines.*

fun main() = runBlocking {
    val termsDictionary = buildTermsDictionary("./src/main/resources/collection")
    val invertedIndex = buildInvertedIndex(termsDictionary)
    writeInvertedIndexToFile(invertedIndex)

    val booleanSearch = BooleanSearch(termsDictionary, invertedIndex)
    println(booleanSearch.searchByQuery("fire AND horror"))
    println(booleanSearch.searchByQuery("rust OR iron"))
    println(booleanSearch.searchByQuery("war   "))
    println(booleanSearch.searchByQuery("coal AND ( asia OR europe )"))
    println(booleanSearch.searchByQuery("( vampire OR ( tears AND death ) ) OR chaos"))
    println(booleanSearch.searchByQuery("chaos OR ( vampire OR ( tears AND death ) )"))
    println(booleanSearch.searchByQuery("( czech OR ( poland OR asia ) ) AND ( germany AND france ) OR ( mary AND shelly )"))


}
