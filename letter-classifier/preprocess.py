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
    image = cv2.imread(file, cv2.IMREAD_GRAYSCALE)
    plt.figure(figsize=(8, 8))
    plt.imshow(image, cmap='gray')
    input('press return to continue')
    #cv2.imshow("Gray", grayimg)
    #cv2.waitKey(0)

    '''
    image_enhanced = cv2.equalizeHist(image)
    plt.imshow(image_enhanced, cmap='gray')
    input('press return to continue')
    contrast_fixed = cv2.convertScaleAbs(image, alpha=2.5, beta=50)
    plt.figure(figsize=(8, 8))
    plt.imshow(contrast_fixed, cmap='gray')
    input('press return to continue')
    #cv2.imshow("Contrast", contrast_fixed)
    #cv2.waitKey(0)
    '''
    
    #_, th = cv2.threshold(image, 200, 255, cv2.THRESH_BINARY_INV)
    th=cv2.adaptiveThreshold(image,255,cv2.ADAPTIVE_THRESH_GAUSSIAN_C,cv2.THRESH_BINARY,11,2)
    cv2.imwrite(file+"-result.png", th)
    fig = plt.figure(figsize=(8, 8))
    fig.add_subplot(1, 2, 1)
    plt.imshow(image, cmap='gray')
    fig.add_subplot(1, 2, 2)
    plt.imshow(th, cmap='gray')
    input('press return to continue')
    #cv2.imshow("Binarization", binary_thresh)
    #cv2.waitKey(0)

    lines = cv2.HoughLinesP(th, 1, np.pi/180, 100, 
                            minLineLength= 600/2.0, maxLineGap=20)
    angle = 0
    for line in lines:
        x1, y1, x2, y2 = line[0]
        r = np.arctan2(y2 - y1, x2 -x1)
        angle += np.arctan2(y2 - y1, x2 - x1)

    avg_radian = angle / len(lines)
    avg_angle = avg_radian * 180 / np.pi
    print("Average angle is %f degrees" % avg_angle)
    