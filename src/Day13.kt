object Day13 {
    const val EXPECTED_PART1_CHECK_ANSWER = 13
    const val EXPECTED_PART2_CHECK_ANSWER = 140

    const val PACKET_COMBOS_NR_OF_LINES = 3
    val NONE_NUMBER_CHARS = listOf('[', ']', ',')
    const val DIVIDER_PACKET_ONE = 2
    const val DIVIDER_PACKET_TWO = 6
    val DIVIDER_PACKETS: List<List<Any>> = listOf(
        listOf(listOf(DIVIDER_PACKET_ONE)),
        listOf(listOf(DIVIDER_PACKET_TWO))
    )
}

fun main() {

    fun String.parse(): List<Any> {
        val stack = ArrayDeque<MutableList<Any>>()
        var idx = 0
        do {
            when (this[idx]) {
                '[' -> {
                    stack.addFirst(mutableListOf())
                    idx++
                }

                ']' -> {
                    if (stack.size > 1) {
                        val innerList = stack.removeFirst()
                        stack.first().add(innerList)
                    }
                    idx++
                }

                in '0'..'9' -> {
                    var nrStr: String = this[idx].toString()
                    while (this[++idx] !in Day13.NONE_NUMBER_CHARS) {
                        nrStr += this[idx]
                    }
                    stack.first().add(nrStr.toInt())
                }

                else -> idx++
            }
        } while (idx < this.length)
        check(stack.size == 1) { "Stack should only have root left" }
        return stack.first()
    }

    operator fun List<Any>.compareTo(other: List<Any>): Int {
        for (leftIdx in this.indices) {
            val firstValue = this[leftIdx]
            if (leftIdx < other.size) {
                val secondValue = other[leftIdx]

                when {
                    firstValue is Int && secondValue is Int -> {
                        if (firstValue != secondValue) return firstValue.compareTo(secondValue)
                    }

                    else -> {
                        val firstValueList = if (firstValue is Int) listOf(firstValue) else firstValue as List<Any>
                        val secondValueList = if (secondValue is Int) listOf(secondValue) else secondValue as List<Any>
                        if (firstValueList != secondValueList) {
                            return firstValueList.compareTo(secondValueList)
                        }
                    }
                }
            } else {
                return 1
            }
        }
        return -1
    }

    fun part1(input: List<String>): Int {
        val pairs = input.chunked(Day13.PACKET_COMBOS_NR_OF_LINES).map { it[0].parse() to it[1].parse() }
        val compareResults = pairs.map { it.first.compareTo(it.second) }
        return compareResults.foldIndexed(0) { index, acc, compareResult ->
            if (compareResult < 0) acc + index + 1 else acc
        }
    }

    fun part2(input: List<String>): Int {
        val pairs = input.filter { it.isNotBlank() }.map { it.parse() } + Day13.DIVIDER_PACKETS

        val sorted = pairs.sortedWith { o1, o2 -> o1!!.compareTo(o2!!) }
        val result = Day13.DIVIDER_PACKETS.map {
            sorted.indexOf(it) + 1
        }.reduce(Int::times)
        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == Day13.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    check(part2(testInput) == Day13.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}
