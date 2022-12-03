import java.io.File

fun main() {
    val elves = readElves()
    println(elves.maxOf { it.foods.sum() })
    println(elves.map { it.foods.sum() }.sortedDescending().take(3).sum())
}

fun readElves(): List<Elf> {
    val elves = mutableListOf<Elf>()
    var currentElf = Elf.new()

    File(ClassLoader.getSystemResource("input1.txt").file).forEachLine {
        if (it.isBlank()) {
            elves.add(currentElf)
            currentElf = Elf.new()
        } else {
            currentElf.foods.add(it.toLong())
        }
    }

    return elves
}

data class Elf(
    val foods: MutableList<Long>,
) {

    companion object {

        fun new() = Elf(mutableListOf())
    }
}
