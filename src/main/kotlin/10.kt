import java.io.File

fun main() {
    val cycles = readCycles()
    part1(cycles)
    part2(cycles)
}

fun readCycles(): List<Int> {
    val cycles = mutableListOf(1)

    File(ClassLoader.getSystemResource("input10.txt").file).forEachLine { line ->
        line.split(" ").let {
            if (it.size == 1 && it[0] == "noop") {
                cycles.add(cycles[cycles.size - 1])
            } else if (it.size == 2) {
                val x = it[1].toInt()
                val registerValue = cycles[cycles.size - 1]
                val newRegisterValue = registerValue + x
                cycles.addAll(listOf(registerValue, newRegisterValue))
            } else throw IllegalArgumentException()
        }
    }

    return cycles
}

fun part1(cycles: List<Int>) {
    println(
        cycles[19] * 20 + cycles[59] * 60 + cycles[99] * 100 + cycles[139] * 140 + cycles[179] * 180 + cycles[219] * 220
    )
}

fun part2(cycles: List<Int>) {
    val crt = Array(6) { CharArray(40) { '.' } }

    for (cycle in cycles.indices) {
        val spriteMiddlePosition = cycles[cycle]
        val (x, y) = cycleToIndices(cycle)
        if (y in spriteMiddlePosition - 1..spriteMiddlePosition + 1) {
            crt[x][y] = '#'
        }
    }

    crt.render()
}

fun cycleToIndices(cycle: Int): Pair<Int, Int> =
    Pair(cycle / 40, cycle % 40)

fun Array<CharArray>.render() {
    for (x in indices) {
        for (y in 0 until this[x].size) {
            print(this[x][y])
        }
        println()
    }
}
