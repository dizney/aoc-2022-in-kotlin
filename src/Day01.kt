const val PART1_EXPECTED_HIGHEST_CALORIES = 24000
const val PART2_EXPECTED_HIGHEST_3_CALORIES_SUM = 45000
const val PART2_TOP_ELF_COUNT = 3

fun main() {

    fun elfTotals(input: List<String>, idx: Int = 0, elfAmount: Int = 0, totals: List<Int> = emptyList()): List<Int> {
        if (idx >= input.size) return totals + elfAmount
        return elfTotals(
            input,
            idx + 1,
            if (input[idx].isBlank()) 0 else elfAmount + input[idx].toInt(),
            if (input[idx].isBlank()) totals + elfAmount else totals
        )
    }

    fun part1(input: List<String>): Int {
        return elfTotals(input).maxOf { it }
    }

    fun part2(input: List<String>): Int {
        val elfTotals = elfTotals(input)
        return elfTotals.sortedDescending().take(PART2_TOP_ELF_COUNT).sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == PART1_EXPECTED_HIGHEST_CALORIES)
    check(part2(testInput) == PART2_EXPECTED_HIGHEST_3_CALORIES_SUM)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))

//    fun part1(input: List<String>): Int {
//        var currentMax = 0
//        var elfTotal = 0
//        input.forEach {
//            if (it.isBlank()) {
//                if (elfTotal > currentMax) {
//                    currentMax = elfTotal
//                }
//                elfTotal = 0
//            } else {
//                elfTotal += it.toInt()
//            }
//        }
//        return currentMax
//    }
//
}
