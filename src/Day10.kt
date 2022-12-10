object Day10 {
    const val EXPECTED_PART1_CHECK_ANSWER = 13140
    const val EXPECTED_PART2_CHECK_ANSWER = 1

    val CYCLE_PROBE_POINTS = listOf(20, 60, 100, 140, 180, 220)
    const val CRT_LINE_WIDTH = 40
}

sealed class CpuInstruction(val cycles: Int)

class Noop : CpuInstruction(1)
class AddX(val arg: Int) : CpuInstruction(2)

class Cpu(val instructions: List<CpuInstruction>) : ClockListener {
    private var instructionPointer: Int = 0
    private var instructionCyclesDone: Int = 0
    private val x: MutableList<Int> = mutableListOf(1)

    fun isDone() = instructionPointer >= instructions.size

    fun xAtCycle(cycle: Int) = x[cycle - 1]

    fun x() = x.last()

    override fun tickStart(cycle: Int) {
        // NOOP
    }

    override fun tickEnd(cycle: Int) {
        instructionCyclesDone++
        val currentXValue = if (x.isEmpty()) 1 else x.last()
        when (val currentInstruction = instructions[instructionPointer]) {
            is Noop -> {
                x.add(currentXValue)
                instructionPointer++
                instructionCyclesDone = 0
            }

            is AddX -> {
                if (currentInstruction.cycles == instructionCyclesDone) {
                    x.addAll(listOf(currentXValue, currentXValue + currentInstruction.arg))
                    instructionPointer++
                    instructionCyclesDone = 0
                }
            }
        }

    }
}

class Crt(private val cpu: Cpu) : ClockListener {
    override fun tickStart(cycle: Int) {
        val crtPos = cycle - 1
        val lineNumber = crtPos / Day10.CRT_LINE_WIDTH
        val positionInLine = crtPos - lineNumber * Day10.CRT_LINE_WIDTH
        print(if (positionInLine in (cpu.x() - 1)..(cpu.x() + 1)) "#" else ".")
        if (crtPos > 0 && cycle % Day10.CRT_LINE_WIDTH == 0) println()
    }

    override fun tickEnd(cycle: Int) {
        // NOOP
    }

}

interface ClockListener {
    fun tickStart(cycle: Int)
    fun tickEnd(cycle: Int)
}

fun main() {
    fun List<String>.parseInstructions(): List<CpuInstruction> =
        map { it.split(" ") }.map {
            when (it[0]) {
                "noop" -> Noop()
                "addx" -> AddX(it[1].toInt())
                else -> error("Unknown operation ${it[0]}")
            }
        }

    fun part1(input: List<String>): Int {
        val cpu = Cpu(input.parseInstructions())
        var cycle = 1
        while (!cpu.isDone()) {
            cpu.tickStart(cycle)
            cpu.tickEnd(cycle)
            cycle++
        }
        return Day10.CYCLE_PROBE_POINTS.sumOf { cpu.xAtCycle(it) * it }
    }

    fun part2(input: List<String>): Int {
        println("\n\n")
        val cpu = Cpu(input.parseInstructions())
        val crt = Crt(cpu)
        var cycle = 1
        while (!cpu.isDone()) {
            cpu.tickStart(cycle)
            crt.tickStart(cycle)
            cpu.tickEnd(cycle)
            crt.tickEnd(cycle)
            cycle++
        }
        return 1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == Day10.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    check(part2(testInput) == Day10.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}
