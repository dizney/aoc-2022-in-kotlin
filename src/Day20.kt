import java.util.LinkedList

object Day20 {
    const val EXPECTED_PART1_CHECK_ANSWER = 3
    const val EXPECTED_PART2_CHECK_ANSWER = 1623178306L

    val ANSWER_POSITIONS = setOf(1000, 2000, 3000)
    const val DECRYPTION_KEY = 811589153L
}

fun main() {
    fun List<String>.parse() = map(String::toLong).foldIndexed(LinkedList<Pair<Long, Int>>()) { idx, acc, nr ->
        acc.add(nr to idx)
        acc
    }

    fun LinkedList<Pair<Long, Int>>.mixIt(orgList: List<Pair<Long, Int>>) {
        orgList.forEach { nrAndOrgIndex ->
            val nr = nrAndOrgIndex.first
            val currentIndex = indexOf(nrAndOrgIndex)
            val uncappedNewIndex = currentIndex + nr
            val moveToIndex = uncappedNewIndex.mod(lastIndex)
            remove(nrAndOrgIndex)
            add(moveToIndex, nrAndOrgIndex)
        }
    }

    fun part1(input: List<String>): Int {
        val numbers = input.parse()
        val newList = LinkedList(numbers)
        newList.mixIt(numbers)
        println(newList)
        val indexOfZero = newList.indexOf(newList.find { it.first == 0L })
        val result = Day20.ANSWER_POSITIONS.sumOf {
            val indexOfNr = indexOfZero + it
            val wrappedIndex = indexOfNr % newList.size
            newList[wrappedIndex].first
        }
        return result.toInt()
    }

    fun part2(input: List<String>): Long {
        val numbers = input.parse().map { Pair(it.first * Day20.DECRYPTION_KEY, it.second) }
        val newList = LinkedList(numbers)
        repeat(10) { newList.mixIt(numbers) }
        println(newList)
        val indexOfZero = newList.indexOf(newList.find { it.first == 0L })
        val result = Day20.ANSWER_POSITIONS.map(Int::toLong).sumOf {
            val indexOfNr = indexOfZero + it
            val wrappedIndex = indexOfNr % newList.size
            newList[wrappedIndex.toInt()].first
        }
        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day20_test")
    check(part1(testInput) == Day20.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    check(part2(testInput) == Day20.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day20")
    println(part1(input))
    println(part2(input))
}
