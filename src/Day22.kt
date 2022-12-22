object Day22 {
    const val EXPECTED_PART1_CHECK_ANSWER = 6032
    const val EXPECTED_PART2_CHECK_ANSWER = 1

    const val RESULT_ROW_MULTIPLIER = 1000
    const val RESULT_COL_MULTIPLIER = 4

    sealed interface Step
    enum class Turn(val chr: Char) : Step {
        Clockwise('R'), Counterclockwise('L');

        companion object {
            fun fromCharacter(chr: Char) = when (chr) {
                'R' -> Clockwise
                'L' -> Counterclockwise
                else -> error("Unknown turn character $chr")
            }
        }
    }

    inline class MoveAmount(val amount: Int) : Step

    enum class Direction {
        WEST, NORTH, EAST, SOUTH;

        fun turn(turn: Turn) = when (this) {
            EAST -> when (turn) {
                Turn.Clockwise -> SOUTH
                Turn.Counterclockwise -> NORTH
            }

            SOUTH -> when (turn) {
                Turn.Clockwise -> WEST
                Turn.Counterclockwise -> EAST
            }

            WEST -> when (turn) {
                Turn.Clockwise -> NORTH
                Turn.Counterclockwise -> SOUTH
            }

            NORTH -> when (turn) {
                Turn.Clockwise -> EAST
                Turn.Counterclockwise -> WEST
            }
        }
    }

    enum class Tile {
        EMPTY, WALL, PATH
    }

    class Map(data: List<String>) {
        private val mapData: List<Array<Tile>>
        var currentLocation: Coordinates
            private set
        var facing: Direction = Direction.EAST
            private set

        init {
            mapData = data.map { row ->
                Array(row.length) { colIdx ->
                    when (row[colIdx]) {
                        ' ' -> Tile.EMPTY
                        '.' -> Tile.PATH
                        else -> Tile.WALL
                    }
                }
            }
            currentLocation = Coordinates(mapData[0].indexOfFirst { it == Tile.PATH }, 0)
        }

        private fun isEmpty(coords: Coordinates) =
            mapData[coords.y].size <= coords.x || mapData[coords.y][coords.x] == Tile.EMPTY

        private fun isEmptyOrEnd(coords: Coordinates) = mapData.size <= coords.y || coords.y < 0 || isEmpty(coords)

        private fun findNextPossibleCoordinateGoingSouth(from: Coordinates): Coordinates? {
            var firstNonEmptyTile = from
            while (mapData[firstNonEmptyTile.y].size <= currentLocation.x || mapData[firstNonEmptyTile.y][currentLocation.x] == Tile.EMPTY) {
                firstNonEmptyTile = firstNonEmptyTile.copy(
                    y = if (firstNonEmptyTile.y + 1 == mapData.size) 0 else firstNonEmptyTile.y + 1
                )
            }
            return if (mapData[firstNonEmptyTile.y][firstNonEmptyTile.x] == Tile.PATH) firstNonEmptyTile else null
        }

        private fun findNextPossibleCoordinateGoingNorth(from: Coordinates): Coordinates? {
            var firstNonEmptyTile = from
            while (mapData[firstNonEmptyTile.y].size <= currentLocation.x || mapData[firstNonEmptyTile.y][currentLocation.x] == Tile.EMPTY) {
                firstNonEmptyTile = firstNonEmptyTile.copy(
                    y = if (firstNonEmptyTile.y - 1 == -1) mapData.size - 1 else firstNonEmptyTile.y - 1
                )
            }
            return if (mapData[firstNonEmptyTile.y][firstNonEmptyTile.x] == Tile.PATH) firstNonEmptyTile else null
        }

        private fun findNextPossibleCoordinateGoingWest(from: Coordinates): Coordinates? {
            var firstNonEmptyTile = from
            while (mapData[firstNonEmptyTile.y][firstNonEmptyTile.x] == Tile.EMPTY) {
                firstNonEmptyTile = firstNonEmptyTile.copy(
                    x = (firstNonEmptyTile.x - 1).let { if (it == -1) mapData[firstNonEmptyTile.y].size - 1 else it }
                )
            }
            return if (mapData[firstNonEmptyTile.y][firstNonEmptyTile.x] == Tile.PATH) firstNonEmptyTile else null
        }

        private fun findNextPossibleCoordinateGoingEast(from: Coordinates): Coordinates? {
            var firstNonEmptyTile = from
            while (mapData[firstNonEmptyTile.y][firstNonEmptyTile.x] == Tile.EMPTY) {
                firstNonEmptyTile = firstNonEmptyTile.copy(
                    x = (firstNonEmptyTile.x + 1).let { if (it == mapData[firstNonEmptyTile.y].size) 0 else it }
                )
            }
            return if (mapData[firstNonEmptyTile.y][firstNonEmptyTile.x] == Tile.PATH) firstNonEmptyTile else null
        }

        fun move(step: Step) {
            when (step) {
                is Turn -> facing = facing.turn(step)
                is MoveAmount -> {
                    when (facing) {
                        Direction.EAST -> {
                            var stepCount = 0
                            while (stepCount < step.amount) {
                                currentLocation =
                                    if (currentLocation.x + 1 < mapData[currentLocation.y].size && mapData[currentLocation.y][currentLocation.x + 1] == Tile.PATH) {
                                        currentLocation.copy(x = currentLocation.x + 1)
                                    } else {
                                        findNextPossibleCoordinateGoingEast(
                                            currentLocation.copy(x = (currentLocation.x + 1).let {
                                                if (it == mapData[currentLocation.y].size) 0 else it
                                            })
                                        ) ?: currentLocation
                                    }
                                stepCount++
                            }
                        }

                        Direction.WEST -> {
                            var stepCount = 0
                            while (stepCount < step.amount) {
                                currentLocation =
                                    if (currentLocation.x - 1 >= 0 && mapData[currentLocation.y][currentLocation.x - 1] == Tile.PATH) {
                                        currentLocation.copy(x = currentLocation.x - 1)
                                    } else {
                                        findNextPossibleCoordinateGoingWest(
                                            currentLocation.copy(x = (currentLocation.x - 1).let {
                                                if (it == -1) mapData[currentLocation.y].size - 1 else it
                                            })
                                        ) ?: currentLocation
                                    }
                                stepCount++
                            }
                        }

                        Direction.SOUTH -> {
                            var stepCount = 0
                            while (stepCount < step.amount) {
                                val nextTile = currentLocation.copy(y = currentLocation.y + 1)
                                if (isEmptyOrEnd(nextTile)) {
                                    currentLocation =
                                        findNextPossibleCoordinateGoingSouth(nextTile.copy(y = if (nextTile.y == mapData.size) 0 else nextTile.y))
                                            ?: currentLocation
                                } else if (mapData[nextTile.y][nextTile.x] == Tile.PATH) {
                                    currentLocation = nextTile
                                }
                                stepCount++
                            }
                        }

                        Direction.NORTH -> {
                            var stepCount = 0
                            while (stepCount < step.amount) {
                                val nextTile = currentLocation.copy(y = currentLocation.y - 1)
                                if (isEmptyOrEnd(nextTile)) {
                                    currentLocation =
                                        findNextPossibleCoordinateGoingNorth(nextTile.copy(y = if (nextTile.y < 0) mapData.size - 1 else nextTile.y))
                                            ?: currentLocation
                                } else if (mapData[nextTile.y][nextTile.x] == Tile.PATH) {
                                    currentLocation = nextTile
                                }
                                stepCount++
                            }
                        }
                    }
                }
            }
        }

        fun draw() {
            for (y in mapData.indices) {
                for (x in 0 until mapData[y].size) {
                    if (currentLocation == Coordinates(x, y)) {
                        when (facing) {
                            Direction.WEST -> print('<')
                            Direction.NORTH -> print('^')
                            Direction.EAST -> print('>')
                            Direction.SOUTH -> print('V')
                        }
                    } else {
                        print(
                            when (mapData[y][x]) {
                                Tile.EMPTY -> ' '
                                Tile.PATH -> '.'
                                Tile.WALL -> '#'
                            }
                        )
                    }
                }
                println()
            }
        }
    }
}

fun main() {
    fun String.parseSteps(): List<Day22.Step> =
        Regex("(\\d+|L|R)").findAll(this)
            .map(MatchResult::destructured)
            .map { (value) ->
                if (value[0].isDigit()) Day22.MoveAmount(value.toInt()) else Day22.Turn.fromCharacter(value[0])
            }
            .toList()

    fun List<String>.parse(): Pair<Day22.Map, List<Day22.Step>> {
        val (mapData, steps) = filter(String::isNotBlank).partition { !it[0].isDigit() }
        return Day22.Map(mapData) to steps[0].parseSteps()
    }

    fun part1(input: List<String>): Int {
        val (map, steps) = input.parse()
        for (step in steps) {
            map.move(step)
//            map.draw()
//            println("========")
        }
        map.draw()
        val result =
            (map.currentLocation.y + 1) * Day22.RESULT_ROW_MULTIPLIER + (map.currentLocation.x + 1) * Day22.RESULT_COL_MULTIPLIER + when (map.facing) {
                Day22.Direction.EAST -> 0
                Day22.Direction.SOUTH -> 1
                Day22.Direction.WEST -> 2
                Day22.Direction.NORTH -> 3
            }
        return result
    }

    fun part2(input: List<String>): Int {
        return 1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day22_test")
    check(part1(testInput) == Day22.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    check(part2(testInput) == Day22.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day22")
    println(part1(input))
    println(part2(input))
}
