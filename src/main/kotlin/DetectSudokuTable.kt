import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.apache.pdfbox.pdmodel.PDDocument
import technology.tabula.ObjectExtractor
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm
import java.io.File

class DetectSudokuTable {
    companion object {
        fun detect(filePath: String): Array<IntArray> {
            val pd = PDDocument.load(File(filePath))
            val oe = ObjectExtractor(pd)
            val sea = SpreadsheetExtractionAlgorithm()
            val tables = sea.extract(oe.extract(pd.numberOfPages))
            var array = arrayOf(intArrayOf())
            for (t in tables) {
                val rows = t.rows
                array = initArray(rows.size)
                for (row in rows.indices) {
                    for (cell in rows[row].indices) {
                        print(rows[row][cell].getText() + " | ")
                        array[row][cell] = rows[row][cell].getText().toIntOrNull() ?: 0
                    }
                    println()
                }
                println("——————————————")
            }
            return array
        }

        private fun initArray(size: Int): Array<IntArray> {
            val array = ArrayList<IntArray>(size)
            for (i in 1..size) {
                array.add(IntArray(size))
            }
            return array.toTypedArray()
        }

        fun detectFromImage(fileImagePath: String): Array<IntArray> {
            val array = arrayOf(intArrayOf())
            val process = ProcessBuilder()
            val pyScriptPath = File(".").canonicalPath + "\\" + "sudokuTableImageToCsv.py "
            process.command("python " , pyScriptPath, fileImagePath)
            val runningProcess = process.start()
            val exitCode = runningProcess.waitFor()
            csvReader().open(fileImagePath) {
                readAllAsSequence().forEach { row ->
                    row.forEach { cell ->
                        print(cell)
                    }
                }
            }
            return array
        }
    }
}