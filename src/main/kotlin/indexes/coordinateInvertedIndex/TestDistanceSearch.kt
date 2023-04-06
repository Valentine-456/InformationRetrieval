package indexes.coordinateInvertedIndex

import indexes.termsDictionary.TermsDictionary

fun testDistanceSearch(termsDictionary: TermsDictionary) {
    val coordinateInvertedIndex = CoordinateInvertedIndex()
    coordinateInvertedIndex.buildInvertedIndex(termsDictionary)
    val distanceSearch = DistanceSearch(termsDictionary, coordinateInvertedIndex)

    println("Boolean queries:")
    println(distanceSearch.searchByBooleanQuery("( czech OR ( poland OR asia ) ) AND ( germany AND france ) OR ( mary AND shelly )"))
    println("Neighbourhood queries:")
    println(distanceSearch.searchByQueryInNeighborhood("madness NEAR1000 horror NEAR300 space"))
    println(distanceSearch.searchByQueryInNeighborhood("god NEAR1000 horror NEAR200 human"))
    println("Phrase queries:")
    println(distanceSearch.searchByPhrase("I declare after all there is no enjoyment like reading"))
    println(distanceSearch.searchByPhrase("We live on a placid island of ignorance, in the midst of black seas of infinity"))
    println(distanceSearch.searchByPhrase("And if you are not crushed by such a pressure, it is because the air penetrates the interior of your body with equal pressure. Hence perfect equilibrium between the interior and exterior pressure, which thus neutralise each other, and which"))

}