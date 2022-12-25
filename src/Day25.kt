import kotlin.math.pow

object Day25 {
    const val EXPECTED_PART1_CHECK_ANSWER = "2=-1=0"
    const val EXPECTED_PART2_CHECK_ANSWER = 1

}

fun main() {

    fun fivesValueForDigit(char: Char) = when (char) {
        '2' -> 2
        '1' -> 1
        '0' -> 0
        '-' -> -1
        '=' -> -2
        else -> error("Unknown char $char")
    }

    fun digitForFivesValue(value: Long) = when (value) {
        0L -> '0'
        1L -> '1'
        2L -> '2'
        3L -> '='
        4L -> '-'
        else -> error("Unsupported value $value")
    }

    fun convertFivePowerNumberToDecimalNumber(fivePowerNumber: String): Long {
        return fivePowerNumber.reversed().foldIndexed(0L) { idx, total, curFive ->
            total + fivesValueForDigit(curFive) * (5.toDouble().pow(idx).toLong())
        }
    }

    fun convertDecimalNumberToFivePowerNumber(decimalNumber: Long): String {
        if (decimalNumber == 0L) return "0"
        return generateSequence(decimalNumber to "") { (rest, totalNumber) ->
            val low = rest % 5
            (rest + 2) / 5 to digitForFivesValue(low) + totalNumber
        }.first { it.first <= 0L }.second
    }

    fun part1(input: List<String>): String {
        val sumInDecimal = input.sumOf { convertFivePowerNumberToDecimalNumber(it) }
        val snafuValue = convertDecimalNumberToFivePowerNumber(sumInDecimal)
        return snafuValue
    }

    fun part2(input: List<String>): Int {
        return 1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day25_test")
    val input = readInput("Day25")

    val part1TestResult = part1(testInput)
    println("Part 1 test: $part1TestResult")
    check(part1TestResult == Day25.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }

    println(part1(input))

    val part2TestResult = part2(testInput)
    println("Part 2 test: $part2TestResult")
    check(part2TestResult == Day25.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    println(part2(input))
}
