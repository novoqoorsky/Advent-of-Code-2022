import java.io.File
import kotlin.math.max
import kotlin.math.min

fun main() {
    println(simulateFallingSand(readCave()))
    println(simulateFallingSand2(readCave()))
}

fun readCave(): Cave {
    val cave = Cave.empty()

    File(ClassLoader.getSystemResource("input14.txt").file).forEachLine { line ->
        val rocks = line.split(" -> ")
        for (i in 0 until rocks.size - 1) {
            val fromRock = rocks[i].split(",").let { Pair(it[1].toInt(), it[0].toInt()) }
            val toRock = rocks[i + 1].split(",").let { Pair(it[1].toInt(), it[0].toInt()) }

            if (fromRock.first == toRock.first) {
                for (y in min(fromRock.second, toRock.second)..max(fromRock.second, toRock.second)) {
                    cave.material[fromRock.first][y] = Rock()
                }
            } else if (fromRock.second == toRock.second) {
                for (x in min(fromRock.first, toRock.first)..max(fromRock.first, toRock.first)) {
                    cave.material[x][fromRock.second] = Rock()
                }
            } else throw IllegalArgumentException()
        }
    }

    return cave
}

fun simulateFallingSand(cave: Cave): Int = runSimulation(cave, Cave::atBottom, Cave::atBottom) - 1

fun simulateFallingSand2(cave: Cave): Int {
    cave.addFloor()

    return runSimulation(cave, { _: Cave, _: Int -> false }, Cave::sandSourceBlocked)
}

fun runSimulation(
    cave: Cave,
    individualUnitFallBreakCondition: (Cave, Int) -> Boolean,
    simulationBreakCondition: (Cave, Int) -> Boolean,
): Int {
    var units = 0

    while (true) {
        var currentSandX = 0
        var currentSandY = 500

        while (true) {
            val newSandPosition = tryMoveSand(cave, currentSandX, currentSandY)

            if (newSandPosition == null) {
                val sandRestPosition = sandRestPosition(cave, currentSandX, currentSandY)
                cave.material[sandRestPosition.x][sandRestPosition.y] = Sand()
                break
            }

            currentSandX = newSandPosition.x
            currentSandY = newSandPosition.y

            if (individualUnitFallBreakCondition(cave, currentSandX)) {
                break
            }
        }

        units++

        if (simulationBreakCondition(cave, currentSandX)) {
            break
        }
    }

    return units
}

fun tryMoveSand(cave: Cave, x: Int, y: Int): SandPosition? {
    if (cave.material[x + 1][y] is Air) {
        return SandPosition(x + 1, y)
    }
    if (cave.material[x + 1][y - 1] is Air) {
        return SandPosition(x + 1, y - 1)
    }
    if (cave.material[x + 1][y + 1] is Air) {
        return SandPosition(x + 1, y + 1)
    }
    return null
}

fun sandRestPosition(cave: Cave, x: Int, y: Int): SandPosition =
    if (cave.material[x + 1][y - 1] is Air) {
        SandPosition(x + 1, y - 1)
    } else if (cave.material[x + 1][y + 1] is Air) {
        SandPosition(x + 1, y + 1)
    } else {
        SandPosition(x, y)
    }

class Air : Material {
    override fun toString() = "."
}

class Sand : Material {
    override fun toString() = "o"
}

class Rock : Material {
    override fun toString() = "#"
}

interface Material

data class Cave(
    val material: List<MutableList<Material>>
) {

    fun print(fromX: Int, toX: Int, fromY: Int, toY: Int) {
        for (x in fromX..toX) {
            for (y in fromY..toY) {
                print(material[x][y])
            }
            println()
        }
    }

    private fun bottom(): Int = material.indexOfLast { it.any { material -> material is Rock } }

    private fun floor(): Int = this.bottom() + 2

    fun atBottom(x: Int): Boolean = x >= bottom()

    fun sandSourceBlocked(x: Int): Boolean = material[0][500] is Sand

    fun addFloor() {
        val floor = floor()
        for (y in 0 until 1000) {
            material[floor][y] = Rock()
        }
    }

    companion object {

        fun empty(): Cave = Cave(MutableList(200) { MutableList(1000) { Air() } })
    }
}

data class SandPosition(val x: Int, val y: Int)
