object Day19 {
    const val EXPECTED_PART1_CHECK_ANSWER = 33
    const val EXPECTED_PART2_CHECK_ANSWER = 3472

    val BLUEPRINT_REGEX =
        Regex("Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.")

    const val PART1_MINUTES = 24
}

enum class RobotCurrency {
    ORE, CLAY, OBSIDIAN
}

enum class RobotType {
    ORE_COLLECTOR, CLAY_COLLECTOR, OBSIDIAN_COLLECTOR, GEODE_CRACKING
}

data class RobotBuildCost(val amount: Int, val currency: RobotCurrency)

data class Blueprint(
    val id: Int,
    val buildCosts: Map<RobotType, Set<RobotBuildCost>>,
)

data class CollectingState(
    val minutesDone: Int = 0,
    val orePerMinute: Int = 1,
    val clayPerMinute: Int = 0,
    val obsidianPerMinute: Int = 0,
    val geoCrackedPerMinute: Int = 0,
    val ore: Int = 0,
    val clay: Int = 0,
    val obs: Int = 0,
    val geoCracked: Int = 0,
)

fun main() {
    fun List<String>.parseBlueprints() =
        map {
            val (id, oreRobotOre, clayRobotOre, obsRobotOre, obsRobotClay, geoRobotOre, geoRobotObs) = Day19.BLUEPRINT_REGEX.matchEntire(
                it
            )?.destructured ?: error("Oops")
            Blueprint(
                id.toInt(),
                mapOf(
                    RobotType.ORE_COLLECTOR to setOf(RobotBuildCost(oreRobotOre.toInt(), RobotCurrency.ORE)),
                    RobotType.CLAY_COLLECTOR to setOf(RobotBuildCost(clayRobotOre.toInt(), RobotCurrency.ORE)),
                    RobotType.OBSIDIAN_COLLECTOR to setOf(RobotBuildCost(obsRobotOre.toInt(), RobotCurrency.ORE), RobotBuildCost(obsRobotClay.toInt(), RobotCurrency.CLAY)),
                    RobotType.GEODE_CRACKING to setOf(RobotBuildCost(geoRobotOre.toInt(), RobotCurrency.ORE), RobotBuildCost(geoRobotObs.toInt(), RobotCurrency.OBSIDIAN)),
                )
            )
        }

    fun findBest(
        blueprint: Blueprint,
        minutesToRun: Int,
        state: CollectingState,
    ): Int {
        val minutesLeft = minutesToRun - state.minutesDone
        return blueprint.buildCosts
            .maxOf { (robotType, buildCosts) ->
                if (
                    (robotType == RobotType.ORE_COLLECTOR && state.orePerMinute > 5) ||
                    (robotType == RobotType.CLAY_COLLECTOR && state.clayPerMinute > 5) ||
                    (robotType == RobotType.OBSIDIAN_COLLECTOR && state.obsidianPerMinute > 5)
                ){
                    -1
                } else {
                    val minutesPerCurrency = buildCosts.map {
                        when (it.currency) {
                            RobotCurrency.ORE -> if (state.ore >= it.amount) 0 else if (state.orePerMinute > 0) it.amount - state.ore / state.orePerMinute else -1
                            RobotCurrency.CLAY -> if (state.clay >= it.amount) 0 else if (state.clayPerMinute > 0) it.amount - state.clay / state.clayPerMinute else -1
                            RobotCurrency.OBSIDIAN -> if (state.obs >= it.amount) 0 else if (state.obsidianPerMinute > 0) it.amount - state.obs / state.obsidianPerMinute else -1
                        }
                    }
                    val canNotMake = minutesPerCurrency.any { it == -1 }
                    val minutesUntilWeCanMakeRobot = minutesPerCurrency.max() + 1

                    if (canNotMake || minutesUntilWeCanMakeRobot >= minutesLeft) {
                        state.copy(
                            minutesDone = minutesToRun,
                            ore = state.ore + state.orePerMinute * minutesLeft,
                            clay = state.clay + state.clayPerMinute * minutesLeft,
                            obs = state.obs + state.obsidianPerMinute * minutesLeft,
                            geoCracked = state.geoCracked + state.geoCrackedPerMinute * minutesLeft,
                        ).geoCracked
                    } else {
                        findBest(
                            blueprint,
                            minutesToRun,
                            state.copy(
                                minutesDone = state.minutesDone + minutesUntilWeCanMakeRobot,
                                ore = state.ore + (minutesUntilWeCanMakeRobot * state.orePerMinute) - (buildCosts.firstOrNull { it.currency == RobotCurrency.ORE }?.amount
                                    ?: 0),
                                clay = state.clay + (minutesUntilWeCanMakeRobot * state.clayPerMinute) - (buildCosts.firstOrNull { it.currency == RobotCurrency.CLAY }?.amount
                                    ?: 0),
                                obs = state.obs + (minutesUntilWeCanMakeRobot * state.obsidianPerMinute) - (buildCosts.firstOrNull { it.currency == RobotCurrency.OBSIDIAN }?.amount
                                    ?: 0),
                                geoCracked = state.geoCracked + (minutesUntilWeCanMakeRobot * state.geoCrackedPerMinute),
                                orePerMinute = state.orePerMinute + if (robotType == RobotType.ORE_COLLECTOR) 1 else 0,
                                clayPerMinute = state.clayPerMinute + if (robotType == RobotType.CLAY_COLLECTOR) 1 else 0,
                                obsidianPerMinute = state.obsidianPerMinute + if (robotType == RobotType.OBSIDIAN_COLLECTOR) 1 else 0,
                                geoCrackedPerMinute = state.geoCrackedPerMinute + if (robotType == RobotType.GEODE_CRACKING) 1 else 0,
                            )
                        )
                    }
                }
            }
    }

    fun part1(input: List<String>): Int {
        val bluePrints = input.parseBlueprints()
        println(bluePrints)
        bluePrints.forEach { bluePrint -> println("${bluePrint.id}: ${findBest(bluePrint, 24 /*Day19.PART1_MINUTES*/, CollectingState())}") }
        return 1
    }

    fun part2(input: List<String>): Int {
        return 1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    check(part1(testInput) == Day19.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    check(part2(testInput) == Day19.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day19")
    println(part1(input))
    println(part2(input))
}
