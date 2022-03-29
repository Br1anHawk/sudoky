import java.util.LinkedList
import java.util.Queue
import kotlin.math.sqrt

class Sudoky(
    val table: Array<IntArray>
) {
    private val SIZE = table.size
    private val SIDE_OF_SQUARE_SIZE = sqrt(SIZE.toDouble()).toInt()
    private val mainTable = arrayListOf<ArrayList<Cell>>()
    //private val squares = arrayListOf<ArrayList<Square>>()
    private val cellsToFill: ArrayList<Cell> = arrayListOf()

    init {
        table.forEach {
            mainTable.add(arrayListOf())
        }
        for (i in 0 until table.size) {
            for (j in 0 until table[i].size) {
                mainTable[i].add(Cell(i, j, table[i][j]))
                if (table[i][j] == 0) {
                    cellsToFill.add(mainTable[i][j])
                }
            }
        }
    }

    fun resolve() {
//        for (squareI in 0 until SIDE_OF_SQUARE_SIZE step SIDE_OF_SQUARE_SIZE) {
//            for (squareJ in 0 until SIDE_OF_SQUARE_SIZE step SIDE_OF_SQUARE_SIZE) {
//                val allOptionsInSquare = arrayListOf(SIZE)
//                for (k in 1..SIZE) allOptionsInSquare.add(k)
//                for (i in 0 until SIDE_OF_SQUARE_SIZE) {
//                    for (j in 0 until SIDE_OF_SQUARE_SIZE) {
//                        allOptionsInSquare.remove(mainTable[squareI + i][squareJ + j].value)
//                    }
//                }
//                val options = arrayListOf<Int>()
//                options.addAll(allOptionsInSquare)
//                for (i in 0 until SIDE_OF_SQUARE_SIZE) {
//                    for (j in 0 until SIDE_OF_SQUARE_SIZE) {
//                        for (p in 0 until SIZE)  {
//                            options.remove(mainTable[p][squareJ + j].value)
//                            options.remove(mainTable[squareI + i][p].value)
//                        }
//                        mainTable[squareI + i][squareJ + j].options.addAll(options)
//                    }
//                }
//            }
//        }
        for (cell in cellsToFill) {
            val squareI = cell.i - cell.i % SIDE_OF_SQUARE_SIZE
            val squareJ = cell.j - cell.j % SIDE_OF_SQUARE_SIZE
            val allOptionsInSquare = arrayListOf<Int>()
            for (k in 1..SIZE) allOptionsInSquare.add(k)
            for (i in 0 until SIDE_OF_SQUARE_SIZE) {
                for (j in 0 until SIDE_OF_SQUARE_SIZE) {
                    allOptionsInSquare.remove(mainTable[squareI + i][squareJ + j].value)
                }
            }
            val options = arrayListOf<Int>()
            options.addAll(allOptionsInSquare)
            for (p in 0 until SIZE)  {
                options.remove(mainTable[p][cell.j].value)
                options.remove(mainTable[cell.i][p].value)
            }
            cell.options.addAll(options)
        }

        val associatives = Associatives(SIDE_OF_SQUARE_SIZE, cellsToFill)
        associatives.check()
        associatives.fillTable(mainTable)
        //while (checkForOneAvailableOptionInCell()) {}
    }

    fun printMainTableInConsole() {
        for (i in 0 until mainTable.size) {
            for(j in 0 until mainTable[i].size) {
                print(mainTable[i][j].value)
                print(" ")
            }
            println()
        }
        println()
    }

//    private fun checkForOneAvailableOptionInCell(): Boolean {
//        var cellForRemoving: Cell? = null
//        for (cell in cellsToFill) {
//            if (cell.options.size == 1) {
//                cell.value = cell.options[0]
//                cellForRemoving = cell
//                removeOptionValue(cell)
//                break
//            }
//        }
//        cellForRemoving?.let {
//            cellsToFill.remove(it)
//            return true
//        }
//        return false
//    }
//
//    private fun checkForOneAvailableOptionInSquare(): Boolean {
//        var cellForRemoving: Cell? = null
//        Loop@ for (cell in cellsToFill) {
//            val value = cell.value
//            for (cellForCheck in cell.square.cells) {
//                if (cellForCheck.value == value) {
//                    continue@Loop
//                }
//            }
//            cellForRemoving = cell
//            removeOptionValue(cell)
//            break
//        }
//        cellForRemoving?.let {
//            cellsToFill.remove(it)
//            return true
//        }
//        return false
//    }
//
//    private fun checkForOneAvailableOptionInRow(): Boolean {
//        var cellForRemoving: Cell? = null
//        Loop@ for (cell in cellsToFill) {
//            val value = cell.value
//            for (i in 0 until SIZE) {
//                if (mainTable[i][cell.j].value == value) {
//                    continue@Loop
//                }
//            }
//            cellForRemoving = cell
//            removeOptionValue(cell)
//            break
//        }
//        cellForRemoving?.let {
//            cellsToFill.remove(it)
//            return true
//        }
//        return false
//    }
//
//    private fun checkForOneAvailableOptionInColumn(): Boolean {
//
//        return false
//    }
//
//    private fun removeOptionValue(cell: Cell) {
//        for (k in 0 until SIZE) {
//            mainTable[k][cell.j].options.remove(cell.value)
//            mainTable[cell.i][k].options.remove(cell.value)
//        }
//        for (cellInSquare in cell.square.cells) {
//            cellInSquare.options.remove(cell.value)
//        }
//    }
}