import indexes.termsBTree.TermsBTree
import indexes.termsDictionary.buildTermsDictionary
import indexes.termsDictionary.writeTermsDictionaryToFile
import kotlinx.coroutines.*
import utils.getAllCharsFromFile

fun main(): Unit = runBlocking {
    val termsDictionary = buildTermsDictionary("./src/main/resources/collection")
    writeTermsDictionaryToFile(termsDictionary)

    val charset = getAllCharsFromFile("TermsDictionary.txt")
    charset.forEach { print(it) }
    print("\n")

    val termsBTree = TermsBTree(termsDictionary, charset, reverseTree = false)
    termsBTree.buildTermsBTree()
    val reversedTermsBTree = TermsBTree(termsDictionary, charset, reverseTree = true)
    reversedTermsBTree.buildTermsBTree()
    println("BTrees of terms is successfully built!")

    val termsByPrefix = termsBTree.findAllTermsByAffix("house")
    val termsByPrefix2 = termsBTree.findAllTermsByAffix("dar")
    termsByPrefix.forEach { print("$it, ") }
    print("\n")
    termsByPrefix2.forEach { print("$it, ") }
    print("\n")

    val termsBySuffix1 = reversedTermsBTree.findAllTermsByAffix("paired")
    termsBySuffix1.forEach { print("$it, ") }
    print("\n")
    val termsBySuffixBuggy = reversedTermsBTree.findAllTermsByAffix("repaired")
    termsBySuffixBuggy.forEach { print("$it, ") }
    print("\n")

    val termsBySuffix2 = reversedTermsBTree.findAllTermsByAffix("dire")
    termsBySuffix2.forEach { print("$it, ") }
    print("\n")

}
