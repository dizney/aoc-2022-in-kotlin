
const val SCORE_ROCK = 1
const val SCORE_PAPER = 2
const val SCORE_SCISSORS = 3

const val DRAW = 'Y'
const val LOSE = 'X'
const val WIN = 'Z'

const val EXPECTED_PART1_CHECK_ANSWER = 15
const val EXPECTED_PART2_CHECK_ANSWER = 12

const val POS_THEIR_CHOICE = 0
const val POS_OUR_CHOICE = 2

const val SCORE_WIN = 6
const val SCORE_DRAW = 3
const val SCORE_LOSE = 0

enum class Shape(val shapeScore: Int) {
    Rock(SCORE_ROCK),
    Paper(SCORE_PAPER),
    Scissors(SCORE_SCISSORS);

    companion object {
        fun from(char: Char): Shape =
            when (char) {
                'A', 'X' -> Rock
                'B', 'Y' -> Paper
                'C', 'Z' -> Scissors
                else -> error("Unknown value $char")
            }
    }
}

val BEATS = mapOf(
    Shape.Rock to Shape.Scissors,
    Shape.Paper to Shape.Rock,
    Shape.Scissors to Shape.Paper,
)

val BEATEN_BY = mapOf(
    Shape.Scissors to Shape.Rock,
    Shape.Rock to Shape.Paper,
    Shape.Paper to Shape.Scissors,
)

fun main() {

    fun getScore(theirs: Shape, ours: Shape) = when {
        ours == theirs -> ours.shapeScore + SCORE_DRAW
        BEATS[ours] == theirs -> ours.shapeScore + SCORE_WIN
        else -> ours.shapeScore + SCORE_LOSE
    }

    fun part1(input: List<String>): Int {
        return input.sumOf {
            val theirs = Shape.from(it[POS_THEIR_CHOICE])
            val ours = Shape.from(it[POS_OUR_CHOICE])
            getScore(theirs, ours)
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf {
            val theirs = Shape.from(it[POS_THEIR_CHOICE])
            val ours = when (it[POS_OUR_CHOICE]) {
                DRAW -> theirs
                LOSE -> BEATS[theirs]!!
                WIN -> BEATEN_BY[theirs]!!
                else -> error("Wrong input")
            }
            getScore(theirs, ours)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == EXPECTED_PART1_CHECK_ANSWER)
    check(part2(testInput) == EXPECTED_PART2_CHECK_ANSWER)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
