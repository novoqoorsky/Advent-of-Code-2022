import kotlin.math.floor

fun main() {
    part1()
    part2()
}

fun part1() {
    val monkeys = readMonkeys()
    for (i in 1..20) {
        round(monkeys.monkeys, 3.0, monkeys.leastCommonMultiple)
    }
    println(monkeys.monkeyBusiness())
}

fun part2() {
    val monkeys = readMonkeys()
    for (i in 1..10000) {
        round(monkeys.monkeys, 1.0, monkeys.leastCommonMultiple)
    }
    println(monkeys.monkeyBusiness())
}

fun round(monkeys: List<Monkey>, worryLevelDivisor: Double, leastCommonMultiple: Long) {
    for (m in monkeys.indices) {
        for (i in monkeys[m].items.indices) {
            monkeys[m].items[i] =
                (floor(monkeys[m].operation(monkeys[m].items[i]) / worryLevelDivisor).toLong()) % leastCommonMultiple
            monkeys[m].inspections++
            if (monkeys[m].test(monkeys[m].items[i])) {
                monkeys[monkeys[m].toMonkeyOnTrue].items.add(monkeys[m].items[i])
            } else {
                monkeys[monkeys[m].toMonkeyOnFalse].items.add(monkeys[m].items[i])
            }
        }
        monkeys[m].items = mutableListOf()
    }
}

fun readMonkeys() = Monkeys(
    listOf(
        Monkey(mutableListOf(54, 82, 90, 88, 86, 54), { x: Long -> x * 7 }, { x: Long -> x % 11 == 0L }, 2, 6),
        Monkey(mutableListOf(91, 65), { x: Long -> x * 13 }, { x: Long -> x % 5 == 0L }, 7, 4),
        Monkey(mutableListOf(62, 54, 57, 92, 83, 63, 63), { x: Long -> x + 1 }, { x: Long -> x % 7 == 0L }, 1, 7),
        Monkey(mutableListOf(67, 72, 68), { x: Long -> x * x }, { x: Long -> x % 2 == 0L }, 0, 6),
        Monkey(mutableListOf(68, 89, 90, 86, 84, 57, 72, 84), { x: Long -> x + 7 }, { x: Long -> x % 17 == 0L }, 3, 5),
        Monkey(mutableListOf(79, 83, 64, 58), { x: Long -> x + 6 }, { x: Long -> x % 13 == 0L }, 3, 0),
        Monkey(mutableListOf(96, 72, 89, 70, 88), { x: Long -> x + 4 }, { x: Long -> x % 3 == 0L }, 1, 2),
        Monkey(mutableListOf(79), { x: Long -> x + 8 }, { x: Long -> x % 19 == 0L }, 4, 5),
    ),
    11 * 5 * 7 * 2 * 17 * 13 * 3 * 19,
)

data class Monkey(
    var items: MutableList<Long>,
    val operation: (Long) -> Long,
    val test: (Long) -> Boolean,
    val toMonkeyOnTrue: Int,
    val toMonkeyOnFalse: Int,
    var inspections: Long = 0,
)

data class Monkeys(
    val monkeys: List<Monkey>,
    val leastCommonMultiple: Long,
) {

    fun monkeyBusiness(): Long = twoMostActiveMonkeys().map { it.inspections }.let { it[0] * it[1] }

    private fun twoMostActiveMonkeys(): List<Monkey> = monkeys.sortedByDescending { it.inspections }.take(2)
}
