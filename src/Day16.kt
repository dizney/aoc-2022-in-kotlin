import java.util.*

object Day16 {
    const val EXPECTED_PART1_CHECK_ANSWER = 1651
    const val EXPECTED_PART2_CHECK_ANSWER = 1707

    const val START_VALVE_NAME = "AA"

    const val RUNTIME_MINS_PART1 = 30
    const val RUNTIME_MINS_PART2 = 26
}

data class Valve(val name: String, val flowRate: Int) {
    private var _accessibleValves: Set<Valve> = emptySet()

    val accessibleValves
        get() = _accessibleValves

    fun addAccessibleValve(valve: Valve) {
        _accessibleValves += valve
    }
}

fun main() {

    fun List<String>.parseValves(): MutableMap<String, Valve> {
        val paths = mutableMapOf<String, List<String>>()
        val valves = mutableMapOf<String, Valve>()
        forEach { valveString ->
            val (name, flowRate, accessibleValves) = Regex(
                "Valve (.+) has flow rate=(\\d+); tunnels? leads? to valves? (.*)"
            )
                .matchEntire(valveString)?.destructured ?: error("Valve line not matched")
            if (valves.contains(name)) error("Duplicate valve name detected")
            paths[name] = accessibleValves.split(',').map(String::trim)
            valves[name] = Valve(name, flowRate.toInt())
        }
        paths.forEach { (name, accessibleValves) ->
            accessibleValves.forEach { accessibleValveName ->
                valves[name]?.addAccessibleValve(
                    valves[accessibleValveName] ?: error("Could not find accessible valve")
                )
            }
        }
        return valves
    }

    fun shortestPathDistance(start: Valve, dest: Valve): Int {
        val queue = LinkedList<Pair<Valve, Int>>()
        val visited = mutableSetOf<Valve>()

        queue.add(start to 0)
        visited.add(start)

        while (queue.isNotEmpty()) {
            val (valve, distance) = queue.remove()
            if (valve == dest) {
                return distance
            }

            for (accessibleValve in valve.accessibleValves) {
                if (!visited.contains(accessibleValve)) {
                    queue.add(accessibleValve to distance + 1)
                    visited.add(accessibleValve)
                }
            }
        }
        error("No shortest path found for $start to $dest")
    }

    data class Opener(val currentLocation: Valve, val minutesRemaining: Int)

    fun releasedPressure(
        distances: Map<Valve, Map<Valve, Int>>,
        openenersData: List<Opener>,
        opened: List<Valve> = emptyList(),
        releasedPressure: Int = 0,
    ): Int {
        val openerData = openenersData.maxBy { it.minutesRemaining }
        return distances[openerData.currentLocation]!!.filter { it.key !in opened }
            .maxOfOrNull { (nextValve, distance) ->
                val newMinutesRemaining = openerData.minutesRemaining - distance - 1
                if (newMinutesRemaining > 0) {
                    releasedPressure(
                        distances,
                        openenersData = openenersData.minus(openerData) + Opener(nextValve, newMinutesRemaining),
                        opened = opened + nextValve,
                        releasedPressure = releasedPressure + nextValve.flowRate * newMinutesRemaining,
                    )
                } else {
                    releasedPressure
                }
            } ?: releasedPressure
    }


    fun part1(input: List<String>, startValveName: String): Int {
        val valvesMap = input.parseValves()

        val distances = valvesMap.values.associateWith { startValve ->
            valvesMap.values.filter { it.flowRate > 0 }.associateWith { destValve ->
                shortestPathDistance(startValve, destValve)
            }
        }

        return releasedPressure(
            distances,
            openenersData = listOf(Opener(valvesMap[startValveName]!!, Day16.RUNTIME_MINS_PART1)),
        )
    }

    fun part2(input: List<String>): Int {
        val valvesMap = input.parseValves()

        val distances = valvesMap.values.associateWith { startValve ->
            valvesMap.values.filter { it.flowRate > 0 }.associateWith { destValve ->
                shortestPathDistance(startValve, destValve)
            }
        }

        val result = releasedPressure(
            distances,
            openenersData = listOf(
                Opener(valvesMap[Day16.START_VALVE_NAME]!!, Day16.RUNTIME_MINS_PART2),
                Opener(valvesMap[Day16.START_VALVE_NAME]!!, Day16.RUNTIME_MINS_PART2)
            ),
        )
        println("Part 2 result $result")
        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    println("Part 1 check")
    check(part1(testInput, Day16.START_VALVE_NAME) == Day16.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    println("Part 2 check")
    check(part2(testInput) == Day16.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day16")
    println("Part 1")
    println(part1(input, Day16.START_VALVE_NAME))
    println("Part 2")
    println(part2(input))
}
