class AssociativeTable(
    val id: Int,
    val sideOfSquareSize: Int
) {
    private val size = sideOfSquareSize * sideOfSquareSize
    private val table = arrayListOf<BooleanArray>()
    private val availableSquaresForChecking = arrayListOf<Square>()
    private val availableColumnsForChecking = arrayListOf<Int>()
    private val availableRowsForChecking = arrayListOf<Int>()
    private val forRemoving = ForRemoving()

    init {
        for (i in 0 until size) {
            table.add(BooleanArray(size))
        }
    }

    fun markCell(i: Int, j: Int) {
        table[i][j] = true
        val square = Square(Position(i - i % sideOfSquareSize, j - j % sideOfSquareSize))
        if (!availableSquaresForChecking.contains(square)) { availableSquaresForChecking.add(square) }
        if (!availableColumnsForChecking.contains(j)) { availableColumnsForChecking.add(j) }
        if (!availableRowsForChecking.contains(i)) { availableRowsForChecking.add(i) }
    }

    fun getCellValue(i: Int, j: Int): Boolean {
        return table[i][j]
    }

    fun excludeCell(cellI: Int, cellJ: Int) {
        val i0 = cellI - cellI % sideOfSquareSize
        val j0 = cellJ - cellJ % sideOfSquareSize
        for (i in 0 until sideOfSquareSize) {
            for (j in 0 until sideOfSquareSize) {
                table[i0 + i][j0 + j] = false
            }
        }
        for (k in 0 until size) {
            table[cellI][k] = false
            table[k][cellJ] = false
        }
        table[cellI][cellJ] = true
        availableSquaresForChecking.remove(Square(Position(i0, j0)))
        availableColumnsForChecking.remove(cellJ)
        availableRowsForChecking.remove(cellI)
    }

    fun checkForAvailableOptions(): Boolean {
        var isFound = false
        do {
            val isFoundAvailableOptionsInSquares = checkForOneAvailableOptionInSquare()
            val isFoundAvailableOptionsInColumns = checkForOneAvailableOptionInColumns()
            val isFoundAvailableOptionsInRows = checkForOneAvailableOptionInRows()
            if (isFoundAvailableOptionsInSquares || isFoundAvailableOptionsInColumns || isFoundAvailableOptionsInRows) {
                isFound = true
            }
        } while (isFoundAvailableOptionsInSquares || isFoundAvailableOptionsInColumns || isFoundAvailableOptionsInRows)
        return isFound
    }

    private fun checkForOneAvailableOptionInSquare(): Boolean {
        var isFound = false
        do {
            removeFoundSquaresColumnsRows()
            for (square in availableSquaresForChecking) {
                var countRepetitions = 0
                var position: Position = Position(-1, -1)
                for (i in 0 until sideOfSquareSize) {
                    for (j in 0 until sideOfSquareSize) {
                        if (table[square.position.i + i][square.position.j + j]) {
                            countRepetitions++
                            position = Position(
                                square.position.i + i,
                                square.position.j + j
                            )
                        }
                    }
                }
                if (countRepetitions == 1) {
                    for (i in 0 until sideOfSquareSize) {
                        for (j in 0 until sideOfSquareSize) {
                            table[square.position.i + i][square.position.j + j] =
                                false
                        }
                    }
                    for (k in 0 until size) {
                        table[position.i][k] = false
                        table[k][position.j] = false
                    }
                    table[position.i][position.j] = true
                    forRemoving.addSquare(square)
                    forRemoving.addColumn(position.j)
                    forRemoving.addRow(position.i)
                    isFound = true
                }
            }
        } while (!forRemoving.isEmpty())
        return isFound
    }

    private fun checkForOneAvailableOptionInColumns(): Boolean {
        var isFound = false
        do {
            removeFoundSquaresColumnsRows()
            for (column in availableColumnsForChecking) {
                var countRepetitions = 0
                var position = -1
                for (i in 0 until size) {
                    if (table[i][column])  {
                        countRepetitions++
                        position = i
                    }
                }
                if (countRepetitions == 1) {
                    val i0 = position - position % sideOfSquareSize
                    val j0 = column - column % sideOfSquareSize
                    for (i in 0 until sideOfSquareSize) {
                        for (j in 0 until sideOfSquareSize) {
                            table[i0 + i][j0 + j] = false
                        }
                    }
                    for (k in 0 until size) {
                        table[position][k] = false
                        table[k][position] = false
                    }
                    table[position][column] = true
                    forRemoving.addSquare(Square(Position(i0, j0)))
                    forRemoving.addColumn(column)
                    forRemoving.addRow(position)
                    isFound = true
                }
            }
        } while (!forRemoving.isEmpty())
        return isFound
    }

    private fun checkForOneAvailableOptionInRows(): Boolean {
        var isFound = false
        do {
            removeFoundSquaresColumnsRows()
            for (row in availableRowsForChecking) {
                var countRepetitions = 0
                var position = -1
                for (j in 0 until size) {
                    if (table[row][j])  {
                        countRepetitions++
                        position = j
                    }
                }
                if (countRepetitions == 1) {
                    val i0 = row - row % sideOfSquareSize
                    val j0 = position - position % sideOfSquareSize
                    for (i in 0 until sideOfSquareSize) {
                        for (j in 0 until sideOfSquareSize) {
                            table[i0 + i][j0 + j] = false
                        }
                    }
                    for (k in 0 until size) {
                        table[position][k] = false
                        table[k][position] = false
                    }
                    table[row][position] = true
                    forRemoving.addSquare(Square(Position(i0, j0)))
                    forRemoving.addColumn(position)
                    forRemoving.addRow(row)
                    isFound = true
                }
            }
        } while (!forRemoving.isEmpty())
        return isFound
    }

    private fun removeFoundSquaresColumnsRows() {
        for (square in forRemoving.squaresForRemoving) {
            availableSquaresForChecking.remove(square)
        }
        for (column in forRemoving.columnsForRemoving) {
            availableColumnsForChecking.remove(column)
        }
        for (row in forRemoving.rowsForRemoving) {
            availableRowsForChecking.remove(row)
        }
        forRemoving.clear()
    }
}