object Day11 {
    const val EXPECTED_PART1_CHECK_ANSWER = 10605L
    const val EXPECTED_PART2_CHECK_ANSWER = 2713310158

    const val PART1_RUNS = 20
    const val PART2_RUNS = 10_000

    const val WORRY_LEVEL_DIVISION_AMOUNT = 3

    val MONKEY_LINE_ITEMS = Regex("\\s+Starting items: ([, \\d]+)")
    val MONKEY_LINE_OPERATION = Regex("\\s+Operation: new = old ([+*]) (\\w+)")
    val MONKEY_LINE_TEST = Regex("\\s+Test: divisible by (\\d+)")
    val MONKEY_LINE_THROW = Regex("\\s+If (true|false): throw to monkey (\\d+)")
}

data class Item(var worryLevel: Long)

class Monkey(
    startingItems: List<Item>,
    val operation: (Long) -> Long,
    val testDivisibleBy: Int,
    private val testTrueDestinationMonkey: Int,
    private val testFalseDestinationMonkey: Int,
    private val relieve: Boolean = true,
) {
    private val items: MutableList<Item> = startingItems.toMutableList()
    var itemsInspected = 0
        private set

    fun addItems(items: List<Item>) {
        this.items.addAll(items)
    }

    fun turn(worryReduceVale: Int): Map<Int, List<Item>> {
        val itemsToThrow = mutableMapOf<Int, List<Item>>()
        for (item in items) {
            item.worryLevel = operation(item.worryLevel)
            if (relieve) {
                item.worryLevel /= Day11.WORRY_LEVEL_DIVISION_AMOUNT
            }
            item.worryLevel %= worryReduceVale
            itemsToThrow.compute(
                if (item.worryLevel % testDivisibleBy == 0L) testTrueDestinationMonkey else testFalseDestinationMonkey
            ) { _, currentItems ->
                (currentItems ?: emptyList()) + item
            }
            itemsInspected++
        }
        items.clear()
        return itemsToThrow
    }
}

class MonkeyBusiness(private val monkeys: List<Monkey>) {
    private val worryReduceVale = monkeys.map { it.testDivisibleBy }.reduce(Int::times)
    private fun round() {
        monkeys.forEach { monkey ->
            val itemsToPass = monkey.turn(worryReduceVale)
            itemsToPass.forEach { (toMonkey, items) ->
                monkeys[toMonkey].addItems(items)
            }
        }
    }

    fun process(numberOfRounds: Int): Long {
        repeat(numberOfRounds) {
            round()
        }
        return monkeys.map { it.itemsInspected }.sortedDescending().take(2).let { it[0].toLong() * it[1].toLong() }
    }
}

fun main() {

    fun List<String>.parseMonkeyBusiness(doRelieve: Boolean = true): MonkeyBusiness {
        val monkeys = mutableListOf<Monkey>()
        var inputIdx = 1
        while (inputIdx < size) {
            val (itemsWorryLevels) = Day11.MONKEY_LINE_ITEMS.matchEntire(this[inputIdx++])!!.destructured
            val items = itemsWorryLevels.split(",").map(String::trim).map { Item(it.toLong()) }
            val (operation, operationValue) = Day11.MONKEY_LINE_OPERATION.matchEntire(this[inputIdx++])!!.destructured
            val (testDivisibleValue) = Day11.MONKEY_LINE_TEST.matchEntire(this[inputIdx++])!!.destructured
            val (_, throwToWhenTrue) = Day11.MONKEY_LINE_THROW.matchEntire(this[inputIdx++])!!.destructured
            val (_, throwToWhenFalse) = Day11.MONKEY_LINE_THROW.matchEntire(this[inputIdx++])!!.destructured

            monkeys.add(
                Monkey(
                    items,
                    {
                        val operand = when (operationValue) {
                            "old" -> it
                            else -> operationValue.toLong()
                        }
                        when (operation) {
                            "*" -> it * operand
                            "+" -> it + operand
                            else -> error("Unknown operation $operation")
                        }
                    },
                    testDivisibleValue.toInt(),
                    throwToWhenTrue.toInt(),
                    throwToWhenFalse.toInt(),
                    doRelieve,
                )
            )
            inputIdx += 2
        }
        return MonkeyBusiness(monkeys)
    }

    fun part1(input: List<String>): Long {
        val monkeyBusiness = input.parseMonkeyBusiness()
        return monkeyBusiness.process(Day11.PART1_RUNS)
    }

    fun part2(input: List<String>): Long {
        val monkeyBusiness = input.parseMonkeyBusiness(false)
        return monkeyBusiness.process(Day11.PART2_RUNS)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == Day11.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    check(part2(testInput) == Day11.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}
