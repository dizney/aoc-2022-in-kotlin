object Day03 {
    const val EXPECTED_PART1_CHECK_ANSWER = 157
    const val EXPECTED_PART2_CHECK_ANSWER = 70

    const val ELF_GROUP_SIZE = 3
}

fun main() {

    fun Char.mapToItemValue() = if (this.isUpperCase()) (this.minus('A') + 27) else (this.minus('a') + 1)

    fun part1(input: List<String>): Int {
        return input.sumOf {
            val compartment1 = it
                .take(it.length / 2)
            val compartment2 = it
                .takeLast(it.length / 2)
            val inBothCompartments = compartment1.toSet().intersect(compartment2.toSet())
            val itemValue = if (inBothCompartments.isEmpty()) 0 else inBothCompartments.first().mapToItemValue()
            itemValue
        }
    }

    fun part2(input: List<String>): Int {
        val commonItemsValues = input.chunked(Day03.ELF_GROUP_SIZE) {
            it
                .map { rucksackContent -> rucksackContent.toSet() }
                .foldIndexed(emptySet<Char>()) { idx, acc, chars -> if (idx == 0) chars else acc.intersect(chars) }
                .firstOrNull()
                ?.mapToItemValue()
                ?: 0
        }
        return commonItemsValues.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == Day03.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    check(part2(testInput) == Day03.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
