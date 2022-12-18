import java.io.File

fun main() {
    val jetsOfGas = readJetsOfGas()
    val chamber = Chamber()
    var jetsOfGasPosition = 0
    var fallDown = false
    var currentRockNumber = 0
    var currentRock = RockShape.new(ROCKS[currentRockNumber], 0)

    while (chamber.rocksStopped < 2022) {
        if (fallDown) {
            if (chamber.isRockStopped(currentRock)) {
                chamber.addRock(currentRock)
                currentRockNumber = if (currentRockNumber + 1 < 5) currentRockNumber + 1 else 0
                currentRock = RockShape.new(ROCKS[currentRockNumber], chamber.height)
            } else {
                currentRock.fallDown()
            }
        } else {
            when (jetsOfGas[jetsOfGasPosition]) {
                '<' -> if (!chamber.isRockLeftBlocked(currentRock)) currentRock.pushLeft()
                '>' -> if (!chamber.isRockRightBlocked(currentRock)) currentRock.pushRight()
                else -> throw IllegalArgumentException()
            }
            jetsOfGasPosition = if (jetsOfGasPosition + 1 < jetsOfGas.length) jetsOfGasPosition + 1 else 0
        }
        fallDown = !fallDown
    }
    println(chamber.height)
    chamber.visualise()
    println(350 + ((1000000000000L - 207) / 1740) * 2754 + 1529)
}

fun readJetsOfGas(): String =
    File(ClassLoader.getSystemResource("input17.txt").file).useLines { it.joinToString() }

data class Chamber(
    var rocks: MutableList<RockShape> = mutableListOf(),
    var rocksStopped: Long = 0,
    var height: Int = 1,
) {

    fun addRock(rock: RockShape) {
        rocks.add(rock)
        rocksStopped++
        height = rocks.flatMap { it.fragments }.maxOf { it.x } + 1
    }

    fun isRockLeftBlocked(rock: RockShape): Boolean =
        rock.fragments.minOf { it.y } == 0 ||
                rock.fragments.any { fragment ->
                    rocks.flatMap { it.fragments }.any { it.x == fragment.x && it.y == fragment.y - 1 }
                }

    fun isRockRightBlocked(rock: RockShape): Boolean =
        rock.fragments.maxOf { it.y } == 6 ||
                rock.fragments.any { fragment ->
                    rocks.flatMap { it.fragments }.any { it.x == fragment.x && it.y == fragment.y + 1 }
                }

    fun isRockStopped(rock: RockShape): Boolean =
        isRockAtTheBottom(rock) || isRockTouchingAnyOther(rock)

    private fun isRockTouchingAnyOther(rock: RockShape): Boolean =
        rock.fragments.any { fragment ->
            rocks.flatMap { it.fragments }.any { fragment.x - 1 == it.x && fragment.y == it.y }
        }

    private fun isRockAtTheBottom(rock: RockShape) = rock.fragments.any { it.x == 0 }

    fun visualise() {
        for (x in height + 4 downTo 0) {
            for (y in 0..7) {
                if (rocks.flatMap { it.fragments }.any { it.x == x && it.y == y }) {
                    print("#")
                } else {
                    print(".")
                }
            }
            println()
        }
    }
}

data class RockShape(
    val fragments: List<RockFragment>
) {

    fun fallDown() {
        fragments.forEach { it.x-- }
    }

    fun pushLeft() {
        fragments.forEach { it.y-- }
    }

    fun pushRight() {
        fragments.forEach { it.y++ }
    }

    companion object {

        fun new(model: RockShape, height: Int): RockShape =
            RockShape(model.fragments.map { RockFragment(it.x + height, it.y) })
    }
}

data class RockFragment(var x: Int, var y: Int)

val ROCKS = listOf(
    RockShape(
        listOf(
            RockFragment(3, 2),
            RockFragment(3, 3),
            RockFragment(3, 4),
            RockFragment(3, 5),
        )
    ),
    RockShape(
        listOf(
            RockFragment(3, 3),
            RockFragment(4, 2),
            RockFragment(4, 3),
            RockFragment(4, 4),
            RockFragment(5, 3),
        )
    ),
    RockShape(
        listOf(
            RockFragment(3, 2),
            RockFragment(3, 3),
            RockFragment(3, 4),
            RockFragment(4, 4),
            RockFragment(5, 4),
        )
    ),
    RockShape(
        listOf(
            RockFragment(3, 2),
            RockFragment(4, 2),
            RockFragment(5, 2),
            RockFragment(6, 2),
        )
    ),
    RockShape(
        listOf(
            RockFragment(3, 2),
            RockFragment(3, 3),
            RockFragment(4, 2),
            RockFragment(4, 3),
        )
    ),
)
