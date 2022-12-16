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
