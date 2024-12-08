import java.io.File

fun main() {

    val assignmentPairs = readAssignmentPairs()
    println(assignmentPairs.count { it.isFullyContained() })
    println(assignmentPairs.count { it.hasOverlap() })
}

fun readAssignmentPairs(): List<AssignmentPair> {
    val regex = Regex("(\\d+)-(\\d+),(\\d+)-(\\d+)")

    return File(ClassLoader.getSystemResource("input4.txt").file).useLines { lines ->
        lines.map { line ->
            regex.matchEntire(line)!!.let {
                AssignmentPair(
                    Assignment(it.groups[1]!!.value.toInt(), it.groups[2]!!.value.toInt()),
                    Assignment(it.groups[3]!!.value.toInt(), it.groups[4]!!.value.toInt()),
                )
            }
        }.toList()
    }
}

data class Assignment(
    val lowerBound: Int,
    val upperBound: Int,
) {

    fun contains(other: Assignment): Boolean =
        this.lowerBound <= other.lowerBound && this.upperBound >= other.upperBound

    fun overlaps(other: Assignment): Boolean =
        this.lowerBound <= other.upperBound && other.lowerBound <= this.upperBound
}

data class AssignmentPair(
    val assignment1: Assignment,
    val assignment2: Assignment,
) {

    fun isFullyContained(): Boolean =
        assignment1.contains(assignment2) || assignment2.contains(assignment1)

    fun hasOverlap(): Boolean =
        assignment1.overlaps(assignment2) || assignment2.overlaps(assignment1)
}

