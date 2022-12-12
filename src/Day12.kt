import java.util.LinkedList

object Day12 {
    const val EXPECTED_PART1_CHECK_ANSWER = 31
    const val EXPECTED_PART2_CHECK_ANSWER = 29

    const val CHAR_START = 'S'
    const val CHAR_DESTINATION = 'E'
    const val CHAR_LOWEST = 'a'
}

fun main() {

    fun List<String>.parseData(): Array<CharArray> =
        map { it.toCharArray() }.toTypedArray()

    fun Array<CharArray>.findLocationOfChar(char: Char) =
        this.indexOfFirst { it.contains(char) }.let { vertIndex -> Location(this[vertIndex].indexOf(char), vertIndex) }

    fun Array<CharArray>.findLocationsOfChar(char: Char): List<Location> {
        val locationsOfChar = mutableListOf<Location>()
        this.forEachIndexed { yIndex, listOfChars ->
            listOfChars.forEachIndexed { xIndex, possibleChar ->
                if (char == possibleChar) {
                    locationsOfChar.add(Location(xIndex, yIndex))
                }
            }
        }
        return locationsOfChar
    }

    fun Array<CharArray>.getValueAt(location: Location) = this[location.y][location.x]

    fun Array<CharArray>.width() = this[0].size
    fun Array<CharArray>.height() = this.size
    fun Char.mapToHeightValue() = when (this) {
        Day12.CHAR_START -> 'a'
        Day12.CHAR_DESTINATION -> 'z'
        else -> this
    }

    fun findShortestPath(grid: Array<CharArray>, start: Location): Int {
        val destination = grid.findLocationOfChar('E')
        val queue = LinkedList<Pair<Location, Int>>()
        val visited = mutableSetOf<Location>()

        queue.add(start to 0)
        visited.add(start)
        do {
            val locationAndDistance = queue.remove()
            val locationHeight = grid.getValueAt(locationAndDistance.first).mapToHeightValue()

            if (locationAndDistance.first == destination) {
                return locationAndDistance.second
            }

            val adjCells = listOf(
                Location(locationAndDistance.first.x - 1, locationAndDistance.first.y),
                Location(locationAndDistance.first.x + 1, locationAndDistance.first.y),
                Location(locationAndDistance.first.x, locationAndDistance.first.y - 1),
                Location(locationAndDistance.first.x, locationAndDistance.first.y + 1),
            ).filter { it.x >= 0 && it.y >= 0 && it.x < grid.width() && it.y < grid.height() }

            for (adjLocation in adjCells) {
                val height = grid.getValueAt(adjLocation).mapToHeightValue()
                if ((height - locationHeight <= 1) && !visited.contains(
                        adjLocation
                    )
                ) {
                    queue.add(adjLocation to locationAndDistance.second + 1)
                    visited.add(adjLocation)
                }
            }
        } while (queue.isNotEmpty())

        return -1
    }

    fun part1(input: List<String>): Int {
        val grid = input.parseData()
        return findShortestPath(grid, grid.findLocationOfChar(Day12.CHAR_START))
    }

    fun part2(input: List<String>): Int {
        val grid = input.parseData()
        val locationsOfLowest = grid.findLocationsOfChar(Day12.CHAR_LOWEST) + grid.findLocationOfChar(Day12.CHAR_START)
        return locationsOfLowest.map { findShortestPath(grid, it) }.filter { it != -1 }.also { println(it) }.min()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == Day12.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    check(part2(testInput) == Day12.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}
