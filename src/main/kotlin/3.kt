import java.io.File

fun main() {
    val rucksacks = readRucksacks()
    println(rucksacks.sumOf { priority(findError(it)) })

    val elfGroups = readElfGroups()
    println(elfGroups.sumOf { priority(findBadge(it)) })
}

fun readRucksacks(): List<Rucksack> =
    File(ClassLoader.getSystemResource("input3.txt").file).useLines { lines ->
        lines.map {
            Rucksack(
                Compartment(it.take(it.length / 2).toCharArray()),
                Compartment(it.takeLast(it.length / 2).toCharArray()),
            )
        }.toList()
    }

fun readElfGroups(): List<ElfGroup> =
    File(ClassLoader.getSystemResource("input3.txt").file).useLines {
        it.chunked(3).map { group ->
            ElfGroup(
                ElfRucksack(group[0].toCharArray()),
                ElfRucksack(group[1].toCharArray()),
                ElfRucksack(group[2].toCharArray()),
            )
        }.toList()
    }


fun findError(rucksack: Rucksack): Char {
    rucksack.compartment1.items.forEach {
        if (rucksack.compartment2.items.contains(it)) {
            return it
        }
    }
    throw IllegalArgumentException()
}

fun findBadge(elfGroup: ElfGroup): Char {
    elfGroup.elfRucksack1.items.forEach {
        if (elfGroup.elfRucksack2.items.contains(it) && elfGroup.elfRucksack3.items.contains(it)) {
            return it
        }
    }
    throw IllegalArgumentException()
}

fun priority(c: Char): Int =
    if (c.isUpperCase()) {
        c.code - 38
    } else if (c.isLowerCase()) {
        c.code - 96
    } else throw IllegalArgumentException()

data class Rucksack(
    val compartment1: Compartment,
    val compartment2: Compartment,
)

data class Compartment(
    val items: CharArray,
)

data class ElfRucksack(
    val items: CharArray,
)

data class ElfGroup(
    val elfRucksack1: ElfRucksack,
    val elfRucksack2: ElfRucksack,
    val elfRucksack3: ElfRucksack,
)
