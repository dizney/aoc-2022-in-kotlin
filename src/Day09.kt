import kotlin.math.abs

object Day09 {
    const val EXPECTED_PART1_CHECK_ANSWER = 13
    const val EXPECTED_PART2_CHECK_ANSWER = 1

    const val TAIL_SIZE_PART_2 = 9
}

enum class MovementDirection { U, D, L, R }

fun main() {

    fun Pair<Int, Int>.move(direction: MovementDirection) = when (direction) {
        MovementDirection.U -> copy(second = second + 1)
        MovementDirection.D -> copy(second = second - 1)
        MovementDirection.R -> copy(first = first + 1)
        MovementDirection.L -> copy(first = first - 1)
    }

    fun Pair<Int, Int>.moveAfterPrecursor(precursorPosition: Pair<Int, Int>): Pair<Int, Int> {
        val xDiff = precursorPosition.first - first
        val yDiff = precursorPosition.second - second
        if (abs(xDiff) > 1 || abs(yDiff) > 1) {
            return copy(
                first = first + (if (xDiff > 0) xDiff.inc() else xDiff.dec()) / 2,
                second = second + (if (yDiff > 0) yDiff.inc() else yDiff.dec()) / 2,
            )
        }
        return this
    }

    fun part1(input: List<String>): Int {
        val tailVisitedPositions = mutableSetOf<Pair<Int, Int>>()
        var currentHeadPosition = Pair(0, 0)
        var currentTailPosition = Pair(0, 0)
        tailVisitedPositions.add(currentTailPosition)
        input.forEach { movement ->
            val directionAndSteps = movement.split(" ")
            val (direction, steps) = Pair(MovementDirection.valueOf(directionAndSteps[0]), directionAndSteps[1].toInt())
            repeat(steps) {
                currentHeadPosition = currentHeadPosition.move(direction)

                currentTailPosition = currentTailPosition.moveAfterPrecursor(currentHeadPosition)
                tailVisitedPositions.add(currentTailPosition)
            }
        }
        return tailVisitedPositions.size
    }

    fun part2(input: List<String>): Int {
        val tailVisitedPositions = mutableSetOf<Pair<Int, Int>>()
        var currentHeadPosition = Pair(0, 0)
        val trailPositions = MutableList(Day09.TAIL_SIZE_PART_2) { Pair(0, 0) }
        tailVisitedPositions.add(trailPositions.last())
        input.forEach { movement ->
            val directionAndSteps = movement.split(" ")
            val (direction, steps) = Pair(MovementDirection.valueOf(directionAndSteps[0]), directionAndSteps[1].toInt())
            repeat(steps) {
                currentHeadPosition = currentHeadPosition.move(direction)

                trailPositions.forEachIndexed { idx, trailPosition ->
                    val newTrailPosition =
                        trailPosition.moveAfterPrecursor(if (idx == 0) currentHeadPosition else trailPositions[idx - 1])
                    trailPositions[idx] = newTrailPosition
                }
                tailVisitedPositions.add(trailPositions.last())
            }
        }
        return tailVisitedPositions.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == Day09.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    check(part2(testInput) == Day09.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
