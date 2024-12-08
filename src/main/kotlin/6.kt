import java.io.File

fun main() {
    println(findMarker(4))
    println(findMarker(14))
}

fun findMarker(distinctCharactersCount: Int): Int {
    var marker = -1
    File(ClassLoader.getSystemResource("input6.txt").file).forEachLine {
        for (i in it.indices) {
            if (it.subSequence(i, i + distinctCharactersCount).toSet().size == distinctCharactersCount) {
                marker = i + distinctCharactersCount
                break
            }
        }
    }
    return marker
}
