object DayXX {
    const val EXPECTED_PART1_CHECK_ANSWER = 1
    const val EXPECTED_PART2_CHECK_ANSWER = 1
}

fun main() {

    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("DayXX_test")
    check(part1(testInput) == DayXX.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    check(part2(testInput) == DayXX.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("DayXX")
    println(part1(input))
    println(part2(input))
}
