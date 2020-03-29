from cv2 import cv2
import numpy as np
import matplotlib
import matplotlib.pyplot as plt
import os
import sys

'''
Pre-Processing Techniques
We do Pre-Processing images to improve the chances of successful in recognition.
1. De-skew — When the document is not aligned properly when scanned, it may need to be titled a few degrees clockwise or counterclockwise.In order to take it perfectly horizontal or vertical.
2. Despeckle — remove positive and negative spots, smoothing edges
3. Binarisation — Convert an image from color or greyscale to black-and-white (called a “binary image” because there are two colors). The task of binarisation is performed as a simple way of separating the text (or any other desired image component) from the background.
4. Line removal — Cleans up non-glyph boxes and lines
5. Layout analysis or “zoning” — Identifies columns, paragraphs, captions, etc. as distinct blocks.
6. Line and word detection — Establishes baseline for word and character shapes, separates words if necessary.
7. Character isolation or “segmentation” — For per-character OCR, multiple characters that are connected due to image artifacts must be separated; single characters that are broken into multiple pieces due to artifacts must be connected.
8. Normalize aspect ratio and scale.
'''

#some things to do in preprocessing
#Good Quality Original Source
#Binarize Image
#Scaling To The Right Size
#Increase Contrast
#Remove Noise and Scanning Artefacts
#Deskew
#Layout Analysis (or Zone Analysis)

if __name__ == "__main__":
    if len(sys.argv) != 2:
        sys.stderr.write(
            "You must pass image name as argument !")
        sys.exit(1)
    path = os.path.dirname(os.path.abspath(__file__))
    file=os.path.join(path, sys.argv[1])
    print(file)

    matplotlib.interactive(True)
    im = cv2.imread(file)
    grayimg = cv2.cvtColor(im, cv2.COLOR_BGR2HSV)
    plt.figure(figsize=(8, 8))
    plt.imshow(grayimg, cmap='gray')
    input('press return to continue')

    contrast_fixed = cv2.convertScaleAbs(grayimg, alpha=2.5, beta=0)
    plt.figure(figsize=(8, 8))
    plt.imshow(contrast_fixed)
    input('press return to continue')
    
    _, binary_thresh = cv2.threshold(contrast_fixed, 200, 255, cv2.THRESH_BINARY_INV)
    fig = plt.figure(figsize=(8, 8))
    fig.add_subplot(1, 2, 1)
    plt.imshow(contrast_fixed, cmap='gray')
    fig.add_subplot(1, 2, 2)
    plt.imshow(binary_thresh, cmap='gray')
    input('press return to continue')

    lines = cv2.HoughLinesP(binary_thresh, 1, np.pi/180, 100, 
                            minLineLength= 600/2.0, maxLineGap=20)
    angle = 0
    for line in lines:
        x1, y1, x2, y2 = line[0]
        r = np.arctan2(y2 - y1, x2 -x1)
        angle += np.arctan2(y2 - y1, x2 - x1)

    avg_radian = angle / len(lines)
    avg_angle = avg_radian * 180 / np.pi
    print("Average angle is %f degrees" % avg_angle)
    