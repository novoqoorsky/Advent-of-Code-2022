import java.io.File
import kotlin.math.ceil
import kotlin.math.max

fun main() {
    val blueprints = readBlueprints()
    part1(blueprints)
    part2(blueprints.take(3))
}

fun readBlueprints(): List<Blueprint> {
    val regex =
        Regex("Blueprint \\d+: Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.")

    return File(ClassLoader.getSystemResource("input19.txt").file).useLines { lines ->
        lines
            .filter { it.isNotBlank() }
            .map {
                val matches = regex.matchEntire(it)!!
                Blueprint(
                    listOf(
                        OreRobot(matches.groups[1]!!.value.toInt()),
                        ClayRobot(matches.groups[2]!!.value.toInt()),
                        ObsidianRobot(matches.groups[3]!!.value.toInt(), matches.groups[4]!!.value.toInt()),
                        GeodeRobot(matches.groups[5]!!.value.toInt(), matches.groups[6]!!.value.toInt())
                    )
                )
            }
            .toList()
    }
}

fun part1(blueprints: List<Blueprint>) {
    var quality = 0
    for (i in 1..blueprints.size) {
        quality += i * blueprints[i - 1].determineQuality(24)
    }
    println(quality)
}

fun part2(blueprints: List<Blueprint>) {
    var quality = 1
    for (i in blueprints.indices) {
        quality *= blueprints[i].determineQuality(32)
    }
    println(quality)
}

data class Blueprint(
    val robots: List<Robot>,
) {
    private var maxQuality = 0

    fun determineQuality(
        remainingTime: Int,
        resources: Resources = Resources(),
        robotCounts: Map<Class<out Robot>, Int> = mapOf(
            OreRobot::class.java to 1,
            ClayRobot::class.java to 0,
            ObsidianRobot::class.java to 0,
            GeodeRobot::class.java to 0,
        ),
    ): Int {
        val geodeRobots = robotCounts[GeodeRobot::class.java]!!
        var quality = resources.geode + geodeRobots * remainingTime

        if (quality + (remainingTime * remainingTime - remainingTime) / 2 <= maxQuality) {
            return 0
        }

        for (robot in robots) {
            val waitingTime = robot.waitingTimeForResources(resources, robotCounts) + 1
            if (remainingTime - waitingTime < 1) {
                continue
            }

            quality = max(
                quality,
                determineQuality(
                    remainingTime - waitingTime,
                    resources.collect(robotCounts, waitingTime).also { robot.craftFrom(it) },
                    robotCounts.addRobot(robot.javaClass),
                )
            )
        }

        if (quality > maxQuality) {
            maxQuality = quality
        }
        return quality
    }
}

abstract class Robot(
    val oreCost: Int = 0,
    val clayCost: Int = 0,
    val obsidianCost: Int = 0,
) {

    protected val maxWaitingTime = Int.MAX_VALUE

    abstract fun craftFrom(resources: Resources)

    abstract fun waitingTimeForResources(resources: Resources, robotsCount: Map<Class<out Robot>, Int>): Int
}

abstract class OreCraftedRobot(oreCost: Int) : Robot(oreCost) {

    override fun craftFrom(resources: Resources) {
        resources.ore -= oreCost
    }

    override fun waitingTimeForResources(resources: Resources, robotsCount: Map<Class<out Robot>, Int>): Int {
        val oreRobots = robotsCount[OreRobot::class.java]!!

        return if (oreRobots == 0) {
            maxWaitingTime
        } else if (resources.ore >= oreCost) {
            0
        } else {
            ceil((oreCost - resources.ore) / oreRobots.toDouble()).toInt()
        }
    }
}

class OreRobot(oreCost: Int) : OreCraftedRobot(oreCost = oreCost)

class ClayRobot(oreCost: Int) : OreCraftedRobot(oreCost = oreCost)

class ObsidianRobot(oreCost: Int, clayCost: Int) : Robot(oreCost = oreCost, clayCost = clayCost) {

    override fun craftFrom(resources: Resources) {
        resources.ore -= oreCost
        resources.clay -= clayCost
    }

    override fun waitingTimeForResources(resources: Resources, robotsCount: Map<Class<out Robot>, Int>): Int {
        val oreRobots = robotsCount[OreRobot::class.java]!!
        val clayRobots = robotsCount[ClayRobot::class.java]!!

        return if (oreRobots == 0 || clayRobots == 0) {
            maxWaitingTime
        } else if (resources.ore >= oreCost && resources.clay >= clayCost) {
            0
        } else {
            max(
                ceil((oreCost - resources.ore) / oreRobots.toDouble()).toInt(),
                ceil((clayCost - resources.clay) / clayRobots.toDouble()).toInt()
            )
        }
    }
}

class GeodeRobot(oreCost: Int, obsidianCost: Int) : Robot(oreCost = oreCost, obsidianCost = obsidianCost) {

    override fun craftFrom(resources: Resources) {
        resources.ore -= oreCost
        resources.obsidian -= obsidianCost
    }

    override fun waitingTimeForResources(resources: Resources, robotsCount: Map<Class<out Robot>, Int>): Int {
        val oreRobots = robotsCount[OreRobot::class.java]!!
        val obsidianRobots = robotsCount[ObsidianRobot::class.java]!!

        return if (oreRobots == 0 || obsidianRobots == 0) {
            maxWaitingTime
        } else if (resources.ore >= oreCost && resources.obsidian >= obsidianCost) {
            0
        } else {
            max(
                ceil((oreCost - resources.ore) / oreRobots.toDouble()).toInt(),
                ceil((obsidianCost - resources.obsidian) / obsidianRobots.toDouble()).toInt(),
            )
        }
    }
}

data class Resources(
    var ore: Int = 0,
    var clay: Int = 0,
    var obsidian: Int = 0,
    var geode: Int = 0,
) {

    fun collect(robotsCount: Map<Class<out Robot>, Int>, time: Int): Resources =
        Resources(
            ore + robotsCount[OreRobot::class.java]!! * time,
            clay + robotsCount[ClayRobot::class.java]!! * time,
            obsidian + robotsCount[ObsidianRobot::class.java]!! * time,
            geode + robotsCount[GeodeRobot::class.java]!! * time,
        )
}

fun Map<Class<out Robot>, Int>.addRobot(robotClass: Class<out Robot>): Map<Class<out Robot>, Int> =
    this.toMutableMap().apply { this[robotClass] = this[robotClass]!! + 1 }.toMap()



