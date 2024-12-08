import java.io.File
import kotlin.math.pow

fun main() {
    println(
        readSnafuNumbers()
            .sumOf { it.snafuToDecimal() }
            .decimalToSnafu()
    )
}

fun readSnafuNumbers(): List<String> =
    File(ClassLoader.getSystemResource("input25.txt").file).readLines()

fun String.snafuToDecimal(): Long {
    var decimal = 0.0

    for (i in this.indices) {
        val power = 5.0.pow(this.length - i - 1)
        decimal += when (this[i]) {
            '0' -> 0.0
            '1' -> power
            '2' -> 2 * power
            '-' -> -power
            '=' -> -2 * power
            else -> throw IllegalArgumentException()
        }
    }

    return decimal.toLong()
}

fun Long.decimalToSnafu(): String {
    var snafu = ""
    var remainder = this
    while (remainder > 0) {
        val snafuDigit = remainder % 5
        snafu = when (snafuDigit) {
            0L -> "0"
            1L -> "1"
            2L -> "2"
            3L -> {
                remainder += 5
                "="
            }
            4L -> {
                remainder += 5
                "-"
            }
            else -> throw IllegalArgumentException()
        } + snafu
        remainder /= 5
    }
    return snafu
}
