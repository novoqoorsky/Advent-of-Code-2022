import java.io.File

fun main() {
    val trees = readTrees()
    part1(trees)
    part2(trees)
}

fun readTrees(): List<List<Int>> =
    File(ClassLoader.getSystemResource("input8.txt").file).useLines { lines ->
        lines.map {
            it.toCharArray().map { tree -> tree.digitToInt() }
        }.toList()
    }

fun part1(trees: List<List<Int>>) {
    var visibleTreesCount = 0

    for (x in trees.indices) {
        for (y in trees[x].indices) {
            if (isVisible(trees, x, y)) {
                visibleTreesCount++
            }
        }
    }

    println(visibleTreesCount)
}

fun part2(trees: List<List<Int>>) {
    val scenicScores = mutableListOf<Int>()

    for (x in trees.indices) {
        for (y in trees[x].indices) {
            scenicScores.add(scenicScore(trees, x, y))
        }
    }

    println(scenicScores.maxOrNull())
}

fun scenicScore(trees: List<List<Int>>, x: Int, y: Int): Int {
    val row = trees.getRow(x)
    val column = trees.getColumn(y)
    val treeHeight = trees[x][y]

    val leftScore = firstBlockingTreeDistance(row, treeHeight, y, true)
    val rightScore = firstBlockingTreeDistance(row, treeHeight, y, false)
    val topScore = firstBlockingTreeDistance(column, treeHeight, x, true)
    val bottomScore = firstBlockingTreeDistance(column, treeHeight, x, false)

    return leftScore * rightScore * topScore * bottomScore
}

fun firstBlockingTreeDistance(treesInLine: List<Int>, treeHeight: Int, treeLocation: Int, countDown: Boolean): Int {
    if (countDown) {
        var firstBlockingTree = 0
        for (i in treeLocation - 1 downTo 0) {
            if (treesInLine[i] >= treeHeight) {
                firstBlockingTree = i
                break
            }
        }
        return treeLocation - firstBlockingTree
    } else {
        var firstBlockingTree = treesInLine.size - 1
        for (i in treeLocation + 1 until treesInLine.size) {
            if (treesInLine[i] >= treeHeight) {
                firstBlockingTree = i
                break
            }
        }
        return firstBlockingTree - treeLocation
    }
}

fun isVisible(trees: List<List<Int>>, x: Int, y: Int): Boolean {
    val row = trees.getRow(x)
    val column = trees.getColumn(y)
    val treeHeight = trees[x][y]

    val isVisibleFromLeft = isVisible(row.subList(0, y), treeHeight)
    val isVisibleFromRight = isVisible(row.subList(y + 1, row.size), treeHeight)
    val isVisibleFromTop = isVisible(column.subList(0, x), treeHeight)
    val isVisibleFromBottom = isVisible(column.subList(x + 1, column.size), treeHeight)

    return isVisibleFromLeft || isVisibleFromRight || isVisibleFromBottom || isVisibleFromTop
}

fun isVisible(treesBlockingView: List<Int>, treeHeight: Int): Boolean =
    treesBlockingView.none { it >= treeHeight }

fun List<List<Int>>.getRow(x: Int): List<Int> =
    this[x]

fun List<List<Int>>.getColumn(y: Int): List<Int> =
    this.map { it[y] }

