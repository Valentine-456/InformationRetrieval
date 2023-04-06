package utils

fun listsIntersect(list1: ArrayList<Int>, list2: ArrayList<Int>): ArrayList<Int> {
    val answer = ArrayList<Int>()
    if (list1.isEmpty() or list2.isEmpty()) return answer
    var position1 = 0
    var position2 = 0
    var iteratingOverLists = true
    while (iteratingOverLists) {
        if (list1[position1] == list2[position2]) {
            answer.add(list1[position1])
            position1++
            position2++
        } else {
            if (list1[position1] < list2[position2]) position1++ else position2++
        }
        iteratingOverLists = (position1 < list1.size) and (position2 < list2.size)
    }
    return answer
}

fun listsUnion(list1: ArrayList<Int>, list2: ArrayList<Int>): ArrayList<Int> {
    val answer = ArrayList<Int>()
    if (list1.isEmpty()) return list2
    else if (list2.isEmpty()) return list1
    var position1 = 0
    var position2 = 0
    var iteratingOverLists = true
    while (iteratingOverLists) {
        if (list1[position1] == list2[position2]) {
            answer.add(list1[position1])
            position1++
            position2++
        } else {
            if (list1[position1] < list2[position2]) {
                answer.add(list1[position1])
                position1++
            } else {
                answer.add(list2[position2])
                position2++
            }
        }
        iteratingOverLists = (position1 < list1.size) and (position2 < list2.size)
    }
    while ((position1 < list1.size)) {
        answer.add(list1[position1])
        position1++
    }
    while ((position2 < list2.size)) {
        answer.add(list2[position2])
        position2++
    }
    return answer
}