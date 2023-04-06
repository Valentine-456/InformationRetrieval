package indexes.termsBTree

import indexes.termsDictionary.TermsDictionary
import utils.getAllCharsFromFile

fun testTermsBTree(termsDictionary: TermsDictionary) {
    val charset = getAllCharsFromFile("TermsDictionary.txt")

    val termsBTree = TermsBTree(termsDictionary, charset, reverseTree = false)
    termsBTree.buildTermsBTree()
    val reversedTermsBTree = TermsBTree(termsDictionary, charset, reverseTree = true)
    reversedTermsBTree.buildTermsBTree()

    val termsByPrefix = termsBTree.findAllTermsByAffix("house")
    println("Terms by prefix: $termsByPrefix")
    val termsBySuffix = reversedTermsBTree.findAllTermsByAffix("paired")
    println("Terms by suffix: $termsBySuffix")
}