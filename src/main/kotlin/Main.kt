import java.awt.FileDialog
import javax.swing.JFrame


fun main(args: Array<String>) {
//    val table = arrayOf(
//        intArrayOf(3, 0, 0, 0),
//        intArrayOf(0, 4, 0, 2),
//        intArrayOf(0, 0, 0, 4),
//        intArrayOf(0, 1, 2, 3)
//    )


//    val fileImagePath = "S:\\programming\\sudokuImages\\test2.jpg"

    val fileSolutionHtmlPath = "bin\\"
    val fileSolutionHtmlName = "solution.html"

    var fileImagePath = ""
    val jFrameContainer = JFrame()
    val fileDialog = FileDialog(jFrameContainer)
    fileDialog.isVisible = true
    //val fileImagePath = "Этот компьютер\\POCO M3\\Внутренний общий накопитель\\DCIM\\Screenshots\\Screenshot_2022-04-02-15-45-56-727_easy.sudoku.puzzle.solver.free.jpg"
    val files = fileDialog.files
    if (files.isNotEmpty()) {
        fileImagePath = files[0].absolutePath
    }
    fileDialog.dispose()
    jFrameContainer.dispose()

    val table = DetectSudokuTable.detectFromImage(fileImagePath)
    val sudoky = Sudoky(table)
    sudoky.resolve()
    sudoky.writeSolutionTableToHtmlFile(fileSolutionHtmlPath, fileSolutionHtmlName)
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

//    val writer = StringWriter()
//    val context = SimpleScriptContext()
//    context.setWriter(writer)
//    val manager = ScriptEngineManager()
//    val engine = manager.getEngineByName("python")
//    val fr = FileReader("sudokuTableImageToCsv.py")
//    engine.eval(fr, context)
}