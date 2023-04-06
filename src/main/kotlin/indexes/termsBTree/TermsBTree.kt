package indexes.termsBTree

import indexes.termsDictionary.TermsDictionary


class TermsBTree(
    private val termsDictionary: TermsDictionary,
    private val charset: Array<Char>,
    private val reverseTree: Boolean = false
) {
    private val t = charset.size
    private val root = TermsBTreeNode(null)

    fun buildTermsBTree() {
        termsDictionary.termsDict.forEach {
            val term = if (reverseTree) it.reversed() else it
            val termChars = term.toCharArray()
            var currentNode = root
            termChars.forEachIndexed { positionOfChar, character ->
                val index = charset.indexOf(character)
                if (currentNode.children[index] == null) {
                    currentNode.children[index] = TermsBTreeNode(character)
                }
                if (positionOfChar == termChars.size - 1) {
                    currentNode.children[index]?.endOfTerm = true
                }
                currentNode = currentNode.children[index]!!
            }
        }
    }

    fun findAllTermsByAffix(affix: String): ArrayList<String> {
        val transformedAffix = if (reverseTree) affix.reversed() else affix
        val affixChars = transformedAffix.toCharArray()
        val subbranch = root.findSubbranchByPrefix(affixChars) ?: return arrayListOf()

        val subterms = subbranch.getAllSubtermsFromSubbranch()
        if (subterms.isEmpty()) return ArrayList()

        val terms = subterms.map {
            val fullTermCharArray = CharArray(affixChars.size + it.size)
            for (i in affixChars.indices) {
                fullTermCharArray[i] = affixChars[i]
            }
            for (i in it.indices) {
                fullTermCharArray[i + affixChars.size] = it[i]
            }
            val fullTermCharArrayTransformed =
                if (reverseTree) fullTermCharArray.reversedArray()
                else fullTermCharArray

            String(fullTermCharArrayTransformed)
        } as ArrayList
        if (subbranch.endOfTerm) terms.add(affix)
        return terms
    }

    inner class TermsBTreeNode(val value: Char?) {
        val children = arrayOfNulls<TermsBTreeNode>(t)
        var endOfTerm = false

        fun findSubbranchByPrefix(prefixChars: CharArray): TermsBTreeNode? {
            var childNode: TermsBTreeNode? = null
            for (branch in children) {
                if (branch == null) continue
                if (branch.value == prefixChars[0]) childNode = branch
            }
            if (childNode == null) return childNode
            else {
                val nextPrefixChars = CharArray(prefixChars.size - 1)
                for (i in 1 until prefixChars.size) {
                    nextPrefixChars[i - 1] = prefixChars[i]
                }
                return if (childNode.value == prefixChars.last()) childNode
                else childNode.findSubbranchByPrefix(nextPrefixChars)
            }
        }

        fun getAllSubtermsFromSubbranch(): ArrayList<CharArray> {
            val terms = arrayListOf<CharArray>()
            val notnullSubbranches = children.filterNotNull()
            if (notnullSubbranches.isEmpty()) {
                terms.add("".toCharArray())
                return terms
            }
            notnullSubbranches.forEach {
                val currentValue = it.value!!
                val subterms = it.getAllSubtermsFromSubbranch()
                val fullSubterms = subterms.map {
                    val fullCharArray = CharArray(it.size + 1)
                    fullCharArray[0] = currentValue
                    for (i in it.indices) {
                        fullCharArray[i + 1] = it[i]
                    }
                    fullCharArray
                } as ArrayList
                val isNewWord = it.endOfTerm and it.children.filterNotNull().isNotEmpty()
                if (isNewWord) terms.add(charArrayOf(it.value))
                terms.addAll(fullSubterms)
            }
            return terms
        }
    }
}
