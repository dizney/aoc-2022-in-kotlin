object Day08 {
    const val EXPECTED_PART1_CHECK_ANSWER = 21
    const val EXPECTED_PART2_CHECK_ANSWER = 8
}

fun main() {

    fun List<String>.parseTreeGrid(): List<List<Int>> =
        this.map { it.toList() }.map { it.map { chr -> chr.digitToInt() } }

    fun List<List<Int>>.isVisible(x: Int, y: Int): Boolean {
        if (x == 0 || y == 0 || x == this[0].size - 1 || y == this.size - 1) return true
        val heightOfTree = this[y][x]
        if (this[y].subList(0, x).all { it < heightOfTree }) return true
        if (this[y].subList(x + 1, this[y].size).all { it < heightOfTree }) return true
        val treesInSameVerticalRow = this.fold(emptyList<Int>()) { acc, row -> acc + row[x] }
        if (treesInSameVerticalRow.subList(0, y).all { it < heightOfTree }) return true
        if (treesInSameVerticalRow.subList(y + 1, treesInSameVerticalRow.size).all { it < heightOfTree }) return true

        return false
    }

    fun List<List<Int>>.scenicScore(x: Int, y: Int): Int {
        val heightOfTree = this[y][x]

        val treesToTheLeft = this[y].subList(0, x)
        val leftTreesVisible = treesToTheLeft
            .takeLastWhile { it < heightOfTree }.size
            .let { if (it == treesToTheLeft.size) it else it + 1 } // accommodate for edges
        val treesToTheRight = this[y].subList(x + 1, this[y].size)
        val rightTreesVisible = treesToTheRight
            .takeWhile { it < heightOfTree }.size
            .let { if (it == treesToTheRight.size) it else it + 1 } // accommodate for edges

        val treesInSameVerticalRow = this.fold(emptyList<Int>()) { acc, row -> acc + row[x] }
        val treesToTheTop = treesInSameVerticalRow.subList(0, y)
        val upTreesVisible = treesToTheTop
            .takeLastWhile { it < heightOfTree }.size
            .let { if (it == treesToTheTop.size) it else it + 1 } // accommodate for edge
        val treesToTheBottom = treesInSameVerticalRow.subList(y + 1, treesInSameVerticalRow.size)
        val downTreesVisible = treesToTheBottom
                .takeWhile { it < heightOfTree }.size
                .let { if (it == treesToTheBottom.size) it else it + 1 } // accommodate for edge

        return leftTreesVisible * rightTreesVisible * upTreesVisible * downTreesVisible
    }

    fun part1(input: List<String>): Int {
        val treeGrid = input.parseTreeGrid()
        var treesVisible = 0
        for (xIndex in treeGrid[0].indices) {
            for (yIndex in treeGrid.indices) {
                if (treeGrid.isVisible(xIndex, yIndex)) {
                    treesVisible++
                }
            }
        }
        return treesVisible
    }

    fun part2(input: List<String>): Int {
        val treeGrid = input.parseTreeGrid()
        var highestScenicScore = 0
        for (xIndex in treeGrid[0].indices) {
            for (yIndex in treeGrid.indices) {
                val scenicScore = treeGrid.scenicScore(xIndex, yIndex)
                highestScenicScore = maxOf(scenicScore, highestScenicScore)
            }
        }
        return highestScenicScore
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == Day08.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    check(part2(testInput) == Day08.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
