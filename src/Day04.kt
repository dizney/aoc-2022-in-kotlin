object Day04 {
    const val EXPECTED_PART1_CHECK_ANSWER = 2
    const val EXPECTED_PART2_CHECK_ANSWER = 4
}

fun main() {

    fun String.parseToIntRangePerElf(): Pair<IntRange, IntRange> =
        this
            .split(",")
            .map { it.split("-") }
            .map { (it[0].toInt())..(it[1].toInt()) }
            .let { it[0] to it[1] }

    fun part1(input: List<String>): Int =
        input
            .map { it.parseToIntRangePerElf() }
            .count { it.first.minus(it.second).isEmpty() || it.second.minus(it.first).isEmpty() }

    fun part2(input: List<String>): Int =
        input
            .map { it.parseToIntRangePerElf() }
            .count { (firstElfSections, secondElfSections) ->
                firstElfSections.any { secondElfSections.contains(it) } || firstElfSections.any {
                    secondElfSections.contains(
                        it
                    )
                }
            }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == Day04.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    check(part2(testInput) == Day04.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
