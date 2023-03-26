import indexes.coordinateInvertedIndex.CoordinateInvertedIndex
import indexes.coordinateInvertedIndex.DistanceSearch
import indexes.kGramIndex.KGramIndex
import indexes.kGramIndex.WildCardSearch
import indexes.termsDictionary.buildTermsDictionary
import indexes.termsDictionary.writeTermsDictionaryToFile
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.Instant

fun main(): Unit = runBlocking {
    val termsDictionary = buildTermsDictionary("./src/main/resources/collection")
    writeTermsDictionaryToFile(termsDictionary)

    val kGram2Index = KGramIndex(2)
    kGram2Index.buildInvertedIndex(termsDictionary)

//    val start = Instant.now()
    val coordinateInvertedIndex = CoordinateInvertedIndex()
    coordinateInvertedIndex.buildInvertedIndex(termsDictionary)
//    val finish = Instant.now()
//    val timeElapsed: Long = Duration.between(start, finish).toMillis()
//    println(timeElapsed)

    val distanceSearch = DistanceSearch(termsDictionary, coordinateInvertedIndex)
    val wildCardSearch = WildCardSearch(termsDictionary, coordinateInvertedIndex, kGram2Index)

    println("Boolean queries:")
    println(distanceSearch.searchByBooleanQuery("( czech OR ( poland OR asia ) ) OR ( mary AND shelly )"))
    println(wildCardSearch.searchByBooleanQuery("( c*h OR ( poland OR asia ) )  OR ( m*y AND sh*y )"))
    var start = Instant.now()
    println(distanceSearch.searchByBooleanQuery("( austria OR ( water AND fire AND earth AND air ) )"))
    var finish = Instant.now()
    println(Duration.between(start, finish).toMillis())

    start = Instant.now()
    println(wildCardSearch.searchByBooleanQuery("( a*ia OR ( wa*er AND fi*e AND ea*th AND a*r ) )"))
    finish = Instant.now()
    println(Duration.between(start, finish).toMillis())

    println("Neighbourhood queries:")
    start = Instant.now()
    println(distanceSearch.searchByQueryInNeighborhood("madness NEAR1000 horror NEAR300 space"))
    finish = Instant.now()
    println(Duration.between(start, finish).toMillis())

    start = Instant.now()
    println(wildCardSearch.searchByQueryInNeighborhood("ma*ss NEAR1000 h*or NEAR300 sp*ce"))
    finish = Instant.now()
    println(Duration.between(start, finish).toMillis())


    println("Phrase queries:")
    println(distanceSearch.searchByPhrase("I declare after all there is no enjoyment like reading"))
    println(wildCardSearch.searchByPhrase("I declare after all there is no enjoyment like reading"))
    start = Instant.now()
    println(distanceSearch.searchByPhrase("We live on a placid island of ignorance, in the midst of black seas of infinity"))
    finish = Instant.now()
    println(Duration.between(start, finish).toMillis())

    start = Instant.now()
    println(wildCardSearch.searchByPhrase("We live on a placid is*d of ignorance, in the m*st of black seas of inf*ty"))
    finish = Instant.now()
    println(Duration.between(start, finish).toMillis())

    start = Instant.now()
    println(distanceSearch.searchByPhrase("And if you are not crushed by such a pressure, it is because the air penetrates the interior of your body with equal pressure. Hence perfect equilibrium between the interior and exterior pressure, which thus neutralise each other, and which"))
    finish = Instant.now()
    println(Duration.between(start, finish).toMillis())

    start = Instant.now()
    println(wildCardSearch.searchByPhrase("And if you are n*t crushed by such a pr*re, it is because the air penetrates the interior of your body with equal pressure. Hence perfect equilibrium between the interior and exterior pressure, which thus neutralise each other, and wh*ch"))
    finish = Instant.now()
    println(Duration.between(start, finish).toMillis())

    start = Instant.now()
    println(wildCardSearch.getAllTermVariantsByWildCard("hi*"))
    finish = Instant.now()
    println(Duration.between(start, finish).toMillis())
}
