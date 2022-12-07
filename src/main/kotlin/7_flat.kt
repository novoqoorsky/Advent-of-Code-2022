import java.io.File as IoFile

fun main() {
    val root = readFlatDirectories().structure()
    part1(root)
    part2(root)
}

fun readFlatDirectories(): Map<FlatDirectory, List<File>> {
    val cdRegex = Regex("\\$ cd (.+)")
    val ls = "$ ls"
    val dirRegex = Regex("dir (.+)")
    val fileRegex = Regex("(\\d+) (.+)")

    val directories = mutableMapOf<FlatDirectory, MutableList<File>>()
    var currentDirectory = ""
    val folderStack = ArrayDeque<String>()

    IoFile(ClassLoader.getSystemResource("input7.txt").file).forEachLine {
        if (cdRegex.matches(it)) {
            currentDirectory = handleCd(cdRegex.matchEntire(it)!!.groups[1]!!.value, folderStack)
        } else if (dirRegex.matches(it) || it == ls) {
            // ignore
        } else if (fileRegex.matches(it)) {
            val matches = fileRegex.matchEntire(it)
            directories.getOrPut(FlatDirectory(currentDirectory)) { mutableListOf() }
                .add(File(matches!!.groups[2]!!.value, matches.groups[1]!!.value.toLong()))
        } else {
            throw IllegalArgumentException("Unknown command $it")
        }
    }

    return directories
}

fun handleCd(argument: String, folderStack: ArrayDeque<String>): String {
    if (argument == "..") {
        folderStack.removeLast()
        return folderStack.joinToString("\\")
    }
    folderStack.addLast(argument)
    return folderStack.joinToString("\\")
}

data class FlatDirectory(
    val name: String,
)

fun Map<FlatDirectory, List<File>>.structure(): Directory {
    val root = Directory("./", mutableSetOf(), mutableSetOf(), null)
    this.forEach { (directory, files) ->
        val directories = directory.name.split("\\")
        var parentDirectory = root
        for (i in directories.indices) {
            var currentDirectory = parentDirectory.children.singleOrNull { it.name == directories[i] }
            if (currentDirectory == null) {
                currentDirectory = Directory(directories[i], mutableSetOf(), mutableSetOf(), null)
                parentDirectory.children.add(currentDirectory)
            }
            parentDirectory = currentDirectory
        }
        parentDirectory.files.addAll(files)
    }
    return root
}
