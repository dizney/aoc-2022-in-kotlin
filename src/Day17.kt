object Day17 {
    const val EXPECTED_PART1_CHECK_ANSWER = 3068
    const val EXPECTED_PART2_CHECK_ANSWER = 1514285714288

    const val PART1_STOP_THE_ROCK_COUNT = 2022
    const val PART2_STOP_THE_ROCK_COUNT = 1000000000000
    const val CHAMBER_WIDTH = 7
    const val ROCK_START_LEFT = 2
    const val ROCK_START_BOTTOM = 3
}

enum class RockShape(val coordinates: Set<Coordinates>) {
    HORIZ_STRIPE(setOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(2, 0), Coordinates(3, 0))),
    VERT_STRIPE(setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(0, 2), Coordinates(0, 3))),
    STAR(setOf(Coordinates(1, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(2, 1), Coordinates(1, 2))),
    FLIPPED_L(setOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(2, 0), Coordinates(2, 1), Coordinates(2, 2))),
    SQUARE(setOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(0, 1), Coordinates(1, 1)));

    val width
        get() = coordinates.maxBy { it.x }.x - coordinates.minBy { it.x }.x + 1

    val height
        get() = coordinates.maxBy { it.y }.y - coordinates.minBy { it.y }.y + 1
}

data class FallingRock(val shape: RockShape, var position: Coordinates)

val RockFallOrder =
    listOf(RockShape.HORIZ_STRIPE, RockShape.STAR, RockShape.FLIPPED_L, RockShape.VERT_STRIPE, RockShape.SQUARE)

class Chamber(val jetStream: CharArray) {
    private var currentStep = 0
    private var currentFallingRock: FallingRock
    val stoppedRocks = mutableListOf<FallingRock>()
    private val occupiedPositions = MutableList(1000) { BooleanArray(Day17.CHAMBER_WIDTH) }
    private var highestOccupiedPosition = -1

    init {
        currentFallingRock = nextRock(null)
    }

    private fun nextRock(prevRock: FallingRock?) = FallingRock(
        RockFallOrder[if (prevRock != null) (RockFallOrder.indexOf(prevRock.shape) + 1) % RockFallOrder.size else 0],
        Coordinates(
            Day17.ROCK_START_LEFT,
            highestOccupiedPosition + 1 + Day17.ROCK_START_BOTTOM
        )
    )

    private fun hitsWallOrOtherRock(shape: RockShape, coordinates: Coordinates) =
        shape.coordinates.any {
            val xPosition = coordinates.x + it.x
            val yPosition = coordinates.y + it.y
            if (xPosition >= 0 && xPosition < Day17.CHAMBER_WIDTH && yPosition >= 0 && yPosition < occupiedPositions.size) {
                occupiedPositions[yPosition][xPosition]
            } else yPosition < 0 || xPosition < 0 || xPosition >= Day17.CHAMBER_WIDTH
        }

    private fun applyJetStream() {
        when (val pushDirection = jetStream[currentStep % jetStream.size]) {
            '<' -> {
                if (!hitsWallOrOtherRock(currentFallingRock.shape, currentFallingRock.position.copy(x = currentFallingRock.position.x - 1))) {
                    currentFallingRock.position = currentFallingRock.position
                        .copy(x = currentFallingRock.position.x - 1)
                }
            }

            '>' -> {
                if (!hitsWallOrOtherRock(currentFallingRock.shape, currentFallingRock.position.copy(x = currentFallingRock.position.x + 1))) {
                    currentFallingRock.position = currentFallingRock.position
                        .copy(x = currentFallingRock.position.x + 1)
                }
            }

            else -> error("Unknown push direction $pushDirection")
        }
    }

    private fun applyGravity() {
        val newPosition = currentFallingRock.position.copy(y = currentFallingRock.position.y - 1)

        if (hitsWallOrOtherRock(currentFallingRock.shape, newPosition)) {
            stoppedRocks += currentFallingRock
            currentFallingRock.shape.coordinates.forEach {
                if (currentFallingRock.position.x + it.x < Day17.CHAMBER_WIDTH) {
                    if (occupiedPositions[currentFallingRock.position.y + it.y][currentFallingRock.position.x + it.x]) {
                        error("Occupied position for $currentFallingRock ${currentFallingRock.position.x + it.x}, ${currentFallingRock.position.y + it.y} is already occupied")
                    }
                    occupiedPositions[currentFallingRock.position.y + it.y][currentFallingRock.position.x + it.x] = true
                    highestOccupiedPosition = maxOf(highestOccupiedPosition, currentFallingRock.position.y + it.y)
                }
            }
            currentFallingRock = nextRock(currentFallingRock)

            if (stoppedRocks.size % 100 == 0) {
                println(stoppedRocks.size)
            }
        } else {
            currentFallingRock.position = newPosition
        }
    }

    fun next() {
        if (currentFallingRock.position.y + currentFallingRock.shape.height >= occupiedPositions.size) {
            repeat(currentFallingRock.shape.height + 1) {
                occupiedPositions.add(BooleanArray(Day17.CHAMBER_WIDTH))
            }
        }

        applyJetStream()
        applyGravity()
        currentStep++
    }

    fun draw(from: Int? = null, to: Int? = null) {
        val range = (from ?: occupiedPositions.indexOfFirst { it.all { occupied -> !occupied } }) downTo (to ?: 0)
        for(y in range) {
            print("$y: ")
            occupiedPositions[y].forEach { print(if (it) '#' else '.') }
            println()
        }
    }

    fun getHigestOccupiedPosition() =
        occupiedPositions.indexOfFirst { it.all { occupied -> !occupied } }

}

fun main() {
    fun List<String>.parseJetStream() = this[0].toCharArray()

    fun part1(input: List<String>): Int {
        val jetStream = input.parseJetStream()
        val chamber = Chamber(jetStream)
        while (chamber.stoppedRocks.size < Day17.PART1_STOP_THE_ROCK_COUNT) {
            chamber.next()
        }
        chamber.draw(100, 0)
        return chamber.getHigestOccupiedPosition()
    }

    fun part2(input: List<String>): Long {
        return 1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    check(part1(testInput) == Day17.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    check(part2(testInput) == Day17.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day17")
    println(part1(input))
    println(part2(input))
}
