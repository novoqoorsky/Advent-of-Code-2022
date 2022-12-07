import java.io.File as IoFile

fun main() {
    val root = readDirectories()
    part1(root)
    part2(root)
}

fun part1(root: Directory) {
    val directoriesBiggerThan100000 = mutableListOf<Directory>()
    findDirectoriesThat(root, directoriesBiggerThan100000) { directory: Directory -> directory.size() <= 100000 }
    println(directoriesBiggerThan100000.sumOf { it.size() })
}

fun part2(root: Directory) {
    val remainingSpace = 70000000 - root.size()
    val directoriesToDelete = mutableListOf<Directory>()
    findDirectoriesThat(
        root,
        directoriesToDelete
    ) { directory: Directory -> directory.size() >= 30000000 - remainingSpace }
    println(directoriesToDelete.minOf { it.size() })
}

fun readDirectories(): Directory {
    val cdRegex = Regex("\\$ cd (.+)")
    val ls = "$ ls"
    val dirRegex = Regex("dir (.+)")
    val fileRegex = Regex("(\\d+) (.+)")

    val root = Directory("./", mutableSetOf(), mutableSetOf(), null)
    var currentDirectory = root

    IoFile(ClassLoader.getSystemResource("input7.txt").file).forEachLine {
        if (cdRegex.matches(it)) {
            val argument = cdRegex.matchEntire(it)!!.groups[1]!!.value
            if (argument == "..") {
                currentDirectory = currentDirectory.parent!!
            } else {
                var newCurrentDirectory = currentDirectory.children.singleOrNull { child -> child.name == argument }
                if (newCurrentDirectory == null) {
                    newCurrentDirectory = Directory(argument, mutableSetOf(), mutableSetOf(), currentDirectory)
                    currentDirectory.children.add(newCurrentDirectory)
                }
                currentDirectory = newCurrentDirectory
            }
        } else if (dirRegex.matches(it) || it == ls) {
            // ignore
        } else if (fileRegex.matches(it)) {
            val matches = fileRegex.matchEntire(it)
            currentDirectory.files.add(File(matches!!.groups[2]!!.value, matches.groups[1]!!.value.toLong()))
        } else {
            throw IllegalArgumentException("Unknown command $it")
        }
    }

    return root
}

fun findDirectoriesThat(parent: Directory, answer: MutableList<Directory>, predicate: (Directory) -> Boolean) {
    if (predicate(parent)) {
        answer.add(parent)
    }
    parent.children.forEach {
        findDirectoriesThat(it, answer, predicate)
    }
}

data class File(
    val name: String,
    val size: Long,
)

class Directory(
    val name: String,
    val files: MutableSet<File>,
    val children: MutableSet<Directory>,
    val parent: Directory?,
) {

    fun size(): Long =
        files.sumOf { it.size } + children.sumOf { it.size() }
}


