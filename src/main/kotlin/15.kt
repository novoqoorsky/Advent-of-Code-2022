import java.io.File
import kotlin.math.abs

fun main() {
    val sensorsAndBeacons = readSensorsAndBeacons()
    part1(sensorsAndBeacons)
    part2(sensorsAndBeacons)
}

fun readSensorsAndBeacons(): List<Pair<Sensor, Beacon>> {
    val regex = Regex("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)")

    return File(ClassLoader.getSystemResource("input15.txt").file).useLines { lines ->
        lines
            .filter { it.isNotBlank() }
            .map { regex.matchEntire(it) }
            .map {
                Pair(
                    Sensor(it!!.groups[1]!!.value.toInt(), it.groups[2]!!.value.toInt()),
                    Beacon(it.groups[3]!!.value.toInt(), it.groups[4]!!.value.toInt())
                )
            }
            .toList()
    }
}

fun part1(sensorsAndBeacons: List<Pair<Sensor, Beacon>>) {
    val noBeaconRanges = noBeaconRanges(sensorsAndBeacons)
    println(countNoBeaconPositionsAt(2000000, noBeaconRanges, sensorsAndBeacons))
}

fun part2(sensorsAndBeacons: List<Pair<Sensor, Beacon>>) {
    val noBeaconRanges = noBeaconRanges(sensorsAndBeacons)
    val lowerBound = 0
    val upperBound = 4000000
    for (x in upperBound downTo lowerBound) {
        val noBeaconPosition = findNoBeaconPositionAtX(x, noBeaconRanges, lowerBound, upperBound)

        if (noBeaconPosition != null) {
            println(4000000L * x + noBeaconPosition)
            break
        }
    }
}

fun noBeaconRanges(sensorsAndBeacons: List<Pair<Sensor, Beacon>>): NoBeaconRanges {
    val ranges = hashMapOf<Int, MutableList<Range>>()

    sensorsAndBeacons.map { (sensor, beacon) ->
        val manhattanDistance = abs(sensor.y - beacon.y) + abs(sensor.x - beacon.x)

        for (x in sensor.x - manhattanDistance..sensor.x + manhattanDistance) {
            ranges.getOrPut(x) { mutableListOf() }.add(
                Range(
                    sensor.y - (manhattanDistance - abs(sensor.x - x)),
                    sensor.y + (manhattanDistance - abs(sensor.x - x)),
                )
            )
        }
    }

    return NoBeaconRanges(ranges)
}

fun countNoBeaconPositionsAt(
    row: Int,
    noBeaconRanges: NoBeaconRanges,
    sensorsAndBeacons: List<Pair<Sensor, Beacon>>
): Int {
    var count = 0

    for (x in sensorsAndBeacons.minBeacon()..sensorsAndBeacons.maxBeacon() + 1000000) {
        if (sensorsAndBeacons.isBeaconAt(x, row)) {
            continue
        }

        val noBeaconRangesAtX = noBeaconRanges.ranges[x] ?: continue

        for (i in noBeaconRangesAtX.indices) {
            if (noBeaconRangesAtX[i].isIn(row)) {
                count++
                break
            }
        }
    }

    return count
}

fun findNoBeaconPositionAtX(x: Int, noBeaconRanges: NoBeaconRanges, lowerBound: Int, upperBound: Int): Int? {
    val noBeaconRangesAtX = noBeaconRanges.ranges[x] ?: return lowerBound

    var currentRange = noBeaconRangesAtX.filter { it.from <= lowerBound }.maxByOrNull { it.to }
    var currentPosition = currentRange?.to ?: return lowerBound

    while (currentPosition <= upperBound) {
        currentRange = noBeaconRangesAtX
            .filter { it.from <= currentPosition && it.to > currentPosition }
            .maxByOrNull { it.to }
        currentPosition = currentRange?.to ?: return currentPosition + 1
    }

    return null
}

data class Sensor(val x: Int, val y: Int)

data class Beacon(val x: Int, val y: Int)

data class NoBeaconRanges(val ranges: Map<Int, List<Range>>)

data class Range(var from: Int, var to: Int) {

    fun isIn(y: Int) = y in from..to
}

fun List<Pair<Sensor, Beacon>>.minBeacon(): Int =
    this.map { it.second }.minOf { it.x }

fun List<Pair<Sensor, Beacon>>.maxBeacon(): Int =
    this.map { it.second }.maxOf { it.x }

fun List<Pair<Sensor, Beacon>>.isBeaconAt(x: Int, y: Int) =
    this.map { it.second }.any { it.x == x && it.y == y }
