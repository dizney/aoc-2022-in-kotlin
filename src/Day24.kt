import kotlin.math.absoluteValue

object Day24 {
    const val EXPECTED_PART1_CHECK_ANSWER = 18
    const val EXPECTED_PART2_CHECK_ANSWER = 54

    data class MapInfo(val width: Int, val height: Int)
}

typealias Minutes = Int

fun main() {
    fun List<String>.parseToCoordinates() =
        flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, position ->
                val coords = Coordinates(x, y)
                when (position) {
                    '<' -> coords to Direction.WEST
                    '>' -> coords to Direction.EAST
                    '^' -> coords to Direction.NORTH
                    'v' -> coords to Direction.SOUTH
                    else -> null
                }
            }
        }

    fun List<Pair<Coordinates, Direction>>.hasBlizzardAt(
        coordinates: Coordinates,
        andTime: Minutes,
        gridWidth: Int,
        gridHeight: Int
    ): Boolean {
        val xDiff = andTime % gridWidth
        val yDiff = andTime % gridHeight

        return filter {
            (it.first.y == coordinates.y && it.second in setOf(Direction.WEST, Direction.EAST)) ||
                    (it.first.x == coordinates.x && it.second in setOf(Direction.NORTH, Direction.SOUTH))
        }
            .map {
                when (it.second) {
                    Direction.WEST -> it.first.copy(x = (it.first.x - xDiff)
                        .let { newX -> if (newX < 1) newX + gridWidth else newX })
                    Direction.EAST -> it.first.copy(x = (it.first.x + xDiff)
                        .let { newX -> if (newX > gridWidth) newX - gridWidth else newX })
                    Direction.NORTH -> it.first.copy(y = (it.first.y - yDiff)
                        .let { newY -> if (newY < 1) newY + gridHeight else newY })
                    Direction.SOUTH -> it.first.copy(y = (it.first.y + yDiff)
                        .let { newY -> if (newY > gridHeight) newY - gridHeight else newY })
                } to it.second
            }
            .also {
//                println("Time $andTime, coords: $coordinates, blizzards: $it")
            }
            .any { (blizzardCoordinates, _) -> blizzardCoordinates == coordinates }
    }

    fun Coordinates.wrap(minX: Int, maxX: Int, minY: Int, maxY: Int) =
        if (y < minY) Coordinates(x, maxY)
        else if (y > maxY) Coordinates(x, minY)
        else if (x < minX) Coordinates(maxX, y)
        else if (x > maxX) Coordinates(minX, y)
        else this

    fun Set<Coordinates>.excludeOutsideBoundaries(
        minX: Int,
        maxX: Int,
        minY: Int,
        maxY: Int,
        allowed: Set<Coordinates>
    ) =
        filter { coords ->
            (coords.y in minY..maxY && coords.x in minX..maxX) || coords in allowed
        }.toSet()

    fun findExitStepCount(
        blizzards: List<Pair<Coordinates, Direction>>,
        mapInfo: Day24.MapInfo,
        startPosition: Pair<Coordinates, Minutes>,
        exitPosition: Coordinates,
    ): Int {
        val queue = ArrayDeque(listOf(startPosition))
        val visited = mutableSetOf<Pair<Coordinates, Minutes>>()
        val extraAllowedPositions = setOf(startPosition.first, exitPosition)
        while (queue.isNotEmpty()) {
            val (currentExpPosition, atTime) = queue.removeFirst()
            val minX = 1
            val maxX = mapInfo.width - 2
            val minY = 1
            val maxY = mapInfo.height - 2
            currentExpPosition.adjacentPositions(false)
                .excludeOutsideBoundaries(minX, maxX, minY, maxY, extraAllowedPositions)
                .filterNot { potentialExpPosition ->
                    blizzards.hasBlizzardAt(
                        potentialExpPosition,
                        atTime + 1,
                        mapInfo.width - 2,
                        mapInfo.height - 2
                    )
                }
                .sortedBy {
                    (exitPosition.x - it.x).absoluteValue + (exitPosition.y - it.y).absoluteValue
                }
                .ifEmpty { listOf(currentExpPosition) }
                .forEach { newNextExpPosition ->
                    if (atTime in 291..294) {
                        println("atTime $atTime, queue $queue, visited size: ${visited.size}")
                    }
                    if (!visited.contains(newNextExpPosition to atTime + 1)) {
                        if (newNextExpPosition == exitPosition) {
                            return atTime + 1
                        }

                        visited.add(newNextExpPosition to atTime + 1)
                        queue.add(newNextExpPosition to atTime + 1)
                    }
                }
        }
        return -1
    }

    fun part1(input: List<String>, exitCoordinates: Coordinates): Int {
        val blizzards = input.parseToCoordinates()
        val mapInfo = Day24.MapInfo(
            input[0].length, input.size
        )
        val result = findExitStepCount(
            blizzards, mapInfo, Coordinates(1, 0) to 0, exitCoordinates
        )
        return result
    }

    fun part2(input: List<String>): Int {
        return 1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day24_test")
    val part1TestResult = part1(
        testInput,
        Coordinates(testInput[0].length - 2, testInput.size - 1)
    )
    println("Part1 test: $part1TestResult")
    check(
        part1TestResult == Day24.EXPECTED_PART1_CHECK_ANSWER
    ) { "Part 1 failed" }
//    check(part2(testInput) == Day24.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day24")
    println("part 1 result: ${part1(input, Coordinates(input[0].length - 2, input.size - 1))}")
    println(part2(input))
}
