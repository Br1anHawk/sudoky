import net.sourceforge.tess4j.Tesseract
import nu.pattern.OpenCV
import org.apache.pdfbox.pdmodel.PDDocument
import org.opencv.core.Core
import technology.tabula.ObjectExtractor
import technology.tabula.Table
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm
import java.io.File

fun main(args: Array<String>) {
//    val table = arrayOf(
//        intArrayOf(3, 0, 0, 0),
//        intArrayOf(0, 4, 0, 2),
//        intArrayOf(0, 0, 0, 4),
//        intArrayOf(0, 1, 2, 3)
//    )

    val table = DetectSudokuTable.detect("S:\\programming\\sudoku_test_1.pdf")
    val sudoky = Sudoky(table)
    sudoky.resolve()
    sudoky.printMainTableInConsole()


//    OpenCV.loadShared()
//    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

//
//    val tesseract = Tesseract()

//    tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");
//    val result = tesseract.doOCR(File("/home/dmitry/ksnip_20220326-133356.png"))
//
//    val result = tesseract.doOCR(File("S:\\programming\\sudoku_test.png"))
//    println(result)
}