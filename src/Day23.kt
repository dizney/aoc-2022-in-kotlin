import Day23.draw
import Day23.proposeMoves

object Day23 {
    const val EXPECTED_PART1_CHECK_ANSWER = 110
    const val EXPECTED_PART2_CHECK_ANSWER = 20

    sealed interface GridCell
    object Empty : GridCell
    object Elf : GridCell

    class Grid {
        private val rows = mutableListOf<List<GridCell>>()
        var width: Int = 0
            private set
        var height: Int = 0
            private set

        fun addRow(cells: List<Day23.GridCell>) {
            rows += cells
            width = rows.maxOf { it.size }
            height = rows.size
        }

        fun doStep() {

        }
    }

    fun Array<Coordinates>.draw() {
        val fromX = minOf { it.x }
        val toX = maxOf { it.x }
        val fromY = minOf { it.y }
        val toY = maxOf { it.y }
        for (y in fromY..toY) {
            for (x in fromX..toX) {
                if (Coordinates(x, y) in this) {
                    print('#')
                } else {
                    print('.')
                }
            }
            println()
        }
    }

    fun Array<Coordinates>.proposeMoves(
        directions: Array<Direction>,
        directionStartIdx: Int
    ): List<Pair<Coordinates, Coordinates>> = mapNotNull { elf ->
        if (intersect(elf.adjacentPositions().toSet()).isNotEmpty()) {
            var proposedMove: Pair<Coordinates, Coordinates>? = null
            var directionIdx = directionStartIdx
            var directionsDoneCount = 0
            while (proposedMove == null && directionIdx < directions.size && directionsDoneCount++ <= directions.size) {
                if (intersect(elf.adjacentPositions(directions[directionIdx])).isEmpty()) {
                    proposedMove = Pair(elf, elf + directions[directionIdx].move)
                }
                directionIdx = (directionIdx + 1) % directions.size
            }
            proposedMove
        } else {
            null
        }
    }
}

fun main() {

    fun List<String>.parseElfsMap(): Array<Coordinates> = flatMapIndexed { row, line ->
            line.mapIndexedNotNull { col, cell ->
                when (cell) {
                    '.' -> null
                    '#' -> Coordinates(col, row)
                    else -> error("Unknown cell content $cell")
                }
            }
        }.toTypedArray()

    fun part1(input: List<String>, doDraw: Boolean): Int {
        val elfs = input.parseElfsMap()

        if (doDraw) {
            elfs.draw()
            println()
        }

        val directions = arrayOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)
        var directionStartIdx = 0
        repeat(10) {
            val proposedMoves = elfs.proposeMoves(directions, directionStartIdx)

            val allNewPositions = proposedMoves.map { it.second }
            proposedMoves.forEach { (currentPosition, proposedPosition) ->
                if (allNewPositions.count { it == proposedPosition } == 1) {
                    elfs[elfs.indexOf(currentPosition)] = proposedPosition
                }
            }

            directionStartIdx = (directionStartIdx + 1) % directions.size
            if (doDraw) {
                elfs.draw()
                println()
            }
        }
        val fromX = elfs.minOf { it.x }
        val toX = elfs.maxOf { it.x }
        val fromY = elfs.minOf { it.y }
        val toY = elfs.maxOf { it.y }
        return (fromY..toY).sumOf { y -> (fromX..toX).count { x -> !elfs.contains(Coordinates(x, y)) } }
    }

    fun part2(input: List<String>): Int {
        val elfs = input.parseElfsMap()

        val directions = arrayOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)
        var directionStartIdx = 0
        var round = 0
        var atLeastOneElfMoved: Boolean
        do {
            atLeastOneElfMoved = false
            val proposedMoves = elfs.proposeMoves(directions, directionStartIdx)

            val allNewPositions = proposedMoves.map { it.second }
            proposedMoves.forEach { (currentPosition, proposedPosition) ->
                if (allNewPositions.count { it == proposedPosition } == 1) {
                    elfs[elfs.indexOf(currentPosition)] = proposedPosition
                    atLeastOneElfMoved = true
                }
            }

            directionStartIdx = (directionStartIdx + 1) % directions.size
            round++
        } while (atLeastOneElfMoved)
        return round
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day23_test")
    check(part1(testInput, true) == Day23.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    check(part2(testInput) == Day23.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day23")
    println(part1(input, false))
    println(part2(input))
}
