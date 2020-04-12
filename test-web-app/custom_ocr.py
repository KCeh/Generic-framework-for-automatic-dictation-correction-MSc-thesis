import numpy as np
import cv2
import os
import sys
import shutil
import tensorflow as tf
from tensorflow.keras import models
# in newer version of scipy deprecated! use opencv or something for that
from scipy.misc import imsave, imread, imresize

UPLOAD_FOLDER='./static/uploads/'
ERROR_TEXT="Unable to process.\nPlease try with another image."

width=0
height=0

class_names = ['A', 'B', 'C', 'D', 'E',
               'F', 'G', 'H', 'I', 'J',
               'K', 'L', 'M', 'N', 'O',
               'P', 'Q', 'R', 'S', 'T',
               'U', 'V', 'W', 'X', 'Y',
               'Z']


def load_model():
    dir = os.path.dirname(__file__)
    file_path = os.path.join(dir, "my_model.h5")
    model = models.load_model(file_path)
    return model


def classify_letter(model, filename):
    x = imread(filename, mode='L')  # L black and with

    x = x.reshape(1, 28, 28, 1)  # reshape for model
    x = x.astype('float32')

    x /= 255

    out = model.predict(x)
    # return name of letter for index with max prob
    return class_names[np.argmax(out)]


def line_array(array):
    list_x_upper = []
    list_x_lower = []
    for y in range(5, len(array)-5):
        s_a, s_p = strtline(y, array)
        e_a, e_p = endline(y, array)
        if s_a >= 7 and s_p >= 5:
            list_x_upper.append(y)
            # bin_img[y][:] = 255
        if e_a >= 5 and e_p >= 7:
            list_x_lower.append(y)
            # bin_img[y][:] = 255
    return list_x_upper, list_x_lower


def strtline(y, array):
    count_ahead = 0
    count_prev = 0
    for i in array[y:y+10]:
        if i > 3:
            count_ahead += 1
    for i in array[y-10:y]:
        if i == 0:
            count_prev += 1
    return count_ahead, count_prev


def endline(y, array):
    count_ahead = 0
    count_prev = 0
    for i in array[y:y+10]:
        if i == 0:
            count_ahead += 1
    for i in array[y-10:y]:
        if i > 3:
            count_prev += 1
    return count_ahead, count_prev


def endline_word(y, array, a):
    count_ahead = 0
    count_prev = 0
    for i in array[y:y+2*a]:
        if i < 2:
            count_ahead += 1
    for i in array[y-a:y]:
        if i > 2:
            count_prev += 1
    return count_prev, count_ahead


def end_line_array(array, a):
    list_endlines = []
    for y in range(len(array)):
        e_p, e_a = endline_word(y, array, a)
        # print(e_p, e_a)
        if e_a >= int(1.5*a) and e_p >= int(0.7*a):
            list_endlines.append(y)
    return list_endlines


def refine_endword(array):
    refine_list = []
    for y in range(len(array)-1):
        if array[y]+1 < array[y+1]:
            refine_list.append(array[y])
    # modify
    if len(array) > 0:
        refine_list.append(array[-1])
    return refine_list


def refine_array(array_upper, array_lower):
    upperlines = []
    lowerlines = []
    for y in range(len(array_upper)-1):
        if array_upper[y] + 5 < array_upper[y+1]:
            upperlines.append(array_upper[y]-10)
    for y in range(len(array_lower)-1):
        if array_lower[y] + 5 < array_lower[y+1]:
            lowerlines.append(array_lower[y]+10)

    upperlines.append(array_upper[-1]-10)
    lowerlines.append(array_lower[-1]+10)

    return upperlines, lowerlines


def letter_width(contours):
    letter_width_sum = 0
    count = 0
    for cnt in contours:
        if cv2.contourArea(cnt) > 20:
            x, y, w, h = cv2.boundingRect(cnt)
            letter_width_sum += w
            count += 1

    return letter_width_sum/count


def end_wrd_dtct(lines, i, bin_img, mean_lttr_width):
    count_y = np.zeros(shape=width)
    for x in range(width):
        for y in range(lines[i][0], lines[i][1]):
            if bin_img[y][x] == 255:
                count_y[x] += 1
    end_lines = end_line_array(count_y, int(mean_lttr_width))
    # print(end_lines)
    endlines = refine_endword(end_lines)
    for x in endlines:
        final_thr[lines[i][0]:lines[i][1], x] = 255
    return endlines


def letter_seg(lines_img, x_lines, i):
    copy_img = lines_img[i].copy()
    x_linescopy = x_lines[i].copy()

    letter_img = []
    letter_k = []

    #chalu_img, contours, hierarchy = cv2.findContours(copy_img,cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_SIMPLE)
    contours, hierarchy = cv2.findContours(
        copy_img, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    for cnt in contours:
        if cv2.contourArea(cnt) > 80:  # modify size
            x, y, w, h = cv2.boundingRect(cnt)
            # letter_img.append(lines_img[i][y:y+h, x:x+w])
            letter_k.append((x, y, w, h))

    letter = sorted(letter_k, key=lambda student: student[0])
    # print(letter)

    word = 1
    letter_index = 0
    for e in range(len(letter)):
        if(letter[e][0] < x_linescopy[0]):
            letter_index += 1
            letter_img_tmp = lines_img[i][letter[e][1]-5:letter[e][1] +
                                          letter[e][3]+5, letter[e][0]-5:letter[e][0]+letter[e][2]+5]
            letter_img = cv2.resize(letter_img_tmp, dsize=(
                28, 28), interpolation=cv2.INTER_AREA)
            cv2.imwrite('./characters/'+str(i+1)+'_'+str(word)+'_' +
                        str(letter_index)+'.png', letter_img)  # 255-letter_img for inverting
        else:
            x_linescopy.pop(0)
            word += 1
            letter_index = 1
            letter_img_tmp = lines_img[i][letter[e][1]-5:letter[e][1] +
                                          letter[e][3]+5, letter[e][0]-5:letter[e][0]+letter[e][2]+5]
            letter_img = cv2.resize(letter_img_tmp, dsize=(
                28, 28), interpolation=cv2.INTER_AREA)
            cv2.imwrite('./characters/'+str(i+1)+'_'+str(word) +
                        '_'+str(letter_index)+'.png', letter_img)
            # print(letter[e][0],x_linescopy[0], word)

def init():
    print("TF version:")
    print(tf.version.VERSION)  # must be >= 2.0.0
    model = load_model()
    return model


def ocr(model, filename):
    file=os.path.join(UPLOAD_FOLDER, filename)
    path = os.path.dirname(os.path.abspath(__file__))
    dir = os.path.join(path, "characters")

    try:
        shutil.rmtree(dir)
    except FileNotFoundError:
        print("Directory %s doesn't exist! Creating..." % dir)

    try:
        os.mkdir(dir)
    except OSError:
    	print("Creation of the directory %s failed" % dir)

    src_img = cv2.imread(file, 1)
    copy = src_img.copy()
    height = src_img.shape[0]
    width = src_img.shape[1]

    src_img = cv2.resize(copy, dsize=(1320, int(1320*height/width)), interpolation=cv2.INTER_AREA)

    height = src_img.shape[0]
    width = src_img.shape[1]

    grey_img = cv2.cvtColor(src_img, cv2.COLOR_BGR2GRAY)

    bin_img = cv2.adaptiveThreshold(grey_img, 255, cv2.ADAPTIVE_THRESH_MEAN_C, cv2.THRESH_BINARY_INV,21,20)
    bin_img1 = bin_img.copy()
    bin_img2 = bin_img.copy()

    kernel = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (3, 3))
    kernel1 = np.array([[1, 0, 1], [0,1,0],[1,0,1]], dtype = np.uint8)
    final_thr = cv2.morphologyEx(bin_img, cv2.MORPH_CLOSE, kernel)
    contr_retrival = final_thr.copy()

    #-------------/Thresholding Image-------------#

    #-------------Line Detection------------------#
    count_x = np.zeros(shape=(height))
    for y in range(height):
        for x in range(width):
            if bin_img[y][x] == 255:
                count_x[y] = count_x[y]+1

    upper_lines, lower_lines = line_array(count_x)

    upperlines, lowerlines = refine_array(upper_lines, lower_lines)

    if len(upperlines) == len(lowerlines):
        lines = []
        for y in upperlines:
            final_thr[y][:] = 255
        for y in lowerlines:
            final_thr[y][:] = 255
        for y in range(len(upperlines)):
            lines.append((upperlines[y], lowerlines[y]))

    else:
        return ERROR_TEXT

    lines = np.array(lines)

    no_of_lines = len(lines)

    lines_img = []

    for i in range(no_of_lines):
        lines_img.append(bin_img2[lines[i][0]:lines[i][1], :])

    contours, hierarchy = cv2.findContours(
        contr_retrival, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    final_contr = np.zeros((final_thr.shape[0], final_thr.shape[1], 3), dtype= np.uint8)
    cv2.drawContours(src_img, contours, -1, (0, 255, 0), 1)

    mean_lttr_width = letter_width(contours)

    x_lines = []

    for i in range(len(lines_img)):
        x_lines.append(end_wrd_dtct(lines, i, bin_img, mean_lttr_width))

    for i in range(len(x_lines)):
        x_lines[i].append(width)

    print(x_lines)

    for i in range(len(lines)):
        letter_seg(lines_img, x_lines, i)

    letters = []
    for (dirpath, dirnames, filenames) in os.walk(dir):
        letters.extend(filenames)

    temp = []
    for letter in letters:
        # remove filename extension
        # line, word, letter, full filename
        letter_list = os.path.splitext(letter)[0].split("_")
        letter_list.append(letter)
        temp.append(letter_list)

    # sort by name (as int not string!)
    letters = sorted(temp, key=lambda l: (int(l[0]), int(l[1]), int(l[2])))
    # print(letters)

    word_index = 1
    line_index = 1
    output = ""
    for letter in letters:
        if word_index != int(letter[1]):
            word_index += 1
            output += " "
        if line_index < int(letter[0]):
            word_index = 1
            line_index += 1
            output += " "
        output += classify_letter(model, os.path.join(dir, letter[3]))
    return output
