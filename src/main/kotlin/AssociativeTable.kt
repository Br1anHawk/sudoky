class AssociativeTable(
    val id: Int,
    val sideOfSquareSize: Int,
    val mainTable: ArrayList<ArrayList<Cell>>
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
                mainTable[i0 + i][j0 + j].options.remove(id)
            }
        }
        for (k in 0 until size) {
            table[cellI][k] = false
            mainTable[cellI][k].options.remove(id)
            table[k][cellJ] = false
            mainTable[k][cellJ].options.remove(id)
        }
        table[cellI][cellJ] = true
        mainTable[cellI][cellJ].value = id
        availableSquaresForChecking.remove(Square(Position(i0, j0)))
        availableColumnsForChecking.remove(cellJ)
        availableRowsForChecking.remove(cellI)
    }

    fun checkForAvailableOptions(associativeTables: ArrayList<AssociativeTable>): Boolean {
        var isFound = false
        do {
            val isFoundAvailableOptionsInSquares = checkForOneAvailableOptionInSquare(associativeTables)
            //Sudoky.writeMainTable(mainTable, "squares")
            val isFoundAvailableOptionsInColumns = checkForOneAvailableOptionInColumns(associativeTables)
            //Sudoky.writeMainTable(mainTable, "columns")
            val isFoundAvailableOptionsInRows = checkForOneAvailableOptionInRows(associativeTables)
            //Sudoky.writeMainTable(mainTable, "rows")
            val isFoundPointingPairsInRows = checkForPointingPairsInRows()
            val isFoundPointingPairsInColumns = checkForPointingPairsInColumns()
            val isFoundPointingPairsInSquares = checkForPointingPairsInSquares()
            if (isFoundAvailableOptionsInSquares
                || isFoundAvailableOptionsInColumns
                || isFoundAvailableOptionsInRows
                || isFoundPointingPairsInRows
                || isFoundPointingPairsInColumns
                || isFoundPointingPairsInSquares) {
                isFound = true
            }
        } while (isFoundAvailableOptionsInSquares
            || isFoundAvailableOptionsInColumns
            || isFoundAvailableOptionsInRows
            || isFoundPointingPairsInRows
            || isFoundPointingPairsInColumns
            || isFoundPointingPairsInSquares)
        return isFound
    }

    private fun checkForOneAvailableOptionInSquare(associativeTables: ArrayList<AssociativeTable>): Boolean {
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
                            mainTable[square.position.i + i][square.position.j + j]
                                .options
                                .remove(id)
                        }
                    }
                    for (k in 0 until size) {
                        table[position.i][k] = false
                        mainTable[position.i][k].options.remove(id)
                        table[k][position.j] = false
                        mainTable[k][position.j].options.remove(id)
                    }
                    table[position.i][position.j] = true
                    mainTable[position.i][position.j].value = id
                    excludeCellFromOtherAssociativeTables(associativeTables, mainTable[position.i][position.j])
                    forRemoving.addSquare(square)
                    forRemoving.addColumn(position.j)
                    forRemoving.addRow(position.i)
                    isFound = true
                }
            }
        } while (!forRemoving.isEmpty())
        return isFound
    }

    private fun checkForOneAvailableOptionInColumns(associativeTables: ArrayList<AssociativeTable>): Boolean {
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
                            mainTable[i0 + i][j0 + j].options.remove(id)
                        }
                    }
                    for (k in 0 until size) {
                        table[position][k] = false
                        mainTable[position][k].options.remove(id)
                        table[k][column] = false
                        mainTable[k][column].options.remove(id)
                    }
                    table[position][column] = true
                    mainTable[position][column].value = id
                    excludeCellFromOtherAssociativeTables(associativeTables, mainTable[position][column])
                    forRemoving.addSquare(Square(Position(i0, j0)))
                    forRemoving.addColumn(column)
                    forRemoving.addRow(position)
                    isFound = true
                }
            }
        } while (!forRemoving.isEmpty())
        return isFound
    }

    private fun checkForOneAvailableOptionInRows(associativeTables: ArrayList<AssociativeTable>): Boolean {
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
                            mainTable[i0 + i][j0 + j].options.remove(id)
                        }
                    }
                    for (k in 0 until size) {
                        table[row][k] = false
                        mainTable[row][k].options.remove(id)
                        table[k][position] = false
                        mainTable[k][position].options.remove(id)
                    }
                    table[row][position] = true
                    mainTable[row][position].value = id
                    excludeCellFromOtherAssociativeTables(associativeTables, mainTable[row][position])
                    forRemoving.addSquare(Square(Position(i0, j0)))
                    forRemoving.addColumn(position)
                    forRemoving.addRow(row)
                    isFound = true
                }
            }
        } while (!forRemoving.isEmpty())
        return isFound
    }



    private fun checkForPointingPairsInRows(): Boolean {
        //return false
        var isFound = false
        for (row in availableRowsForChecking) {
            val foundColumns = arrayListOf<Int>()
            for (j in 0 until size) {
                if (table[row][j]) {
                    foundColumns.add(j)
                }
            }
            if (isInSameSquare(foundColumns)) {
                val i0 = row - row % sideOfSquareSize
                val j0 = foundColumns[0] - foundColumns[0] % sideOfSquareSize
                for (i in 0 until sideOfSquareSize) {
                    if (i0 + i == row) continue
                     for (j in 0 until sideOfSquareSize) {
                        if (table[i0 + i][j0 + j]) isFound = true
                        table[i0 + i][j0 + j] = false
                        mainTable[i0 + i][j0 + j].options.remove(id)
                    }
                }
            }
        }
        return isFound
    }
    private fun checkForPointingPairsInColumns(): Boolean {
        //return false
        var isFound = false
        for (column in availableColumnsForChecking) {
            val foundRows = arrayListOf<Int>()
            for (i in 0 until size) {
                if (table[i][column]) {
                    foundRows.add(i)
                }
            }
            if (isInSameSquare(foundRows)) {
                val i0 = foundRows[0] - foundRows[0] % sideOfSquareSize
                val j0 = column - column % sideOfSquareSize
                for (j in 0 until sideOfSquareSize) {
                    if (j0 + j == column) continue
                    for (i in 0 until sideOfSquareSize) {
                        if (table[i0 + i][j0 + j]) isFound = true
                        table[i0 + i][j0 + j] = false
                        mainTable[i0 + i][j0 + j].options.remove(id)
                    }
                }
            }
        }
        return isFound
    }
    private fun isInSameSquare(array: ArrayList<Int>): Boolean {
        var isIn = false
        if (array.isEmpty() || array.size == 1) return isIn
        val posFirst = array[0] / sideOfSquareSize
        array.forEach {
            if (it / sideOfSquareSize != posFirst) return isIn
        }
        isIn = true
        return isIn
    }
    private fun checkForPointingPairsInSquares(): Boolean {
        var isFound = false
        for (square in availableSquaresForChecking) {

        }
        return isFound
    }


    private fun excludeCellFromOtherAssociativeTables(associativeTables: ArrayList<AssociativeTable>, cell: Cell) {
        cell.options.forEach { option ->
            associativeTables[option - 1].table[cell.i][cell.j] = false
        }
        cell.options.clear()
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