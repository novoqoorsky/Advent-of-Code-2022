import java.io.File

fun main() {
    val moves1 = readMoves1()
    println(moves1.sumOf { it.second.scoreAgainst(it.first) })

    val moves2 = readMoves2()
    println(moves2.sumOf { it.second.scoreAgainst(it.first) })
}

fun readMoves1(): List<Pair<OpponentMove, MyMove>> =
    File(ClassLoader.getSystemResource("input2.txt").file).useLines { lines ->
        lines
            .map { it.split(" ", limit = 2) }
            .map { Pair(OpponentMove.from(it[0]), MyMove.from(it[1])) }
            .toList()
    }


fun readMoves2(): List<Pair<OpponentMove, MyMove>> =
    File(ClassLoader.getSystemResource("input2.txt").file).useLines { lines ->
        lines
            .map { it.split(" ", limit = 2) }
            .map {
                OpponentMove.from(it[0]).let { opponentMove ->
                    Pair(opponentMove, MyMove.from(opponentMove, GameResult.from(it[1])))
                }
            }
            .toList()
    }

enum class OpponentMove(
    val code: String
) {

    ROCK("A"),
    PAPER("B"),
    SCISSORS("C")
    ;

    companion object {

        fun from(code: String): OpponentMove =
            values().singleOrNull { it.code == code }!!
    }
}

enum class MyMove(
    val code: String,
    private val value: Int,
) {

    ROCK("X", 1),
    PAPER("Y", 2),
    SCISSORS("Z", 3)
    ;

    fun scoreAgainst(opponentMove: OpponentMove): Int =
        gameResult(opponentMove).value + this.value

    private fun gameResult(opponentMove: OpponentMove): GameResult =
        when (opponentMove) {
            OpponentMove.ROCK ->
                when (this) {
                    ROCK -> GameResult.DRAW
                    PAPER -> GameResult.WIN
                    SCISSORS -> GameResult.LOSS
                }

            OpponentMove.PAPER ->
                when (this) {
                    ROCK -> GameResult.LOSS
                    PAPER -> GameResult.DRAW
                    SCISSORS -> GameResult.WIN
                }

            OpponentMove.SCISSORS -> when (this) {
                ROCK -> GameResult.WIN
                PAPER -> GameResult.LOSS
                SCISSORS -> GameResult.DRAW
            }
        }

    companion object {

        fun from(code: String): MyMove =
            values().singleOrNull { it.code == code }!!

        fun from(opponentMove: OpponentMove, gameResult: GameResult): MyMove =
            when (opponentMove) {
                OpponentMove.ROCK ->
                    when (gameResult) {
                        GameResult.WIN -> PAPER
                        GameResult.DRAW -> ROCK
                        GameResult.LOSS -> SCISSORS
                    }

                OpponentMove.PAPER ->
                    when (gameResult) {
                        GameResult.WIN -> SCISSORS
                        GameResult.DRAW -> PAPER
                        GameResult.LOSS -> ROCK
                    }

                OpponentMove.SCISSORS -> when (gameResult) {
                    GameResult.WIN -> ROCK
                    GameResult.DRAW -> SCISSORS
                    GameResult.LOSS -> PAPER
                }
            }
    }
}

enum class GameResult(
    val value: Int,
) {

    WIN(6),
    DRAW(3),
    LOSS(0)
    ;

    companion object {

        fun from(code: String): GameResult =
            when (code) {
                "X" -> LOSS
                "Y" -> DRAW
                "Z" -> WIN
                else -> throw IllegalArgumentException()
            }
    }
}
