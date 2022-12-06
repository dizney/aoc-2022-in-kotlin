import kotlin.system.measureNanoTime

object Day06 {
    const val EXPECTED_PART1_CHECK_ANSWER = 7
    const val EXPECTED_PART2_CHECK_ANSWER = 19

    const val MARKER_PACKET_LENGTH = 4
    const val MARKER_MESSAGE_LENGTH = 14
}

fun main() {

    fun String.findLengthUntilMarker(markerLength: Int): Int {
        var pointerInData = 0
        var potentialMarker = ""
        do {
            when (val indexOfDuplicate = potentialMarker.indexOf(this[pointerInData])) {
                -1 -> potentialMarker += this[pointerInData]
                else -> potentialMarker = potentialMarker.drop(indexOfDuplicate + 1) + this[pointerInData]
            }
            pointerInData++
        } while (potentialMarker.length < markerLength && pointerInData < this.length)
        return pointerInData
    }

    fun String.findLengthUntilMarkerWindowed(markerLength: Int) =
        windowedSequence(markerLength) { it.toSet().size }.indexOfFirst { it == markerLength } + markerLength

    fun part1(input: List<String>): Int {
        val windowedDuration = measureNanoTime {
            input.first().findLengthUntilMarkerWindowed(Day06.MARKER_PACKET_LENGTH)
        }
        val nonWindowedDuration = measureNanoTime {
            input.first().findLengthUntilMarker(Day06.MARKER_PACKET_LENGTH)
        }

        println("Windowed: $windowedDuration, Non windowed: $nonWindowedDuration")

        return input.first().findLengthUntilMarkerWindowed(Day06.MARKER_PACKET_LENGTH)
    }

    fun part2(input: List<String>): Int {
        val windowedDuration = measureNanoTime {
            input.first().findLengthUntilMarkerWindowed(Day06.MARKER_MESSAGE_LENGTH)
        }
        val nonWindowedDuration = measureNanoTime {
            input.first().findLengthUntilMarker(Day06.MARKER_MESSAGE_LENGTH)
        }

        println("Windowed: $windowedDuration, Non windowed: $nonWindowedDuration")

        return input.first().findLengthUntilMarkerWindowed(Day06.MARKER_MESSAGE_LENGTH)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == Day06.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    check(part2(testInput) == Day06.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
