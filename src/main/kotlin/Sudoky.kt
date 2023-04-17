import java.io.BufferedWriter
import java.io.FileWriter
import kotlin.math.sqrt

class Sudoky(
    val table: Array<IntArray>
) {
    private val EMPTY_CELL = 0
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
                if (table[i][j] == EMPTY_CELL) {
                    mainTable[i][j].status = Status.EMPTY
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

        val associatives = Associatives(SIDE_OF_SQUARE_SIZE, cellsToFill, mainTable)
        associatives.check()
        //associatives.fillTable(mainTable)
        //while (checkForOneAvailableOptionInCell()) {}
    }

    fun writeSolutionTableToHtmlFile(filePath: String, fileName: String) {
        val padding = "20px"
        val htmlString = StringBuilder()
        val delay = 1000
        htmlString.append("<html>")
        htmlString.append("<body>")
        htmlString.append("<script src=\"speech.js\"></script> ")
        htmlString.append("<script>")
        for (digit in getTextForSpeech()) {
            htmlString.append("setTimeout(()=>{speak($digit)}, $delay);")
        }
        //htmlString.append("speak(\"" + getTextForSpeech() + "\");")
        htmlString.append("</script>")
        htmlString.append("<table style = 'font-size: 60px' border = '0'>")
        htmlString.append("<tbody>")
        for (i in mainTable.indices) {
             if (i % SIDE_OF_SQUARE_SIZE == 0) {
                    htmlString.append("<tr>")
                    htmlString.append("<td colspan = '${SIZE + SIDE_OF_SQUARE_SIZE}'>")
                    htmlString.append("<p style = 'padding-bottom: $padding'>")
                    htmlString.append("</p>")
                    htmlString.append("</td>")
                    htmlString.append("</tr>")
            }
            htmlString.append("<tr>")
            for (j in mainTable[i].indices) {
                if (j % SIDE_OF_SQUARE_SIZE == 0) {
                    htmlString.append("<td>")
                    htmlString.append("<p style = 'padding-left: $padding'>")
                    htmlString.append("</p>")
                    htmlString.append("</td>")
                }
                htmlString.append("<td>")
                if (mainTable[i][j].status == Status.EMPTY) {
                    htmlString.append("<p style = 'color:red'>")
                } else {
                    htmlString.append("<p>")
                }
                htmlString.append(mainTable[i][j].value)
                htmlString.append("</p>")
                htmlString.append("</td>")
            }
            htmlString.append("</tr>")
        }
        htmlString.append("</tbody>")
        htmlString.append("</table>")
        htmlString.append("</body>")
        htmlString.append("</html>")

        val bufferedWriter = BufferedWriter(FileWriter(filePath + fileName))
        bufferedWriter.write(htmlString.toString())
        bufferedWriter.flush()
        bufferedWriter.close()
    }

    fun getTextForSpeech():String {
        var textForSpeech = ""
        for (i in mainTable.indices) {
            for (j in mainTable[i].indices) {
                if (mainTable[i][j].status == Status.EMPTY) {
                    textForSpeech += mainTable[i][j].value
                    //textForSpeech += " "
                }
            }
        }
        return textForSpeech
    }

    fun printMainTableInConsole() {
        for (i in 0 until mainTable.size) {
            if (i % SIDE_OF_SQUARE_SIZE == 0) println()
            for(j in 0 until mainTable[i].size) {
                if (j % SIDE_OF_SQUARE_SIZE == 0) print(" ")
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
    companion object {
        fun writeMainTable(mainTable: ArrayList<ArrayList<Cell>>, tag: String) {
            val SIZE = mainTable.size
            val SIDE_OF_SQUARE_SIZE = sqrt(SIZE.toDouble()).toInt()
            val filePath = "bin\\"
            val fileName = "debug.html"

            val padding = "20px"
            val htmlString = StringBuilder()
            htmlString.append("<html>")
            htmlString.append("<body>")
            htmlString.append(tag)
            htmlString.append("<table style = 'font-size: 60px' border = '1'>")
            htmlString.append("<tbody>")
            for (i in mainTable.indices) {
                if (i % SIDE_OF_SQUARE_SIZE == 0) {
                    htmlString.append("<tr>")
                    htmlString.append("<td colspan = '${SIZE + SIDE_OF_SQUARE_SIZE}'>")
                    htmlString.append("<p style = 'padding-bottom: $padding'>")
                    htmlString.append("</p>")
                    htmlString.append("</td>")
                    htmlString.append("</tr>")
                }
                htmlString.append("<tr>")
                for (j in mainTable[i].indices) {
                    if (j % SIDE_OF_SQUARE_SIZE == 0) {
                        htmlString.append("<td>")
                        htmlString.append("<p style = 'padding-left: $padding'>")
                        htmlString.append("</p>")
                        htmlString.append("</td>")
                    }
                    htmlString.append("<td>")
                    if (mainTable[i][j].status == Status.EMPTY) {
                        htmlString.append("<p style = 'color:red'>")
                    } else {
                        htmlString.append("</p>")
                    }
                    htmlString.append(mainTable[i][j].value)
                    if (mainTable[i][j].options.isNotEmpty()) {
                        htmlString.append("</br>")
                    }
                    mainTable[i][j].options.forEach { option ->
                        htmlString.append(option)
                    }
                    htmlString.append("</p>")
                    htmlString.append("</td>")
                }
                htmlString.append("</tr>")
            }
            htmlString.append("</tbody>")
            htmlString.append("</table>")
            htmlString.append("</body>")
            htmlString.append("</html>")

            val fileWriter = FileWriter(filePath + fileName)
            val bufferedWriter = BufferedWriter(fileWriter)
            bufferedWriter.write(htmlString.toString())
            bufferedWriter.flush()
            bufferedWriter.close()
            fileWriter.close()
        }
    }
}