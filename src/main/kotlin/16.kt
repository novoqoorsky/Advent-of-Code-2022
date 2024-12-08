import java.io.File

fun main() {
    val valves = readValves()
    val distances = findDistancesBetweenAllValves(valves)
    var maxScore = 0

    fun findMaxScore(
        currentScore: Int,
        timeRemaining: Int,
        valve: Valve,
        visited: Set<Valve>,
        elephantHelping: Boolean
    ) {
        if (currentScore > maxScore) {
            maxScore = currentScore
        }

        distances.getAllFrom(valve).forEach { (to, distance) ->
            val remainingTimeAfterGettingToValveAndOpening = timeRemaining - distance - 1
            if (remainingTimeAfterGettingToValveAndOpening >= 0 && to !in visited) {
                findMaxScore(
                    currentScore + remainingTimeAfterGettingToValveAndOpening * to.flowRate,
                    remainingTimeAfterGettingToValveAndOpening,
                    to,
                    visited + to,
                    elephantHelping,
                )
            }
        }
        if (elephantHelping) {
            findMaxScore(
                currentScore,
                26,
                valves.single { it.name == "AA" },
                visited,
                false,
            )
        }
    }

    findMaxScore(0, 30, valves.single { it.name == "AA" }, setOf(), false)
    println(maxScore)
    maxScore = 0
    findMaxScore(0, 26, valves.single { it.name == "AA" }, setOf(), true)
    println(maxScore)
}

fun readValves(): List<Valve> {
    val regex = Regex("Valve ([A-Z]{2}) has flow rate=(\\d+); tunnels? leads? to valves? (.*)")

    val valveData = mutableListOf<MatchResult>()
    File(ClassLoader.getSystemResource("input16.txt").file).forEachLine {
        if (it.isNotBlank()) {
            valveData.add(regex.matchEntire(it)!!)
        }
    }

    val valves = valveData.map {
        Valve(
            it.groups[1]!!.value,
            it.groups[2]!!.value.toInt(),
        )
    }

    valveData.forEach {
        val valve = valves.single { valve -> valve.name == it.groups[1]!!.value }
        val tunnels = it.groups[3]!!.value.split(", ")
            .map { valveName ->
                valves.single { valve -> valve.name == valveName }
            }
        valve.tunnels = tunnels
    }

    return valves
}

fun findDistancesBetweenAllValves(valves: List<Valve>): MutableList<Distance> {
    val distances = mutableListOf<Distance>()
    valves.forEach { from ->
        from.tunnels.forEach { to ->
            distances.add(Distance(from, to, 1))
        }
    }

    for (k in valves.indices) {
        for (i in valves.indices) {
            for (j in valves.indices) {
                val ik = distances.getDistance(valves[i], valves[k])
                val kj = distances.getDistance(valves[k], valves[j])
                val ij = distances.getDistance(valves[i], valves[j])

                if (ik + kj < ij) {
                    distances.createOrUpdate(valves[i], valves[j], ik + kj)
                }
            }
        }
    }

    return distances.getRidOfPathsToZeroFlowRateValves()
}

fun MutableList<Distance>.getDistance(from: Valve, to: Valve): Int =
    this.singleOrNull { it.from == from && it.to == to }?.value ?: 30

fun MutableList<Distance>.createOrUpdate(from: Valve, to: Valve, value: Int) {
    this.singleOrNull { it.from == from && it.to == to }?.let { distance ->
        distance.value = value
    } ?: this.add(Distance(from, to, value))
}

fun MutableList<Distance>.getAllFrom(from: Valve): Map<Valve, Int> =
    this.filter { it.from == from }
        .associate { it.to to it.value }

fun MutableList<Distance>.getRidOfPathsToZeroFlowRateValves(): MutableList<Distance> =
    this.filter { it.to.flowRate != 0 }.toMutableList()

data class Distance(
    val from: Valve,
    val to: Valve,
    var value: Int,
)

class Valve(
    val name: String,
    val flowRate: Int,
    var tunnels: List<Valve> = listOf(),
) {

    override fun toString() = name
}
