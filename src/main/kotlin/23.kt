import Direction.EAST
import Direction.NORTH
import Direction.SOUTH
import Direction.WEST
import java.io.File

fun main() {
    val board = readElvesBoard()
    val directions = mutableListOf(NORTH, SOUTH, WEST, EAST)
    for (round in 0 until 1000) {
        if (round == 10) {
            println(board.elvesRectangleEmptyTiles())
        }

        if (!round(board, directions)) {
            println(round + 1)
            break
        }
        directions.add(3, directions.removeAt(0))
    }
}

fun round(board: ElvesBoard, directions: List<Direction>): Boolean =
    getProposals(board, directions).takeIf { it.isNotEmpty() }?.let {
        applyProposals(board, it)
        true
    } ?: false

fun applyProposals(board: ElvesBoard, proposals: Map<BoardElf, Pair<Int, Int>>) {
    proposals.forEach { (elf, point) ->
        if (proposals.values.count { it == point } == 1) {
            board.elves[elf.x][elf.y] = Empty()
            board.elves[point.first][point.second] = BoardElf(point.first, point.second)
        }
    }
}

fun getProposals(board: ElvesBoard, directions: List<Direction>): Map<BoardElf, Pair<Int, Int>> =
    board.elves.flatten()
        .filterIsInstance<BoardElf>()
        .filter { board.hasAnyAdjacent(it) }
        .mapNotNull { getProposal(it, board, directions) }
        .toMap()

fun getProposal(elf: BoardElf, board: ElvesBoard, directions: List<Direction>): Pair<BoardElf, Pair<Int, Int>>? {
    for (direction in directions) {
        when (direction) {
            NORTH -> if (board.canProposeMovingNorth(elf)) {
                return Pair(elf, Pair(elf.x - 1, elf.y))
            }

            SOUTH -> if (board.canProposeMovingSouth(elf)) {
                return Pair(elf, Pair(elf.x + 1, elf.y))
            }

            WEST -> if (board.canProposeMovingWest(elf)) {
                return Pair(elf, Pair(elf.x, elf.y - 1))
            }

            EAST -> if (board.canProposeMovingEast(elf)) {
                return Pair(elf, Pair(elf.x, elf.y + 1))
            }
        }
    }

    return null
}

fun readElvesBoard(): ElvesBoard {
    val elves = mutableListOf<MutableList<ElvesBoardPoint>>()
    var x = 0
    var y = 0

    File(ClassLoader.getSystemResource("input23.txt").file).forEachLine { line ->
        elves.add(mutableListOf())
        line.toCharArray().forEach {
            if (it == '.') {
                elves[x].add(Empty())
            } else {
                elves[x].add(BoardElf(x, y))
            }
            y++
        }
        y = 0
        x++
    }

    return ElvesBoard(elves).expand()
}

data class ElvesBoard(val elves: MutableList<MutableList<ElvesBoardPoint>>) {

    fun hasAnyAdjacent(elf: BoardElf): Boolean {
        return hasNorthAdjacent(elf) || hasNorthWestAdjacent(elf) || hasNorthEastAdjacent(elf) ||
                hasSouthAdjacent(elf) || hasSouthWestAdjacent(elf) || hasSouthEastAdjacent(elf) ||
                hasWestAdjacent(elf) || hasEastAdjacent(elf)
    }

    private fun hasNorthAdjacent(elf: BoardElf): Boolean = elves[elf.x - 1][elf.y] is BoardElf

    private fun hasNorthWestAdjacent(elf: BoardElf): Boolean = elves[elf.x - 1][elf.y - 1] is BoardElf

    private fun hasNorthEastAdjacent(elf: BoardElf): Boolean = elves[elf.x - 1][elf.y + 1] is BoardElf

    private fun hasSouthAdjacent(elf: BoardElf): Boolean = elves[elf.x + 1][elf.y] is BoardElf

    private fun hasSouthWestAdjacent(elf: BoardElf): Boolean = elves[elf.x + 1][elf.y - 1] is BoardElf

    private fun hasSouthEastAdjacent(elf: BoardElf): Boolean = elves[elf.x + 1][elf.y + 1] is BoardElf

    private fun hasWestAdjacent(elf: BoardElf): Boolean = elves[elf.x][elf.y - 1] is BoardElf

    private fun hasEastAdjacent(elf: BoardElf): Boolean = elves[elf.x][elf.y + 1] is BoardElf

    fun canProposeMovingNorth(elf: BoardElf): Boolean =
        !(hasNorthAdjacent(elf) || hasNorthWestAdjacent(elf) || hasNorthEastAdjacent(elf))

    fun canProposeMovingSouth(elf: BoardElf): Boolean =
        !(hasSouthAdjacent(elf) || hasSouthWestAdjacent(elf) || hasSouthEastAdjacent(elf))

    fun canProposeMovingWest(elf: BoardElf): Boolean =
        !(hasWestAdjacent(elf) || hasNorthWestAdjacent(elf) || hasSouthWestAdjacent(elf))

    fun canProposeMovingEast(elf: BoardElf): Boolean =
        !(hasEastAdjacent(elf) || hasNorthEastAdjacent(elf) || hasSouthEastAdjacent(elf))

    fun elvesRectangleEmptyTiles(): Int {
        var counter = 0
        val elvesRectangle = elves.flatten().filterIsInstance<BoardElf>()
        for (x in elvesRectangle.minOf { it.x }..elvesRectangle.maxOf { it.x }) {
            for (y in elvesRectangle.minOf { it.y }..elvesRectangle.maxOf { it.y }) {
                if (elves[x][y] is Empty) {
                    counter++
                }
            }
        }
        return counter
    }

    fun expand(expansionSize: Int = 100): ElvesBoard {
        for (i in 0 until expansionSize) {
            elves.add(0, MutableList(elves[0].size) { Empty() })
            elves.add(elves.size, MutableList(elves[0].size) { Empty() })
            elves.forEach {
                it.add(0, Empty())
                it.add(it.size, Empty())
            }
        }

        elves.flatten().filterIsInstance<BoardElf>().forEach {
            it.x += expansionSize
            it.y += expansionSize
        }

        return this
    }
}


enum class Direction {
    NORTH, SOUTH, WEST, EAST
}

class BoardElf(var x: Int, var y: Int) : ElvesBoardPoint() {

    override fun toString(): String = "#"
}

class Empty : ElvesBoardPoint() {

    override fun toString(): String = "."
}

abstract class ElvesBoardPoint
