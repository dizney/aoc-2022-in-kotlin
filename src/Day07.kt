object Day07 {
    const val EXPECTED_PART1_CHECK_ANSWER = 95437
    const val EXPECTED_PART2_CHECK_ANSWER = 24933642

    const val DIR_SIZE_THRESHOLD = 100_000
    const val FS_TOTAL_SIZE = 70_000_000
    const val FS_NEEDED_FREE_SPACE = 30_000_000
}

sealed class Entry(val name: String)

class File(name: String, val size: Int) : Entry(name)

class Dir(name: String, val parentDir: Dir? = null) : Entry(name) {
    private var content: List<Entry> = emptyList()

    fun add(entry: Entry): Entry {
        content += entry
        return this
    }

    fun dirContent() = content
}

fun main() {

    val fileListingRegex = Regex("(\\d+) (\\S+)")

    fun parseDirTree(input: List<String>): Dir {
        val rootDir = Dir("/")
        var currentDir = rootDir
        input.drop(1).forEach { entry ->
            when {
                entry.startsWith("$ cd ..") -> {
                    if (currentDir.parentDir != null) {
                        currentDir = currentDir.parentDir!!
                    }
                }

                entry.startsWith("$ cd") -> {
                    val newDir = Dir(entry.substring("$ cd ".length), currentDir)
                    currentDir.add(newDir)
                    currentDir = newDir
                }

                entry.matches(fileListingRegex) -> {
                    val match = fileListingRegex.matchEntire(entry)
                    if (match != null) {
                        currentDir.add(File(match.groupValues[2], match.groupValues[1].toInt()))
                    }
                }
            }
        }
        return rootDir
    }

    fun dirSizes(dir: Dir, sizes: MutableList<Int>): Int {
        var totalDirSize = 0
        dir.dirContent().forEach { subEntry ->
            totalDirSize += when (subEntry) {
                is File -> subEntry.size
                is Dir -> dirSizes(subEntry, sizes)
            }
        }
        sizes += totalDirSize
        return totalDirSize
    }

    fun part1(input: List<String>): Int {
        val rootDir = parseDirTree(input)
        val sizesOfDirs = mutableListOf<Int>()
        dirSizes(rootDir, sizesOfDirs)
        return sizesOfDirs.filter { it <= Day07.DIR_SIZE_THRESHOLD }.sum()
    }

    fun part2(input: List<String>): Int {
        val rootDir = parseDirTree(input)
        val sizesOfDirs = mutableListOf<Int>()
        val rootDirSize = dirSizes(rootDir, sizesOfDirs)
        val currentFreeSpace = Day07.FS_TOTAL_SIZE - rootDirSize
        val needToFreeUp = Day07.FS_NEEDED_FREE_SPACE - currentFreeSpace
        return sizesOfDirs.filter { it >= needToFreeUp }.sorted()[0]
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == Day07.EXPECTED_PART1_CHECK_ANSWER) { "Part 1 failed" }
    check(part2(testInput) == Day07.EXPECTED_PART2_CHECK_ANSWER) { "Part 2 failed" }

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
