import BoardPointType.EMPTY
import BoardPointType.OPEN_TILE
import BoardPointType.SOLID_WALL
import Facing.DOWN
import Facing.LEFT
import Facing.RIGHT
import Facing.UP
import java.io.File

fun main() {
    followPath(readBoard(), readPath(), ::simpleWrap)
    followPath(readBoard(), readPath(), ::cubeWrap) // WARNING: cubeWrap is suited only for one input!
}

fun followPath(board: Board, path: String, wrap: (Board, Move) -> Move) {
    var currentPosition = board.start()
    var facing = RIGHT

    var cursor = 0
    while (cursor < path.length) {
        if (cursor != 0) {
            facing = facing.turnNinetyDegrees(path[cursor++] == 'R')
        }

        var steps = ""
        while (cursor < path.length && path[cursor].isDigit()) {
            steps += path[cursor++]
        }

        val move = board.move(currentPosition, steps.toInt(), facing, wrap)
        currentPosition = move.position
        facing = move.facing
    }

    println(1000 * (currentPosition.first + 1) + 4 * (currentPosition.second + 1) + facing.value)
}

fun readBoard(): Board {
    val boardPoints = mutableListOf<MutableList<BoardPoint>>()
    var x = 0
    var y = 0

    File(ClassLoader.getSystemResource("input22.txt").file).forEachLine { line ->
        if (line.contains(".")) {
            boardPoints.add(mutableListOf())
            line.toCharArray().forEach {
                boardPoints[x].add(BoardPoint(BoardPointType.from(it), x, y))
            }
        }
        y = 0
        x++
    }

    return Board(boardPoints)
}

fun readPath(): String =
    File(ClassLoader.getSystemResource("input22.txt").file).useLines { lines ->
        lines
            .filter { it.any { char -> char.isLetterOrDigit() } }
            .joinToString()
    }

data class Board(val points: List<List<BoardPoint>>) {

    fun start(): Pair<Int, Int> = Pair(0, points[0].indexOfFirst { it.type == OPEN_TILE })

    fun move(currentPosition: Pair<Int, Int>, steps: Int, facing: Facing, wrap: (Board, Move) -> Move): Move {
        var x = currentPosition.first
        var y = currentPosition.second
        var currentFacing = facing
        var stepsTaken = 0

        fun processWrapping(wrapping: Move) {
            x = wrapping.position.first
            y = wrapping.position.second
            currentFacing = wrapping.facing
        }

        onWallHit@ while (stepsTaken < steps) {
            when (currentFacing) {
                RIGHT -> {
                    while (stepsTaken < steps) {
                        if (y == points[x].size - 1) {
                            val wrapped = wrap(this, Move(currentPosition, currentFacing))
                            if (points[wrapped.position.first][wrapped.position.second].type == SOLID_WALL) {
                                break@onWallHit
                            }
                            processWrapping(wrapped)
                            stepsTaken++
                            break
                        }
                        if (points[x][y + 1].type == SOLID_WALL) {
                            break@onWallHit
                        }
                        y++
                        stepsTaken++
                    }
                }

                LEFT -> {
                    while (stepsTaken < steps) {
                        if (y == 0 || points[x][y - 1].type == EMPTY) {
                            val wrapped = wrap(this, Move(currentPosition, currentFacing))
                            if (points[wrapped.position.first][wrapped.position.second].type == SOLID_WALL) {
                                break@onWallHit
                            }
                            processWrapping(wrapped)
                            stepsTaken++
                            break
                        }
                        if (points[x][y - 1].type == SOLID_WALL) {
                            break@onWallHit
                        }
                        y--
                        stepsTaken++
                    }
                }

                UP -> {
                    while (stepsTaken < steps) {
                        if (x == 0 || points[x - 1][y].type == EMPTY) {
                            val wrapped = wrap(this, Move(currentPosition, currentFacing))
                            if (points[wrapped.position.first][wrapped.position.second].type == SOLID_WALL) {
                                break@onWallHit
                            }
                            processWrapping(wrapped)
                            stepsTaken++
                            break
                        }
                        if (points[x - 1][y].type == SOLID_WALL) {
                            break@onWallHit
                        }
                        x--
                        stepsTaken++
                    }
                }

                DOWN -> {
                    while (stepsTaken < steps) {
                        if (x == points.filter { y < it.size }.map { it[y] }.last().x) {
                            val wrapped = wrap(this, Move(currentPosition, currentFacing))
                            if (points[wrapped.position.first][wrapped.position.second].type == SOLID_WALL) {
                                break@onWallHit
                            }
                            processWrapping(wrapped)
                            stepsTaken++
                            break
                        }
                        if (points[x + 1][y].type == SOLID_WALL) {
                            break@onWallHit
                        }
                        x++
                        stepsTaken++
                    }
                }
            }
        }

        return Move(Pair(x, y), currentFacing)
    }
}

fun simpleWrap(board: Board, move: Move): Move =
    when (move.facing) {
        RIGHT -> Move(
            Pair(move.position.first, board.points[move.position.first].indexOfFirst { it.type != EMPTY }), RIGHT
        )

        LEFT -> Move(
            Pair(move.position.first, board.points[move.position.first].indexOfLast { it.type != EMPTY }), LEFT
        )

        UP -> Move(
            Pair(board.points.filter { move.position.second < it.size }.map { it[move.position.second] }
                .indexOfLast { it.type != EMPTY }, move.position.second), UP
        )

        DOWN -> Move(
            Pair(board.points.filter { move.position.second < it.size }.map { it[move.position.second] }
                .indexOfFirst { it.type != EMPTY }, move.position.second), DOWN
        )
    }

fun cubeWrap(board: Board, move: Move): Move = // :)
    if (move.facing == RIGHT) {
        if (move.position.first in 0..49 && move.position.second in 100..149) {
            Move(Pair(149 - move.position.first, 99), LEFT)
        } else if (move.position.first in 50..99 && move.position.second in 50..99) {
            Move(Pair(49, 50 + move.position.first), UP)
        } else if (move.position.first in 100..149 && move.position.second in 50..99) {
            Move(Pair(149 - move.position.first, 149), LEFT)
        } else if (move.position.first in 150..199 && move.position.second in 0..49) {
            Move(Pair(149, move.position.first - 100), UP)
        } else throw IllegalArgumentException()
    } else if (move.facing == LEFT) {
        if (move.position.first in 0..49 && move.position.second in 50..99) {
            Move(Pair(149 - move.position.first, 0), RIGHT)
        } else if (move.position.first in 50..99 && move.position.second in 50..99) {
            Move(Pair(100, move.position.first - 50), DOWN)
        } else if (move.position.first in 100..149 && move.position.second in 0..49) {
            Move(Pair(149 - move.position.first, 50), RIGHT)
        } else if (move.position.first in 150..199 && move.position.second in 0..49) {
            Move(Pair(0, move.position.first - 100), DOWN)
        } else throw IllegalArgumentException()
    } else if (move.facing == UP) {
        if (move.position.first in 0..49 && move.position.second in 50..99) {
            Move(Pair(100 + move.position.second, 0), RIGHT)
        } else if (move.position.first in 0..49 && move.position.second in 100..149) {
            Move(Pair(199, move.position.second - 100), UP)
        } else if (move.position.first in 100..149 && move.position.second in 0..49) {
            Move(Pair(50 + move.position.second, 50), RIGHT)
        } else throw IllegalArgumentException()
    } else if (move.facing == DOWN) {
        if (move.position.first in 0..49 && move.position.second in 100..149) {
            Move(Pair(move.position.second - 50, 99), LEFT)
        } else if (move.position.first in 100..149 && move.position.second in 50..99) {
            Move(Pair(100 + move.position.second, 49), LEFT)
        } else if (move.position.first in 150..199 && move.position.second in 0..49) {
            Move(Pair(0, 100 + move.position.second), DOWN)
        } else throw IllegalArgumentException()
    } else throw IllegalArgumentException()

data class Move(val position: Pair<Int, Int>, val facing: Facing)

data class BoardPoint(val type: BoardPointType, val x: Int, val y: Int)

enum class BoardPointType {

    EMPTY,
    OPEN_TILE,
    SOLID_WALL,
    ;

    companion object {

        fun from(char: Char): BoardPointType =
            when (char) {
                ' ' -> EMPTY
                '.' -> OPEN_TILE
                '#' -> SOLID_WALL
                else -> throw IllegalArgumentException()
            }
    }
}

enum class Facing(val value: Int) {

    RIGHT(0),
    LEFT(2),
    UP(3),
    DOWN(1),
    ;

    fun turnNinetyDegrees(clockwise: Boolean): Facing =
        when (this) {
            RIGHT -> if (clockwise) DOWN else UP
            LEFT -> if (clockwise) UP else DOWN
            UP -> if (clockwise) RIGHT else LEFT
            DOWN -> if (clockwise) LEFT else RIGHT
        }
}
