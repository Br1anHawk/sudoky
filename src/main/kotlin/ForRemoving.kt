class ForRemoving {
    val squaresForRemoving = arrayListOf<Square>()
    val columnsForRemoving = arrayListOf<Int>()
    val rowsForRemoving = arrayListOf<Int>()

    fun addSquare(square: Square) {
        squaresForRemoving.add(square)
    }

    fun addColumn(j: Int) {
        columnsForRemoving.add(j)
    }

    fun addRow(i: Int) {
        rowsForRemoving.add(i)
    }
    fun isEmpty(): Boolean {
        return squaresForRemoving.isEmpty() && columnsForRemoving.isEmpty() && columnsForRemoving.isEmpty()
    }

    fun clear() {
        squaresForRemoving.clear()
        columnsForRemoving.clear()
        rowsForRemoving.clear()
    }
}