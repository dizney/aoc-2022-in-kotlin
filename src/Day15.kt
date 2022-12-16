import kotlin.math.abs

object Day15 {
    const val EXPECTED_PART1_CHECK_ANSWER = 26
    const val EXPECTED_PART2_CHECK_ANSWER = 56000011L

    const val PART1_CHECK_ROW = 10
    const val PART1_ROW = 2_000_000
    const val PART2_CHECK_MAX = 20
    const val PART2_MAX = 4_000_000
    const val PART2_MULTIPLY_VALUE = 4_000_000
}

fun main() {

    fun String.parseSensorAndBeaconCoordinatess(): Pair<Coordinates, Coordinates> {
        val (sensorX, sensorY, beaconX, beaconY) = Regex("Sensor at x=([-\\d]+), y=([-\\d]+): closest beacon is at x=([-\\d]+), y=([-\\d]+)")
            .matchEntire(this)
            ?.destructured ?: error("Should match regex")
        return Coordinates(sensorX.toInt(), sensorY.toInt()) to Coordinates(beaconX.toInt(), beaconY.toInt())
    }

    data class RowInfo(val positionsCovered: Int, val notCovered: Coordinates?)

    fun rowInfo(
        row: Int,
        gridMin: Int,
        gridMax: Int,
        sensorAndBeaconCoordinates: List<Pair<Coordinates, Coordinates>>
    ): RowInfo {
        val coveredRangesOnRow = mutableListOf<IntRange>()
        for (sensorAndBeaconCoordinate in sensorAndBeaconCoordinates) {
            val sensorLocation = sensorAndBeaconCoordinate.first
            val beaconLocation = sensorAndBeaconCoordinate.second
            val distance = abs(sensorLocation.x - beaconLocation.x) + abs(sensorLocation.y - beaconLocation.y)
            val positionsCoveredAtSensorRow = distance * 2 + 1
            val yDistanceToCheckRow = abs(sensorLocation.y - row)
            val positionsCoveredAtCheckRow = positionsCoveredAtSensorRow - yDistanceToCheckRow * 2
            if (positionsCoveredAtCheckRow > 0) {
                val xFrom = maxOf(gridMin, sensorLocation.x - (positionsCoveredAtCheckRow / 2))
                val xTo = minOf(gridMax, sensorLocation.x + (positionsCoveredAtCheckRow / 2))
                val range = xFrom..xTo

                coveredRangesOnRow.add(range)
                val overlappingRanges =
                    coveredRangesOnRow
                        .filter { range.first in it || range.last in it || it.first in range || it.last in range }
                if (overlappingRanges.isNotEmpty()) {
                    val mergedRange = overlappingRanges.reduce { acc, intRange ->
                        minOf(acc.first, intRange.first)..maxOf(
                            acc.last,
                            intRange.last
                        )
                    }
                    coveredRangesOnRow.add(mergedRange)
                    overlappingRanges.forEach { coveredRangesOnRow.remove(it) }
                }
            }
        }

        val coveredCount = coveredRangesOnRow.sumOf { it.count() }

        val notCovered = if (gridMax - gridMin - coveredCount >= 0) {
            // find in ranges the open position..
            if (coveredRangesOnRow.size == 1) {
                if (coveredRangesOnRow[0].first > gridMin) {
                    gridMin
                } else {
                    gridMax
                }
            } else {
                coveredRangesOnRow
                    .sortedBy { it.first }
                    .windowed(2)
                    .first { it.first().last + 1 < it[1].first }
                    .let { it.first().last + 1 }
            }
        } else null

        return RowInfo(coveredCount, if (notCovered != null) Coordinates(notCovered, row) else null)
    }


    fun part1(input: List<String>, checkRow: Int): Int {
        val sensorAndBeaconCoordinates = input.map(String::parseSensorAndBeaconCoordinatess)
        val (min, max) = sensorAndBeaconCoordinates.flatMap { listOf(it.first, it.second) }.findMinAndMax()
        val beaconCoordinates = sensorAndBeaconCoordinates.map { it.second }
        val beaconsOnCheckRow = beaconCoordinates.toSet().filter { it.y == checkRow }.size

        return rowInfo(checkRow, min.x, max.x, sensorAndBeaconCoordinates).positionsCovered - beaconsOnCheckRow
    }

    fun part2(input: List<String>, max: Int): Long {
        val sensorAndBeaconCoordinates = input.map(String::parseSensorAndBeaconCoordinatess)
        var notCoveredRow: RowInfo? = null
        for (row in max downTo 0) {
            val rowInfo = rowInfo(row, 0, max, sensorAndBeaconCoordinates)
            println("Row $row")
            if (rowInfo.notCovered != null) {
                notCoveredRow = rowInfo
                break
            }
        }
        if (notCoveredRow != null) {
            return notCoveredRow.notCovered!!.x.toLong() * Day15.PART2_MULTIPLY_VALUE + notCoveredRow.notCovered!!.y
        }
        return -1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    println("Checking")
    check(part1(testInput, Day15.PART1_CHECK_ROW) == Day15.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    check(part2(testInput, Day15.PART2_CHECK_MAX) == Day15.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    println("On real data")

    val input = readInput("Day15")
    println(part1(input, Day15.PART1_ROW))
    println(part2(input, Day15.PART2_MAX))
}
