object Day21 {
    const val EXPECTED_PART1_CHECK_ANSWER = 152L
    const val EXPECTED_PART2_CHECK_ANSWER = 1

    val JOB_MATH_PATTERN = Regex("(\\w+) ([+\\-*/]) (\\w+)")
    const val ROOT_MONKEY_NAME = "root"
}

sealed interface Job {
    val name: String
}

data class NumberJob(override val name: String, val number: Long) : Job
data class MathJob(override val name: String, val waitFor: List<String>, val operator: Char) : Job {
    fun calc(num1: Long, num2: Long): Long = when (operator) {
        '+' -> num1 + num2
        '-' -> num1 - num2
        '*' -> num1 * num2
        '/' -> num1 / num2
        else -> error("Unknown operator $operator")
    }

}

fun main() {

    fun List<String>.parse() =
        map { line ->
            val (name, job) = line.split(':').map(String::trim)
            when {
                job.all { it.isDigit() } -> NumberJob(name, job.toLong())
                else -> {
                    val (depOne, operator, depTwo) = Day21.JOB_MATH_PATTERN
                        .matchEntire(job)?.destructured ?: error("Should not happen")
                    MathJob(name, listOf(depOne, depTwo), operator[0])
                }
            }
        }.groupBy { it.name }.mapValues { it.value.first() }

    fun getMonkeyResult(monkeys: Map<String, Job>, monkeyName: String): Long {
        return when (val monkey = monkeys[monkeyName]!!) {
            is NumberJob -> monkey.number
            is MathJob -> monkey.calc(
                getMonkeyResult(monkeys, monkey.waitFor[0]),
                getMonkeyResult(monkeys, monkey.waitFor[1])
            )
        }
    }

    fun part1(input: List<String>): Long {
        val monkeys = input.parse()
        val result = getMonkeyResult(monkeys, Day21.ROOT_MONKEY_NAME)
        return result
    }

    fun part2(input: List<String>): Int {
        return 1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    check(part1(testInput) == Day21.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    check(part2(testInput) == Day21.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day21")
    println(part1(input))
    println(part2(input))
}
