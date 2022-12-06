object Day06 {
    const val EXPECTED_PART1_CHECK_ANSWER = 7
    const val EXPECTED_PART2_CHECK_ANSWER = 19

    const val MARKER_PACKET_LENGTH = 4
    const val MARKER_MESSAGE_LENGTH = 14
}

fun main() {

    fun findLengthUntilMarker(data: String, markerLength: Int): Int {
        var pointerInData = 0
        var potentialMarker = ""
        do {
            when (val indexOfDuplicate = potentialMarker.indexOf(data[pointerInData])) {
                -1 -> potentialMarker += data[pointerInData]
                else -> potentialMarker = potentialMarker.drop(indexOfDuplicate + 1) + data[pointerInData]
            }
            pointerInData++
        } while (potentialMarker.length < markerLength && pointerInData < data.length)
        return pointerInData
    }

    fun part1(input: List<String>): Int {
        return findLengthUntilMarker(input.first(), Day06.MARKER_PACKET_LENGTH)
    }

    fun part2(input: List<String>): Int {
        return findLengthUntilMarker(input.first(), Day06.MARKER_MESSAGE_LENGTH)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == Day06.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    check(part2(testInput) == Day06.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
