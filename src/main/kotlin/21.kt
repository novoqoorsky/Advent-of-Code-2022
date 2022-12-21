import java.io.File

fun main() {
    part1(readMonkeyNumbers().toMutableMap())
    part2(readMonkeyNumbers().toMutableMap())
}

fun part1(monkeyNumbers: MutableMap<String, MonkeyNumber>) {
    calculate(MonkeyNumber.ROOT, monkeyNumbers)
    println(monkeyNumbers[MonkeyNumber.ROOT]!!.value)
}

fun calculate(
    monkeyName: String,
    monkeyNumbers: MutableMap<String, MonkeyNumber>,
    ignore: (String) -> Boolean = { false },
) {
    var alreadyCalculatedCount = 0

    while (monkeyNumbers[monkeyName]!!.value == null) {
        val alreadyCalculated = monkeyNumbers.getAlreadyCalculated()
        alreadyCalculated.forEach { calculated ->
            monkeyNumbers.findIsLeft(calculated.key).forEach {
                if (!ignore(it.value.operation!!.left) && !ignore(it.value.operation!!.right)) {
                    it.value.assignLeftValue(calculated.value.value!!)
                    it.value.tryExecute()
                }
            }
            monkeyNumbers.findIsRight(calculated.key).forEach {
                if (!ignore(it.value.operation!!.left) && !ignore(it.value.operation!!.right)) {
                    it.value.assignRightValue(calculated.value.value!!)
                    it.value.tryExecute()
                }
            }
        }
        val nowCalculatedCount = monkeyNumbers.count { it.value.value != null }
        if (alreadyCalculatedCount == nowCalculatedCount) {
            break
        }
        alreadyCalculatedCount = nowCalculatedCount
    }
}

fun part2(monkeyNumbers: MutableMap<String, MonkeyNumber>) {
    val rootLeft = monkeyNumbers[MonkeyNumber.ROOT]!!.operation!!.left
    val rootRight = monkeyNumbers[MonkeyNumber.ROOT]!!.operation!!.right

    calculate(rootLeft, monkeyNumbers) { monkeyName: String -> monkeyName == MonkeyNumber.HUMN }
    calculate(rootRight, monkeyNumbers) { monkeyName: String -> monkeyName == MonkeyNumber.HUMN }

    if (monkeyNumbers[rootRight]!!.value != null) {
        monkeyNumbers[rootLeft]!!.value = monkeyNumbers[rootRight]!!.value
        println(reverseCalculate(rootLeft, monkeyNumbers))
    } else if (monkeyNumbers[rootLeft]!!.value != null) {
        monkeyNumbers[rootRight]!!.value = monkeyNumbers[rootLeft]!!.value
        println(reverseCalculate(rootRight, monkeyNumbers))
    } else throw IllegalStateException()
}

fun reverseCalculate(monkeyName: String, monkeyNumbers: MutableMap<String, MonkeyNumber>): Long =
    monkeyNumbers[monkeyName]!!.let {
        if (it.operation?.left == MonkeyNumber.HUMN) {
            return it.operation.reverseOperator!!(it.value!!, monkeyNumbers[it.operation.right]!!.value!!)
        }
        if (it.operation?.right == MonkeyNumber.HUMN) {
            return it.operation.operator!!(it.operation.leftValue!!, monkeyNumbers[it.operation.left]!!.value!!)
        }

        if (it.operation!!.rightValue != null) {
            it.operation.leftValue = it.operation.reverseOperator!!(it.value!!, it.operation.rightValue!!)
            monkeyNumbers[it.operation.left]!!.value = it.operation.leftValue
            return reverseCalculate(it.operation.left, monkeyNumbers)
        } else if (it.operation.leftValue != null) {
            it.operation.rightValue = if (it.operation.commutative) {
                it.operation.reverseOperator!!(it.value!!, it.operation.leftValue!!)
            } else {
                it.operation.operator!!(it.operation.leftValue!!, it.value!!)
            }
            monkeyNumbers[it.operation.right]!!.value = it.operation.rightValue
            return reverseCalculate(it.operation.right, monkeyNumbers)
        } else throw IllegalStateException()
    }


fun readMonkeyNumbers(): Map<String, MonkeyNumber> {
    val regex = Regex("([a-z]+) ([+\\-*/]) ([a-z]+)")

    return File(ClassLoader.getSystemResource("input21.txt").file).useLines { lines ->
        lines
            .map { it.split(": ") }
            .associate {
                it[1].toLongOrNull()
                    ?.let { value -> it[0] to MonkeyNumber(it[1], value) }
                    ?: (it[0] to regex.matchEntire(it[1])!!.let { match ->
                        MonkeyNumber(
                            it[1],
                            operation = Operation.from(
                                match.groups[1]!!.value,
                                match.groups[3]!!.value,
                                match.groups[2]!!.value[0]
                            )
                        )
                    })
            }
    }
}

data class MonkeyNumber(
    val formula: String,
    var value: Long? = null,
    val operation: Operation? = null,
) {

    fun assignLeftValue(value: Long) {
        operation!!.leftValue = value
    }

    fun assignRightValue(value: Long) {
        operation!!.rightValue = value
    }

    fun tryExecute() {
        operation?.let {
            if (it.leftValue != null && it.rightValue != null) {
                execute()
            }
        }
    }

    private fun execute() {
        value = operation!!.let { it.operator!!(it.leftValue!!, it.rightValue!!) }
    }

    companion object {

        const val ROOT = "root"
        const val HUMN = "humn"
    }
}

data class Operation(
    val operator: ((Long, Long) -> Long)? = null,
    val commutative: Boolean,
    val reverseOperator: ((Long, Long) -> Long)? = null,
    val left: String,
    val right: String,
    var leftValue: Long? = null,
    var rightValue: Long? = null,
) {

    companion object {

        fun from(left: String, right: String, operator: Char): Operation =
            Operation(
                operator = when (operator) {
                    '+' -> { a: Long, b: Long -> a + b }
                    '-' -> { a: Long, b: Long -> a - b }
                    '*' -> { a: Long, b: Long -> a * b }
                    '/' -> { a: Long, b: Long -> a / b }
                    else -> throw IllegalArgumentException()
                },
                operator in setOf('+', '*'),
                reverseOperator = when (operator) {
                    '+' -> { a: Long, b: Long -> a - b }
                    '-' -> { a: Long, b: Long -> a + b }
                    '*' -> { a: Long, b: Long -> a / b }
                    '/' -> { a: Long, b: Long -> a * b }
                    else -> throw IllegalArgumentException()
                },
                left,
                right,
            )
    }
}

fun MutableMap<String, MonkeyNumber>.getAlreadyCalculated() =
    this.filter { it.value.value != null }

fun MutableMap<String, MonkeyNumber>.findIsLeft(monkeyName: String) =
    this.filter { it.value.operation?.left == monkeyName }

fun MutableMap<String, MonkeyNumber>.findIsRight(monkeyName: String) =
    this.filter { it.value.operation?.right == monkeyName }
