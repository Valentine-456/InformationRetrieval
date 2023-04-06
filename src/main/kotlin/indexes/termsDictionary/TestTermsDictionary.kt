package indexes.termsDictionary

import kotlinx.coroutines.runBlocking

fun testTermsDictionary() = runBlocking {
    val termsDictionary = buildTermsDictionary("./src/main/resources/collection")
    writeTermsDictionaryToFile(termsDictionary)
    return@runBlocking termsDictionary
}