import indexes.coordinateInvertedIndex.CoordinateInvertedIndex
import indexes.coordinateInvertedIndex.DistanceSearch
import indexes.doubleTermIndex.DoubleTermInvertedIndex
import indexes.doubleTermIndex.PhraseSearch
import indexes.invertedIndex.InvertedIndex
import indexes.termsDictionary.buildTermsDictionary
import indexes.termsDictionary.writeTermsDictionaryToFile
import kotlinx.coroutines.*
import utils.writeCoordinateInvertedIndexToFile
import utils.writeInvertedIndexToFile

fun main(): Unit = runBlocking {
    val termsDictionary = buildTermsDictionary("./src/main/resources/collection")
    writeTermsDictionaryToFile(termsDictionary)

    val coordinateInvertedIndex = CoordinateInvertedIndex().buildInvertedIndex(termsDictionary)
    writeCoordinateInvertedIndexToFile(coordinateInvertedIndex)
    val distanceSearch = DistanceSearch(termsDictionary, coordinateInvertedIndex)

    println("Boolean queries:")
    println(distanceSearch.searchByBooleanQuery("chaos OR ( vampire OR ( tears AND death ) )"))
    println(distanceSearch.searchByBooleanQuery("( czech OR ( poland OR asia ) ) AND ( germany AND france ) OR ( mary AND shelly )"))
    println("Neighbourhood queries:")
    println(distanceSearch.searchByQueryInNeighborhood("vampire NEAR7 death"))
    println(distanceSearch.searchByQueryInNeighborhood("vampire NEAR70 death"))
    println(distanceSearch.searchByQueryInNeighborhood("madness NEAR1000 horror NEAR300 space"))
    println(distanceSearch.searchByQueryInNeighborhood("god NEAR1000 horror NEAR200 human"))
    println("Phrase queries:")
    println(distanceSearch.searchByPhrase("I declare after all there is no enjoyment like reading"))
    println(distanceSearch.searchByPhrase("We live in society"))
    println(distanceSearch.searchByPhrase("We live on a placid island of ignorance, in the midst of black seas of infinity"))
    println(distanceSearch.searchByPhrase("All I wanted was"))
    println(distanceSearch.searchByPhrase("I was astonished by such"))
    println(distanceSearch.searchByPhrase("And if you are not crushed by such a pressure, it is because the air penetrates the interior of your body with equal pressure. Hence perfect equilibrium between the interior and exterior pressure, which thus neutralise each other, and which"))

    println("Control phrase queries:")
    val doubleTermInvertedIndex = DoubleTermInvertedIndex().buildInvertedIndex(termsDictionary)
    writeInvertedIndexToFile(doubleTermInvertedIndex, "DoubleTermInvertedIndex.txt")
    val phraseSearch = PhraseSearch(termsDictionary, doubleTermInvertedIndex as DoubleTermInvertedIndex)
    println(phraseSearch.searchByQuery("I declare after all there is no enjoyment like reading"))
    println(phraseSearch.searchByQuery("We live in society"))
    println(phraseSearch.searchByQuery("We live on a placid island of ignorance, in the midst of black seas of infinity"))
    println(phraseSearch.searchByQuery("All I wanted was"))
    println(phraseSearch.searchByQuery("I was astonished by such"))
}
