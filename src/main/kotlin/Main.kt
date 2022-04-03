import java.io.File
import java.io.FileReader
import java.io.StringWriter
import javax.script.ScriptEngineManager
import javax.script.SimpleScriptContext


fun main(args: Array<String>) {
//    val table = arrayOf(
//        intArrayOf(3, 0, 0, 0),
//        intArrayOf(0, 4, 0, 2),
//        intArrayOf(0, 0, 0, 4),
//        intArrayOf(0, 1, 2, 3)
//    )

//    val table = DetectSudokuTable.detect("S:\\programming\\sudoku_test_1.pdf")
//    val sudoky = Sudoky(table)
//    sudoky.resolve()
//    sudoky.printMainTableInConsole()


//    OpenCV.loadShared()
//    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

//
//    val tesseract = Tesseract()

//    tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");
//    val result = tesseract.doOCR(File("/home/dmitry/ksnip_20220326-133356.png"))
//
//    val result = tesseract.doOCR(File("S:\\programming\\sudoku_test.png"))
//    println(result)

    val writer = StringWriter()
    val context = SimpleScriptContext()
    context.setWriter(writer)
    val manager = ScriptEngineManager()
    val engine = manager.getEngineByName("python")
    engine.eval(FileReader("tabular_image-to-csv.py"), context)
}