package indexes.spimi

fun testSPIMI() {
    val startTime = System.nanoTime()
    val spimi = SPIMI(tokensChunkSize = 20000)
    spimi.execute(binaryOutput = false)
    val stopTime = System.nanoTime()
    println("Execution time in milliseconds: ${(stopTime - startTime) / 1000_000.0}")
}