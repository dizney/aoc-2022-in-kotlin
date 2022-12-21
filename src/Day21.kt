object Day21 {
    const val EXPECTED_PART1_CHECK_ANSWER = 152L
    const val EXPECTED_PART2_CHECK_ANSWER = 301L

    val JOB_MATH_PATTERN = Regex("(\\w+) ([+\\-*/]) (\\w+)")
    const val ROOT_MONKEY_NAME = "root"
    const val HUMAN_MONKEY_NAME = "humn"
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

    fun isMonkeyDependentOn(monkeys: Map<String, Job>, monkeyName: String, depOn: String): Boolean {
        return when (val monkey = monkeys[monkeyName]!!) {
            is NumberJob -> false
            is MathJob -> {
                val oneOfDepsIsTheOne = monkey.waitFor[0] == depOn || monkey.waitFor[1] == depOn
                oneOfDepsIsTheOne || isMonkeyDependentOn(monkeys, monkey.waitFor[0], depOn) || isMonkeyDependentOn(
                    monkeys,
                    monkey.waitFor[1],
                    depOn
                )
            }
        }
    }

    fun getMonkeyResult2(
        monkeys: Map<String, Job>,
        monkeyName: String,
        findExpectedHumanNumber: Boolean = false,
        expectedNumber: Long? = null
    ): Pair<Long, Long?> {
        return when (val monkey = monkeys[monkeyName]!!) {
            is NumberJob -> monkey.number to null

            is MathJob -> {
                val (name, waitFor, operator) = monkey
                val (leftMonkey, rightMonkey) = waitFor
                val isLeftDepOnHuman = leftMonkey == Day21.HUMAN_MONKEY_NAME ||
                        isMonkeyDependentOn(monkeys, leftMonkey, Day21.HUMAN_MONKEY_NAME)
                val isRightDepOnHuman = rightMonkey == Day21.HUMAN_MONKEY_NAME ||
                        isMonkeyDependentOn(monkeys, rightMonkey, Day21.HUMAN_MONKEY_NAME)
                val monkeyDepOnHuman = if (isLeftDepOnHuman) leftMonkey else rightMonkey
                val monkeyNotDepOnHuman = if (isLeftDepOnHuman) rightMonkey else leftMonkey
                if (!findExpectedHumanNumber || (!isLeftDepOnHuman && !isRightDepOnHuman)) {
                    monkey.calc(
                        getMonkeyResult2(monkeys, leftMonkey).first,
                        getMonkeyResult2(monkeys, rightMonkey).first,
                    ) to null
                } else {
                    val (knownNumber) = getMonkeyResult2(monkeys, monkeyNotDepOnHuman)
                    if (name != Day21.ROOT_MONKEY_NAME && expectedNumber == null) error("Expected a expected number")
                    val expectedOtherNumber = if (name == Day21.ROOT_MONKEY_NAME) knownNumber else when (operator) {
                        '+' -> expectedNumber!! - knownNumber
                        '-' -> if (isLeftDepOnHuman) expectedNumber!! + knownNumber else knownNumber - expectedNumber!!
                        '*' -> expectedNumber!! / knownNumber
                        '/' -> if (isLeftDepOnHuman) expectedNumber!! * knownNumber else knownNumber / expectedNumber!!
                        else -> error("Unknown operator $operator")
                    }
                    if (monkeyDepOnHuman == Day21.HUMAN_MONKEY_NAME) {
                        expectedOtherNumber to expectedOtherNumber
                    } else {
                        getMonkeyResult2(
                            monkeys,
                            monkeyDepOnHuman,
                            true,
                            expectedOtherNumber
                        )
                    }
                }
            }
        }
    }

    fun part1(input: List<String>): Long {
        val monkeys = input.parse()
        return getMonkeyResult2(monkeys, Day21.ROOT_MONKEY_NAME).first
    }

    fun part2(input: List<String>): Long {
        val monkeys = input.parse()
        val (_, humanNumber) = getMonkeyResult2(monkeys, Day21.ROOT_MONKEY_NAME, true)
        return humanNumber!!
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    check(part1(testInput) == Day21.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    val part2Check = part2(testInput)
    println("Part 2 check: $part2Check")
    check(part2Check == Day21.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day21")
    println(part1(input))
    println(part2(input))
}
