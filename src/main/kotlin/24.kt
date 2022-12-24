import java.io.File

fun main() {
    part1(readValley())
    part2(readValley())
}

fun part1(valley: Valley) {
    println(valley.findTimeFromTo(valley.entry, valley.exit))
}

fun part2(valley: Valley) {
    println(
        valley.findTimeFromTo(valley.entry, valley.exit) +
                valley.findTimeFromTo(valley.exit, valley.entry) +
                valley.findTimeFromTo(valley.entry, valley.exit)
    )
}

fun readValley(): Valley {
    val valley = mutableListOf<CharArray>()
    File(ClassLoader.getSystemResource("input24.txt").file).forEachLine {
        valley.add(it.toCharArray())
    }

    val blizzards = mutableListOf<Blizzard>()

    for (x in valley.indices) {
        for (y in valley[x].indices) {
            if (valley[x][y] in setOf('>', '<', '^', 'v')) {
                blizzards.add(Blizzard.from(valley[x][y], x, y))
            }
        }
    }

    return Valley(
        blizzards,
        Pair(0, valley[0].indexOfFirst { it == '.' }),
        Pair(valley.size - 1, valley[valley.size - 1].indexOfFirst { it == '.' }),
        valley[0].size,
    )
}

data class Valley(val blizzards: List<Blizzard>, val entry: Pair<Int, Int>, val exit: Pair<Int, Int>, val width: Int) {

    fun findTimeFromTo(from: Pair<Int, Int>, to: Pair<Int, Int>): Int {
        var foundDestinations = mutableSetOf(from)
        var time = 0

        while (to !in foundDestinations) {
            time++
            val newDestinations = mutableSetOf<Pair<Int, Int>>()
            moveBlizzards()

            foundDestinations.forEach { fromDestination ->
                tryWait(fromDestination)?.let { newDestinations.add(it) }
                tryMoveNorth(fromDestination)?.let { newDestinations.add(it) }
                tryMoveSouth(fromDestination)?.let { newDestinations.add(it) }
                tryMoveWest(fromDestination)?.let { newDestinations.add(it) }
                tryMoveEast(fromDestination)?.let { newDestinations.add(it) }
            }

            foundDestinations = newDestinations
        }

        return time
    }

    private fun tryWait(from: Pair<Int, Int>): Pair<Int, Int>? {
        if (!isBlizzardAt(from.first, from.second)) {
            return Pair(from.first, from.second)
        }
        return null
    }

    private fun tryMoveNorth(from: Pair<Int, Int>): Pair<Int, Int>? {
        if (from.first < 1 || (from.first == 1 && from.second != entry.second)) {
            return null
        }
        if (isBlizzardAt(from.first - 1, from.second)) {
            return null
        }

        return Pair(from.first - 1, from.second)
    }

    private fun tryMoveSouth(from: Pair<Int, Int>): Pair<Int, Int>? {
        if (from.first == exit.first - 1 && from.second != exit.second || from.first == exit.first) {
            return null
        }
        if (isBlizzardAt(from.first + 1, from.second)) {
            return null
        }

        return Pair(from.first + 1, from.second)
    }

    private fun tryMoveWest(from: Pair<Int, Int>): Pair<Int, Int>? {
        if (from.second == 1 || from.first == 0 || from.first == exit.first) {
            return null
        }
        if (isBlizzardAt(from.first, from.second - 1)) {
            return null
        }

        return Pair(from.first, from.second - 1)
    }

    private fun tryMoveEast(from: Pair<Int, Int>): Pair<Int, Int>? {
        if (from.second == width - 2 || from.first == 0) {
            return null
        }
        if (isBlizzardAt(from.first, from.second + 1)) {
            return null
        }

        return Pair(from.first, from.second + 1)
    }

    private fun moveBlizzards() {
        blizzards.forEach {
            when (it.direction) {
                Direction.NORTH -> moveBlizzardNorth(it)
                Direction.SOUTH -> moveBlizzardSouth(it)
                Direction.WEST -> moveBlizzardWest(it)
                Direction.EAST -> moveBlizzardEast(it)
            }
        }
    }

    private fun moveBlizzardNorth(blizzard: Blizzard) {
        if (blizzard.x == 1) {
            moveBlizzard(blizzard, Pair(exit.first - 1, blizzard.y))
        } else {
            moveBlizzard(blizzard, Pair(blizzard.x - 1, blizzard.y))
        }
    }

    private fun moveBlizzardSouth(blizzard: Blizzard) {
        if (blizzard.x == exit.first - 1) {
            moveBlizzard(blizzard, Pair(1, blizzard.y))
        } else {
            moveBlizzard(blizzard, Pair(blizzard.x + 1, blizzard.y))
        }
    }

    private fun moveBlizzardWest(blizzard: Blizzard) {
        if (blizzard.y == 1) {
            moveBlizzard(blizzard, Pair(blizzard.x, width - 2))
        } else {
            moveBlizzard(blizzard, Pair(blizzard.x, blizzard.y - 1))
        }
    }

    private fun moveBlizzardEast(blizzard: Blizzard) {
        if (blizzard.y == width - 2) {
            moveBlizzard(blizzard, Pair(blizzard.x, 1))
        } else {
            moveBlizzard(blizzard, Pair(blizzard.x, blizzard.y + 1))
        }
    }

    private fun moveBlizzard(blizzard: Blizzard, to: Pair<Int, Int>) {
        blizzard.x = to.first
        blizzard.y = to.second
    }

    private fun isBlizzardAt(x: Int, y: Int): Boolean = blizzards.any { it.x == x && it.y == y }
}

data class Blizzard(val direction: Direction, var x: Int, var y: Int) {

    companion object {

        fun from(char: Char, x: Int, y: Int): Blizzard =
            when (char) {
                '>' -> Blizzard(Direction.EAST, x, y)
                '<' -> Blizzard(Direction.WEST, x, y)
                '^' -> Blizzard(Direction.NORTH, x, y)
                'v' -> Blizzard(Direction.SOUTH, x, y)
                else -> throw IllegalArgumentException()
            }
    }
}
