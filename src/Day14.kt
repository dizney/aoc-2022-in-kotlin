object Day14 {
    const val EXPECTED_PART1_CHECK_ANSWER = 24
    const val EXPECTED_PART2_CHECK_ANSWER = 93

    const val PATH_SEPARATOR = "->"
    private const val SAND_ENTRY_X = 500
    private const val SAND_ENTRY_Y = 0
    val SAND_ENTRY_COORDINATES = Coordinates(SAND_ENTRY_X, SAND_ENTRY_Y)
    const val PART2_GRID_MIN_X = 330
    const val PART2_GRID_MAX_X = 670
}

data class Coordinates(val x: Int, val y: Int)

sealed interface Tile
object Air : Tile
object Rock : Tile
object SandInRest : Tile

class Grid(
    private val topLeft: Coordinates,
    private val bottomRight: Coordinates,
    linesCoordinates: List<List<Coordinates>>
) {
    private val gridWidth = (bottomRight.x - topLeft.x) + 1
    private val gridHeight = (bottomRight.y - topLeft.y) + 1
    private val tiles = Array(gridWidth) { Array<Tile>(gridHeight) { Air } }
    private var movingSand = Day14.SAND_ENTRY_COORDINATES

    init {
        linesCoordinates.forEach { lineCoordinates ->
            lineCoordinates.windowed(2).forEach { fromTo ->
                val from = fromTo[0]
                val to = fromTo[1]
                if (from.x == to.x) {
                    for (y in (minOf(from.y, to.y))..(maxOf(from.y, to.y))) {
                        setTileAt(from.x, y, Rock)
                    }
                } else {
                    for (x in (minOf(from.x, to.x))..(maxOf(from.x, to.x))) {
                        setTileAt(x, from.y, Rock)
                    }
                }
            }
        }
    }

    private fun tileAt(x: Int, y: Int) = tiles[x - topLeft.x][y - topLeft.y]

    private fun setTileAt(x: Int, y: Int, tile: Tile) {
        tiles[x - topLeft.x][y - topLeft.y] = tile
    }

    fun tick(): Boolean {
        var sandFallOffOrFull = false
        if (movingSand.y == bottomRight.y) {
            // sand moves off grid
            movingSand = Day14.SAND_ENTRY_COORDINATES
            sandFallOffOrFull = true
        } else {
            movingSand = when (tileAt(movingSand.x, movingSand.y + 1)) {
                is Air -> Coordinates(movingSand.x, movingSand.y + 1)
                is Rock, SandInRest -> {
                    if (movingSand.x > topLeft.x) {
                        when (tileAt(movingSand.x - 1, movingSand.y + 1)) {
                            is Air -> Coordinates(movingSand.x - 1, movingSand.y + 1)
                            is Rock, SandInRest -> {
                                if (movingSand.x < bottomRight.x) {
                                    when (tileAt(movingSand.x + 1, movingSand.y + 1)) {
                                        is Air -> Coordinates(movingSand.x + 1, movingSand.y + 1)
                                        is Rock, SandInRest -> {
                                            setTileAt(movingSand.x, movingSand.y, SandInRest)
                                            Day14.SAND_ENTRY_COORDINATES
                                        }
                                    }
                                } else {
                                    setTileAt(movingSand.x, movingSand.y, SandInRest)
                                    Day14.SAND_ENTRY_COORDINATES
                                }
                            }
                        }
                    } else {
                        sandFallOffOrFull = true
                        Day14.SAND_ENTRY_COORDINATES
                    }
                }
            }
        }
        if (tileAt(Day14.SAND_ENTRY_COORDINATES.x, Day14.SAND_ENTRY_COORDINATES.y) is SandInRest) {
            sandFallOffOrFull = true
        }
        return sandFallOffOrFull
    }

    fun countSandsInRest(): Int = tiles.sumOf { it.count { tile -> tile is SandInRest } }

    fun draw() {
        for (y in 0 until gridHeight) {
            for (x in 0 until gridWidth) {
                print(
                    when (tiles[x][y]) {
                        is Air -> '.'
                        is Rock -> '#'
                        is SandInRest -> 'o'
                    }
                )
            }
            println()
        }
    }
}

fun main() {

    fun String.parseCoordinates() =
        split(Regex(" ${Day14.PATH_SEPARATOR} "))
            .map { it.split(',') }
            .map { Coordinates(it.first().toInt(), it[1].toInt()) }

    fun List<Coordinates>.findMinAndMax(): Pair<Coordinates, Coordinates> =
        fold(Coordinates(Int.MAX_VALUE, Int.MAX_VALUE) to Coordinates(0, 0)) { (min, max), (x, y) ->
            Coordinates(
                minOf(min.x, x),
                minOf(min.y, y)
            ) to Coordinates(
                maxOf(max.x, x),
                maxOf(max.y, y)
            )
        }

    fun part1(input: List<String>): Int {
        val linesCoordinates = input.map(String::parseCoordinates)
        val (min, max) = (linesCoordinates.map(List<Coordinates>::findMinAndMax) + (Day14.SAND_ENTRY_COORDINATES to Day14.SAND_ENTRY_COORDINATES))
            .reduce { (currentMin, currentMax), (min, max) ->
                Coordinates(
                    minOf(currentMin.x, min.x),
                    minOf(currentMin.y, min.y)
                ) to Coordinates(maxOf(currentMax.x, max.x), maxOf(currentMax.y, max.y))
            }
        val grid = Grid(Coordinates(min.x, min.y), Coordinates(max.x, max.y), linesCoordinates)
        var sandStartsToFallOff: Boolean
        do {
            sandStartsToFallOff = grid.tick()
        } while (!sandStartsToFallOff)
        return grid.countSandsInRest()
    }

    fun part2(input: List<String>): Int {
        val linesCoordinates = input.map(String::parseCoordinates)
        val (_, max) = (linesCoordinates.map(List<Coordinates>::findMinAndMax) + (Day14.SAND_ENTRY_COORDINATES to Day14.SAND_ENTRY_COORDINATES))
            .reduce { (currentMin, currentMax), (min, max) ->
                Coordinates(
                    minOf(currentMin.x, min.x),
                    minOf(currentMin.y, min.y)
                ) to Coordinates(maxOf(currentMax.x, max.x), maxOf(currentMax.y, max.y))
            }
        val grid = Grid(
            Coordinates(Day14.PART2_GRID_MIN_X, 0),
            Coordinates(Day14.PART2_GRID_MAX_X, max.y + 2),
            linesCoordinates + listOf(
                listOf(
                    Coordinates(Day14.PART2_GRID_MIN_X, max.y + 2),
                    Coordinates(Day14.PART2_GRID_MAX_X, max.y + 2)
                )
            )
        )
        var sandStartsToFallOff: Boolean
        do {
            sandStartsToFallOff = grid.tick()
        } while (!sandStartsToFallOff)
        grid.draw()
        return grid.countSandsInRest()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == Day14.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    check(part2(testInput) == Day14.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}
