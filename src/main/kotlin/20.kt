import java.io.File

fun main() {
    val coordinates = readCoordinates()
    part1(coordinates)
    part2(coordinates.map { Coordinate(it.number * 811589153, it.originalPosition) })
}

fun part1(coordinates: List<Coordinate>) {
    printAnswer(mix(coordinates))
}

fun part2(coordinates: List<Coordinate>) {
    printAnswer(mix(coordinates, 10))
}

fun mix(originalCoordinates: List<Coordinate>, times: Int = 1): List<Coordinate> {
    val newCoordinates = originalCoordinates.toMutableList()

    repeat(times) {
        for (coordinate in originalCoordinates) {
            val currentPosition = newCoordinates.indexOf(coordinate)
            newCoordinates.removeAt(currentPosition)
            newCoordinates.add((coordinate.number + currentPosition).mod(newCoordinates.size), coordinate)
        }
    }

    return newCoordinates
}

fun printAnswer(coordinates: List<Coordinate>) {
    val zeroIndex = coordinates.indexOfFirst { it.number == 0L }
    println(
        coordinates[(zeroIndex + 1000) % coordinates.size].number +
                coordinates[(zeroIndex + 2000) % coordinates.size].number +
                coordinates[(zeroIndex + 3000) % coordinates.size].number
    )
}

fun readCoordinates(): List<Coordinate> =
    File(ClassLoader.getSystemResource("input20.txt").file).useLines { lines ->
        var originalPosition = 0
        lines
            .map { Coordinate(it.toLong(), originalPosition++) }
            .toList()
    }

data class Coordinate(val number: Long, val originalPosition: Int)
