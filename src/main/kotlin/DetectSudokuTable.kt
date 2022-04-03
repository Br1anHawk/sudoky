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
            val array = ArrayList<IntArray>()
            for (i in 1..size) {
                array.add(IntArray(size))
            }
            return array.toTypedArray()
        }

        fun detectFromImage(fileImagePath: String): Array<IntArray> {
            val tesseractCmdPath = "C:\\Program Files (x86)\\Tesseract-OCR\\tesseract.exe"
            val csvFilePath = ""
            val csvFileName = "csv.csv"
            val array = initArray(9)
            val process = ProcessBuilder()
            val pyScriptPath = File(".").canonicalPath + "\\" + "sudokuTableImageToCsv.py "
            process.command("python " , pyScriptPath, fileImagePath, tesseractCmdPath, csvFilePath, csvFileName)
            val runningProcess = process.start()
            val exitCode = runningProcess.waitFor()
            csvReader().open(csvFilePath + csvFileName) {
                var i = 0
                var j = 0
                readAllAsSequence().forEach { row ->
                    row.forEach { cell ->
                        array[i][j++] = cell.toInt()
                    }
                    i++
                    j = 0
                }
            }
            return array
        }
    }
}