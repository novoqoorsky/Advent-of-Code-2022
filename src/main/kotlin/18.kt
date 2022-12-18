import java.io.File
import java.util.Stack

fun main() {
    val lavaDroplet = readLavaDroplet()
    part1(lavaDroplet)
    part2(lavaDroplet)
}

fun part1(lavaDroplet: LavaDroplet) {
    println(lavaDroplet.cubes.sumOf { lavaDroplet.countAdjacentSurface(it) })
}

fun part2(lavaDroplet: LavaDroplet) {
    println(lavaDroplet.cubes.sumOf { lavaDroplet.countExteriorSurface(it) })
}

fun readLavaDroplet(): LavaDroplet =
    File(ClassLoader.getSystemResource("input18.txt").file).useLines { lines ->
        lines
            .map { it.split(",") }
            .map { Cube(it[0].toInt(), it[1].toInt(), it[2].toInt()) }
            .let { LavaDroplet(it.toList()) }
    }

data class Cube(val x: Int, val y: Int, val z: Int) {

    fun adjacent(): List<Cube> =
        listOf(
            copy(x = x - 1),
            copy(x = x + 1),
            copy(y = y - 1),
            copy(y = y + 1),
            copy(z = z - 1),
            copy(z = z + 1),
        )
}

data class LavaDroplet(val cubes: List<Cube>) {

    private val min = Cube(cubes.minOf { it.x }, cubes.minOf { it.y }, cubes.minOf { it.z })
    private val max = Cube(cubes.maxOf { it.x }, cubes.maxOf { it.y }, cubes.maxOf { it.z })

    fun countAdjacentSurface(cube: Cube): Int =
        cubes.none { it.x == cube.x - 1 && it.y == cube.y && it.z == cube.z }.toInt() +
                cubes.none { it.x == cube.x + 1 && it.y == cube.y && it.z == cube.z }.toInt() +
                cubes.none { it.y == cube.y - 1 && it.x == cube.x && it.z == cube.z }.toInt() +
                cubes.none { it.y == cube.y + 1 && it.x == cube.x && it.z == cube.z }.toInt() +
                cubes.none { it.z == cube.z - 1 && it.y == cube.y && it.x == cube.x }.toInt() +
                cubes.none { it.z == cube.z + 1 && it.y == cube.y && it.x == cube.x }.toInt()

    fun countExteriorSurface(cube: Cube): Int =
        canExpand(cube.copy(x = cube.x - 1)).toInt() +
                canExpand(cube.copy(x = cube.x + 1)).toInt() +
                canExpand(cube.copy(y = cube.y - 1)).toInt() +
                canExpand(cube.copy(y = cube.y - 1)).toInt() +
                canExpand(cube.copy(z = cube.z - 1)).toInt() +
                canExpand(cube.copy(z = cube.z - 1)).toInt()

    private fun canExpand(cube: Cube): Boolean {
        val stack = Stack<Cube>().apply { push(cube) }
        val visited = mutableSetOf(cube)

        while (stack.isNotEmpty()) {
            val current = stack.pop()

            if (isCubeWithin(current)) {
                continue
            }

            if (isCubeExternal(current)) {
                return true
            }

            current.adjacent()
                .filter { it !in visited }
                .forEach {
                    stack.push(it)
                    visited.add(it)
                }
        }
        return false
    }

    private fun isCubeExternal(cube: Cube): Boolean =
        cube.x < min.x || cube.x > max.x || cube.y < min.y || cube.y > max.y || cube.z < min.z || cube.z > max.z

    private fun isCubeWithin(cube: Cube): Boolean = cube in cubes

    private fun Boolean.toInt(): Int = if (this) 1 else 0
}


