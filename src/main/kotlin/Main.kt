import net.sourceforge.tess4j.Tesseract
import nu.pattern.OpenCV
import org.opencv.core.Core
import java.io.File

fun main(args: Array<String>) {
    val table = arrayOf(
        intArrayOf(3, 0, 0, 0),
        intArrayOf(0, 4, 0, 2),
        intArrayOf(0, 0, 0, 4),
        intArrayOf(0, 1, 2, 3)
    )
    val sudoky = Sudoky(table)
    sudoky.resolve()
    sudoky.printMainTableInConsole()

    OpenCV.loadShared()
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

    val tesseract = Tesseract()
    tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");
    val result = tesseract.doOCR(File("/home/dmitry/ksnip_20220326-133356.png"))
    println(result)
}