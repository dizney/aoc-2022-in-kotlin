object Day05 {
    const val dayNumber = "05"

    const val EXPECTED_PART1_CHECK_ANSWER = "CMZ"
    const val EXPECTED_PART2_CHECK_ANSWER = "MCD"

    const val STACK_SETUP_ENTRY_WITH_SPACE_SIZE = 4
    const val STACK_SETUP_ENTRY_SIZE = 3
}

fun main() {

    fun List<String>.parseStackSetup(): List<ArrayDeque<Char>> {
        val stacks = mutableListOf<ArrayDeque<Char>>()
        for (lineIdx in this.size - 2 downTo 0) {
            val line = this[lineIdx]
            var stackIndex = 0
            var stackEntryStartPosition = 0
            while (stackEntryStartPosition < line.length) {
                if (stacks.size < stackIndex + 1) {
                    stacks += ArrayDeque<Char>()
                }
                val stackEntry = line.substring(
                    stackEntryStartPosition until (stackEntryStartPosition + Day05.STACK_SETUP_ENTRY_SIZE)
                )
                if (stackEntry.isNotBlank()) {
                    stacks[stackIndex].add(stackEntry[1])
                }
                stackIndex++
                stackEntryStartPosition = stackIndex * Day05.STACK_SETUP_ENTRY_WITH_SPACE_SIZE
            }
        }
        return stacks
    }

    fun parseAndApplyMoves(
        input: List<String>,
        applyMove: (stacks: List<ArrayDeque<Char>>, move: Triple<Int, Int, Int>) -> Unit
    ): String {
        val indexOfEmptyLine = input.indexOf("")
        val stackSetup = input.subList(0, indexOfEmptyLine).parseStackSetup()
        val moves = input.subList(indexOfEmptyLine + 1, input.size)
        val moveTemplate = Regex("move (\\d+) from (\\d+) to (\\d+)")
        moves.forEach { move ->
            moveTemplate.matchEntire(move)?.apply {
                val (amount, from, to) = this.destructured
                applyMove(stackSetup, Triple(amount.toInt(), from.toInt(), to.toInt()))
            }
        }
        return stackSetup.map { stack -> stack.last() }.joinToString(separator = "")
    }

    fun part1(input: List<String>): String {
        return parseAndApplyMoves(input) { stacks, (amount, from, to) ->
            (1..amount).forEach { _ ->
                val toMove = stacks[from - 1].removeLast()
                stacks[to - 1].add(toMove)
            }
        }
    }

    fun part2(input: List<String>): String {
        return parseAndApplyMoves(input) { stacks, (amount, from, to) ->
            val removedChars = (1..amount).map { _ -> stacks[from - 1].removeLast() }
            stacks[to - 1].addAll(removedChars.reversed())
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${Day05.dayNumber}_test")
    check(part1(testInput) == Day05.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    check(part2(testInput) == Day05.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day${Day05.dayNumber}")
    println(part1(input))
    println(part2(input))
}
