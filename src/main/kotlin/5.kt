import java.io.File

fun main() {
    rearrange(::partOneRearrangement)
    rearrange(::partTwoRearrangement)
}

fun rearrange(rearrangement: (Int, ArrayDeque<Char>, ArrayDeque<Char>) -> Unit) {
    val stacks = stacks()
    val regex = Regex("move (\\d+) from (\\d+) to (\\d+)")

    File(ClassLoader.getSystemResource("input5.txt").file).forEachLine { line ->
        regex.matchEntire(line)!!.let {
            val elementsToMove = it.groups[1]!!.value.toInt()
            val fromStack = stacks[it.groups[2]!!.value.toInt() - 1]
            val toStack = stacks[it.groups[3]!!.value.toInt() - 1]

            rearrangement(elementsToMove, fromStack, toStack)
        }
    }

    stacks.forEach { print(it.last()) }
    println()
}

fun partOneRearrangement(elementsToMove: Int, fromStack: ArrayDeque<Char>, toStack: ArrayDeque<Char>) {
    for (i in 1..elementsToMove) {
        toStack.addLast(fromStack.removeLast())
    }
}

fun partTwoRearrangement(elementsToMove: Int, fromStack: ArrayDeque<Char>, toStack: ArrayDeque<Char>) {
    val removeAtIndex = fromStack.size - elementsToMove
    for (i in 1..elementsToMove) {
        toStack.addLast(fromStack.removeAt(removeAtIndex))
    }
}

fun stacks(): List<ArrayDeque<Char>> =
    listOf(
        ArrayDeque(listOf('S', 'Z', 'P', 'D', 'L', 'B', 'F', 'C')),
        ArrayDeque(listOf('N', 'V', 'G', 'P', 'H', 'W', 'B')),
        ArrayDeque(listOf('F', 'W', 'B', 'J', 'G')),
        ArrayDeque(listOf('G', 'J', 'N', 'F', 'L', 'W', 'C', 'S')),
        ArrayDeque(listOf('W', 'J', 'L', 'T', 'P', 'M', 'S', 'H')),
        ArrayDeque(listOf('B', 'C', 'W', 'G', 'F', 'S')),
        ArrayDeque(listOf('H', 'T', 'P', 'M', 'Q', 'B', 'W')),
        ArrayDeque(listOf('F', 'S', 'W', 'T')),
        ArrayDeque(listOf('N', 'C', 'R')),
    )
