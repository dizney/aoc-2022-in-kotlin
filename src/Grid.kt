class GridMap(
    private val topLeft: Coordinates,
    private val bottomRight: Coordinates,
    private val emptyCellValue: Int,
    initializer: GridMap.() -> Unit
) {
    private val gridWidth = (bottomRight.x - topLeft.x) + 1
    private val gridHeight = (bottomRight.y - topLeft.y) + 1
    private val data: Array<Array<Int>> = Array(gridWidth) { Array(gridHeight) { emptyCellValue } }

    init {
        initializer.invoke(this)
    }

    fun valueAt(coords: Coordinates) = data[coords.x - topLeft.x][coords.y - topLeft.y]

    fun setValueAt(coords: Coordinates, value: Int) {
        data[coords.x - topLeft.x][coords.y - topLeft.y] = value
    }

    fun countValues(value: Int): Int = data.sumOf { it.count { posValue -> value == posValue } }

    fun draw() {
        for (y in 0 until gridHeight) {
            for (x in 0 until gridWidth) {
                print(data[x][y])
            }
            println()
        }
    }

}