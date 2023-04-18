import cv2
import numpy as np
import pytesseract
import csv
import sys

filePath = r'C:\Users\binif\IdeaProjects\sudoky\bin\temp\img.jpg'
tesseractCmdPath = r'C:\Program Files (x86)\Tesseract-OCR\tesseract.exe'
csvFilePath = r''
csvFileName = "csv.csv"
TABLE_WIDTH_SIZE = 9
TABLE_HEIGHT_SIZE = 9

EMPTY_CELL = 0


def isIn(rectExt, rectInt):
    if rectExt.x < rectInt.x \
            and rectExt.y < rectInt.y \
            and rectExt.x + rectExt.w > rectInt.x + rectInt.w \
            and rectExt.y + rectExt.h > rectInt.y + rectInt.h:
        return True
    return False


def checkTesseractDetectionErrors(numberStr):
    if numberStr == "2)\n" or numberStr == "<)\n" or numberStr == "{)\n":
        numberStr = "5"
    if numberStr == "74\n":
        numberStr = "4"
    if numberStr == "®\n" or numberStr == "©\n":
        numberStr = "9"
    if numberStr == "у\n":
        numberStr = "7"
    return numberStr


def getTable(m, n):
    table = []
    for i in range(m):
        row = []
        for j in range(n):
            row.append(EMPTY_CELL)
        table.append(row)
    return table


def traversalTreeOfRectangles(rectangle, table, imageForEdit):
    cells = rectangle.cells
    if len(cells) == 0:
        return
    m = len(table)
    n = len(table[0])
    if len(cells) == m * n:
        i = m - 1
        j = n - 1
        for cell in rectangle.cells:
            croppedCell = imageForEdit[cell.y:(cell.y + cell.h), cell.x:(cell.x + cell.w)]

            hsv = cv2.cvtColor(croppedCell, cv2.COLOR_BGR2HSV)
            msk = cv2.inRange(hsv, np.array([0, 0, 175]), np.array([179, 255, 255]))
            krn = cv2.getStructuringElement(cv2.MORPH_RECT, (5, 3))
            dlt = cv2.dilate(msk, krn, iterations=1)
            thr = 255 - cv2.bitwise_and(dlt, msk)

            custom_oem_psm_config = r' --psm 10 -l rus'
            #custom_oem_psm_config = 'digits'
            numberStr = pytesseract.image_to_string(thr, config=custom_oem_psm_config)
            print(numberStr)
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
                j = n - 1
                i -= 1
    for cell in cells:
        traversalTreeOfRectangles(cell, table, imageForEdit)


def writeTableInCsvFile(csvFilePath, csvFileName, table):
    csvFile = open(csvFilePath + csvFileName, 'w', newline='')
    writer = csv.writer(csvFile)
    writer.writerows(table)
    # for row in table:
    # writer.writerow(row)


class Rect:
    def __init__(self, x, y, w, h):
        self.x = x
        self.y = y
        self.w = w
        self.h = h
        self.cells = []


def sudokuTableImageToCsv(script, fileImagePath, tesseractCmdPath=tesseractCmdPath, csvFilePath=csvFilePath,
                          csvFileName=csvFileName):
    pytesseract.pytesseract.tesseract_cmd = tesseractCmdPath
    imageForEdit = cv2.imread(fileImagePath)
    image = cv2.imread(fileImagePath, 0)
    img = np.array(image)
    thresh, img_bin = cv2.threshold(img, 197, 255, cv2.THRESH_BINARY)
    img_bin = 255 - img_bin
    kernel_len = np.array(img).shape[1] // 50
    ver_kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (1, kernel_len))
    hor_kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (kernel_len, 1))
    kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (7, 7))
    image_1 = cv2.erode(img_bin, ver_kernel, iterations=3)
    vertical_lines = cv2.dilate(image_1, ver_kernel, iterations=3)
    image_2 = cv2.erode(img_bin, hor_kernel, iterations=3)
    horizontal_lines = cv2.dilate(image_2, hor_kernel, iterations=3)
    img_vh = cv2.addWeighted(vertical_lines, 0.5, horizontal_lines, 0.5, 0.0)
    img_vh = cv2.erode(~img_vh, kernel, iterations=2)
    _, img_vh = cv2.threshold(img_vh, 197, 255, cv2.THRESH_BINARY)
    dilated_value = cv2.dilate(img_vh, kernel, iterations=1)
    contours, hierarchy = cv2.findContours(dilated_value, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
    mainRectangle = Rect(0, 0, img.shape[1], img.shape[0])
    for cnt in contours:
        x, y, w, h = cv2.boundingRect(cnt)
        if abs(w - h) < 5:
            cv2.rectangle(imageForEdit, (x, y), (x + w, y + h), (0, 0, 255), 1)
            rectInt = Rect(x, y, w, h)
            if len(mainRectangle.cells) > 0:
                if isIn(mainRectangle.cells[len(mainRectangle.cells) - 1], rectInt):
                    mainRectangle.cells[len(mainRectangle.cells) - 1].cells.append(rectInt)
                else:
                    mainRectangle.cells.append(rectInt)
            else:
                mainRectangle.cells.append(rectInt)
    table = getTable(TABLE_WIDTH_SIZE, TABLE_HEIGHT_SIZE)
    traversalTreeOfRectangles(mainRectangle, table, imageForEdit)

    writeTableInCsvFile(csvFilePath, csvFileName, table)
    cv2.namedWindow('detecttable', cv2.WINDOW_NORMAL)
    cv2.imwrite(csvFilePath + "detecttable.jpg", imageForEdit)


if __name__ == '__main__':
    sudokuTableImageToCsv(r'sudokuTableImageToCsv.py', filePath)
    #sudokuTableImageToCsv(*sys.argv)
