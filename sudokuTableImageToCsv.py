import cv2
import numpy as np
import pytesseract
import csv


filePath = r'st.jpg'
tesseractCmdPath = r'C:\Program Files (x86)\Tesseract-OCR\tesseract.exe'
csvFilePath = r''
csvFileName = "csv"

EMPTY_CELL = 0


def isIn(rectExt, rectInt):
    if rectExt.x < rectInt.x \
            and rectExt.y < rectInt.y \
            and rectExt.x + rectExt.w > rectInt.x + rectInt.w \
            and rectExt.y + rectExt.h > rectInt.y + rectInt.h:
        return True
    return False


def checkTesseractDetectionErrors(numberStr):
    if numberStr == "$)\n":
        numberStr = "5"
    return numberStr


def getTable(m, n):
    table = []
    for i in range(m):
        row = []
        for j in range(n):
            row.append(EMPTY_CELL)
        table.append(row)
    return table


def writeTableInCsvFile(csvFilePath, csvFileName, table):
    csvFile = open(csvFilePath + csvFileName, 'w')
    writer = csv.writer(csvFile)
    writer.writerow(table)


class Rect:
    def __init__(self, x, y, w, h):
        self.x = x
        self.y = y
        self.w = w
        self.h = h
        self.cells = []

def sudokuTableImageToCsv(filePath, tesseractCmdPath):

    pytesseract.pytesseract.tesseract_cmd = tesseractCmdPath
    imageForEdit = cv2.imread(filePath)
    image = cv2.imread(filePath, 0)
    img = np.array(image)
    thresh, img_bin = cv2.threshold(img, 128, 255, cv2.THRESH_BINARY | cv2.THRESH_OTSU)
    img_bin = 255 - img_bin
    kernel_len = np.array(img).shape[1] // 100
    ver_kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (1, kernel_len))
    hor_kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (kernel_len, 1))
    kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (7, 7))
    image_1 = cv2.erode(img_bin, ver_kernel, iterations=3)
    vertical_lines = cv2.dilate(image_1, ver_kernel, iterations=3)
    image_2 = cv2.erode(img_bin, hor_kernel, iterations=3)
    horizontal_lines = cv2.dilate(image_2, hor_kernel, iterations=3)
    img_vh = cv2.addWeighted(vertical_lines, 0.5, horizontal_lines, 0.5, 0.0)
    img_vh = cv2.erode(~img_vh, kernel, iterations=2)
    _, img_vh = cv2.threshold(img_vh, 128, 255, cv2.THRESH_BINARY | cv2.THRESH_OTSU)
    dilated_value = cv2.dilate(img_vh, kernel, iterations=1)
    contours, hierarchy = cv2.findContours(dilated_value, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
    rectangles = []
    for cnt in contours:
        x, y, w, h = cv2.boundingRect(cnt)
        if abs(w - h) < 1:
            # cv2.rectangle(im, (x, y), (x + w, y + h), (0, 0, 255), 1)
            rectInt = Rect(x, y, w, h)
            if len(rectangles) > 0:
                if isIn(rectangles[len(rectangles) - 1], rectInt):
                    rectangles[len(rectangles) - 1].cells.append(rectInt)
                else:
                    rectangles.append(rectInt)
            else:
                rectangles.append(rectInt)
    tableSize = 9
    table = getTable(tableSize, tableSize)
    for rectangle in rectangles:
        if len(rectangle.cells) == tableSize * tableSize:
            i = tableSize - 1
            j = tableSize - 1
            for cell in rectangle.cells:
                croppedCell = imageForEdit[cell.y:(cell.y + cell.h), cell.x:(cell.x + cell.w)]

                hsv = cv2.cvtColor(croppedCell, cv2.COLOR_BGR2HSV)
                msk = cv2.inRange(hsv, np.array([0, 0, 175]), np.array([179, 255, 255]))
                krn = cv2.getStructuringElement(cv2.MORPH_RECT, (5, 3))
                dlt = cv2.dilate(msk, krn, iterations=1)
                thr = 255 - cv2.bitwise_and(dlt, msk)

                custom_oem_psm_config = r' --psm 10 -l rus'
                numberStr = pytesseract.image_to_string(thr, config=custom_oem_psm_config)
                numberStr = checkTesseractDetectionErrors(numberStr)
                try:
                    number = int(numberStr)
                    if number > 9:
                        number = EMPTY_CELL
                except ValueError as valErr:
                    number = EMPTY_CELL
                # print(number)
                table[i][j] = number
                j -= 1
                if j == -1:
                    j = tableSize - 1
                    i -= 1
    writeTableInCsvFile(csvFilePath, csvFileName, table)
    # cv2.namedWindow('detecttable', cv2.WINDOW_NORMAL)
    # cv2.imwrite('detecttable.jpg', imageForEdit)


if __name__ == '__main__':
    sudokuTableImageToCsv(filePath, tesseractCmdPath)
