import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

data class Location(val x: Int, val y: Int)

enum class Direction(val move: Coordinates) {
    WEST(Coordinates(-1, 0)),
    NORTH(Coordinates(0, -1)),
    EAST(Coordinates(1, 0)),
    SOUTH(Coordinates(0, 1));

    fun next() = when (this) {
        WEST -> EAST
        NORTH -> SOUTH
        EAST -> NORTH
        SOUTH -> WEST
    }
}

data class Coordinates(val x: Int, val y: Int) {
    operator fun plus(distance: Coordinates): Coordinates = Coordinates(this.x + distance.x, this.y + distance.y)

    fun move(direction: Direction) = when (direction) {
        Direction.NORTH -> Coordinates(x, y - 1)
        Direction.SOUTH -> Coordinates(x, y + 1)
        Direction.EAST -> Coordinates(x + 1, y)
        Direction.WEST -> Coordinates(x - 1, y)
    }

    fun adjacentPositions(includeDiagonal: Boolean = true): Set<Coordinates> =
        adjacentPositions(Direction.NORTH, includeDiagonal) +
                adjacentPositions(Direction.EAST, includeDiagonal) +
                adjacentPositions(Direction.SOUTH, includeDiagonal) +
                adjacentPositions(Direction.WEST, includeDiagonal)

    fun adjacentPositions(direction: Direction, includeDiagonal: Boolean = true): Set<Coordinates> =
        when (direction) {
            Direction.NORTH -> setOf(
                this + Coordinates(0, -1),
            ) + if (includeDiagonal) setOf(
                this + Coordinates(-1, -1),
                this + Coordinates(1, -1),
            ) else emptySet()
            Direction.EAST -> setOf(
                this + Coordinates(1, 0),
            ) + if (includeDiagonal) setOf(
                this + Coordinates(1, -1),
                this + Coordinates(1, 1),
            ) else emptySet()
            Direction.SOUTH -> setOf(
                this + Coordinates(0, 1),
            ) + if (includeDiagonal) setOf(
                this + Coordinates(1, 1),
                this + Coordinates(-1, 1),
            ) else emptySet()
            Direction.WEST -> setOf(
                this + Coordinates(-1, 0),
            ) + if (includeDiagonal) setOf(
                this + Coordinates(-1, -1),
                this + Coordinates(-1, 1),
            ) else emptySet()
        }
}

data class BigCoordinates(val x: Long, val y: Long)

data class Point3D(val x: Int, val y: Int, val z: Int)

fun List<Coordinates>.findMinAndMax(): Pair<Coordinates, Coordinates> =
    fold(Coordinates(Int.MAX_VALUE, Int.MAX_VALUE) to Coordinates(0, 0)) { (min, max), (x, y) ->
        Coordinates(
            minOf(min.x, x),
            minOf(min.y, y)
        ) to Coordinates(
            maxOf(max.x, x),
            maxOf(max.y, y)
        )
    }
