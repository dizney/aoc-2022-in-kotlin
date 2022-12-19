object Day18 {
    const val EXPECTED_PART1_CHECK_ANSWER = 64
    const val EXPECTED_PART2_CHECK_ANSWER = 58
}

fun main() {
    val air = mutableMapOf<Point3D, Boolean>()

    fun List<String>.parsePoints() =
        map { line ->
            val xyz = line.split(',')
            Point3D(xyz[0].toInt(), xyz[1].toInt(), xyz[2].toInt())
        }

    fun Point3D.sides() =
        listOf(
            Triple(-1, 0, 0),
            Triple(0, 1, 0),
            Triple(1, 0, 0),
            Triple(0, -1, 0),
            Triple(0, 0, 1),
            Triple(0, 0, -1),
        ).map { (xMove, yMove, zMove) ->
            Point3D(x + xMove, y +yMove, z + zMove)
        }

    fun addToCache(points: Iterable<Point3D>, isAir: Boolean): Boolean {
        points.forEach { air[it] = isAir }
        return isAir
    }

    fun Point3D.outsideRange(pMin: Point3D, pMax: Point3D) = x < pMin.x || x > pMax.x || y < pMin.y || y > pMax.y || z < pMin.z || z > pMax.z

    fun Point3D.isAir(points: List<Point3D>, pMin: Point3D, pMax: Point3D): Boolean {
        air[this]?.let { return it }
        val frontier = ArrayDeque(listOf(this))
        val visited = mutableSetOf<Point3D>()
        while (!frontier.isEmpty()) {
            val current = frontier.removeFirst()
            when {
                !visited.add(current) -> continue
                current in air -> return addToCache(visited, air.getValue(current))
                current.outsideRange(pMin, pMax) -> return addToCache(visited, false)
                else -> frontier.addAll(current.sides().filter { it !in points })
            }
        }
        return addToCache(visited, true)
    }

    fun part1(input: List<String>): Int {
        val points = input.parsePoints()
        return points.sumOf { point ->
            point.sides().count { it !in points }
        }
    }

    fun part2(input: List<String>): Int {
        val points = input.parsePoints()
        val pMin = Point3D(points.minOf { it.x }, points.minOf { it.y }, points.minOf { it.z })
        val pMax = Point3D(points.maxOf { it.x }, points.maxOf { it.y }, points.maxOf { it.z })

        val result = points.sumOf { point ->
            point.sides().filter { it !in points }.count { !it.isAir(points, pMin, pMax) }
        }
        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == Day18.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    check(part2(testInput) == Day18.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}
