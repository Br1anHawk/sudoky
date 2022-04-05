class Associatives (
    val sideOfSquareSize: Int,
    val cellsToFill: ArrayList<Cell>,
    val mainTable: ArrayList<ArrayList<Cell>>
) {
    private val size = sideOfSquareSize * sideOfSquareSize
    private val associativeTables = arrayListOf<AssociativeTable>()
    private val cellsForRemoving = arrayListOf<Cell>()

    init {
        for (id in 1.. size) {
            associativeTables.add(AssociativeTable(id, sideOfSquareSize, mainTable))
        }

        cellsToFill.forEach { cell ->
            cell.options.forEach {
                associativeTables[it - 1].markCell(cell.i, cell.j)
            }
        }
    }

    fun fillTable (mainTable: ArrayList<ArrayList<Cell>>) {
        associativeTables.forEach { associativeTable ->
            for (i in 0 until size) {
                for (j in 0 until size) {
                    if (associativeTable.getCellValue(i, j)) {
                        mainTable[i][j].value = associativeTable.id
                    }
                }
            }
        }
    }

    fun check() {
        do {
            checkOnlyOneAvailableOptionInCell()
            //Sudoky.writeMainTable(mainTable, "singleCell")
            var isFound = false
            associativeTables.forEach {
                if (it.checkForAvailableOptions(associativeTables)) {
                    isFound = true
                }
                //Sudoky.writeMainTable(mainTable, "afterAssociativeTable - ${it.id}")
            }
            //Sudoky.writeMainTable(mainTable, "afterAllAssociativeTables")
        } while (isFound)
    }

    private fun checkOnlyOneAvailableOptionInCell() {
        do {
            cellsForRemoving.forEach { cellsToFill.remove(it) }
            cellsForRemoving.clear()
            for (cell in cellsToFill) {
                var countRepetitions = 0
                var id = -1
                associativeTables.forEach {
                    if (it.getCellValue(cell.i, cell.j)) {
                        countRepetitions++
                        id = it.id
                    }
                }
                if (countRepetitions == 1) {
                    associativeTables[id - 1].excludeCell(cell.i, cell.j)
                    cellsForRemoving.add(cell)
                }
            }
        } while (cellsForRemoving.isNotEmpty())
    }
}