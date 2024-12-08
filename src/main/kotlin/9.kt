import java.io.File
import kotlin.math.abs

fun main() {
    simulate(2)
    simulate(10)
}

fun simulate(ropeLength: Int) {
    val rope = Rope.new(ropeLength)
    val head = rope.head()
    val tail = rope.tail()
    val tailVisitedPositions = mutableListOf<Position>()

    File(ClassLoader.getSystemResource("input9.txt").file).forEachLine {
        val movement = Regex("([A-Z]) (\\d+)").matchEntire(it)
        val direction = movement!!.groups[1]!!.value
        val steps = movement.groups[2]!!.value.toInt()

        for (step in 1..steps) {
            head.move(direction)
            rope.followHead()
            tailVisitedPositions.add(tail.copy())
        }
    }

    println(tailVisitedPositions.distinct().size)
}

data class Position(
    var x: Int,
    var y: Int,
) {

    fun move(direction: String) =
        when (direction) {
            "R" -> x++
            "L" -> x--
            "U" -> y++
            "D" -> y--
            else -> throw IllegalArgumentException()
        }

    fun follow(other: Position) {
        this.x += other.x.compareTo(this.x)
        this.y += other.y.compareTo(this.y)
    }

    fun isAdjacent(other: Position): Boolean =
        abs(this.x - other.x) <= 1 && abs(this.y - other.y) <= 1
}

data class Rope(
    val positions: List<Position>
) {

    fun head() = positions.first()

    fun tail() = positions.last()

    fun get(i: Int) = positions[i]

    fun followHead() {
        for (knotIndex in 1 until positions.size) {
            val previousKnot = positions[knotIndex - 1]
            val currentKnot = positions[knotIndex]
            if (!currentKnot.isAdjacent(previousKnot)) {
                currentKnot.follow(previousKnot)
            }
        }
    }

    companion object {

        fun new(length: Int): Rope =
            Rope(List(length) { Position(0, 0) })
    }
}
