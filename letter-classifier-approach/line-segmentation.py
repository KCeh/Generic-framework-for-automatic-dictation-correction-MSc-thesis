import cv2
import numpy as np

#based on
#https://stackoverflow.com/questions/53962171/perform-line-segmentation-cropping-serially-with-opencv


def ResizeWithAspectRatio(image, width=None, height=None, inter=cv2.INTER_AREA):
    dim = None
    (h, w) = image.shape[:2]

    if width is None and height is None:
        return image
    if width is None:
        r = height / float(h)
        dim = (int(w * r), height)
    else:
        r = width / float(w)
        dim = (width, int(h * r))

    return cv2.resize(image, dim, interpolation=inter)

image= cv2.imread('./test-pictures/scan0002.png')
(original_h, original_w) = image.shape[:2]
gray = cv2.cvtColor(image,cv2.COLOR_BGR2GRAY)
#gray=ResizeWithAspectRatio(gray, width=1000)

ret,thresh = cv2.threshold(gray,127,255,cv2.THRESH_BINARY_INV)

cv2.imshow('binary',thresh)
cv2.waitKey(0)

#dilation
kernel = np.ones((5,100), np.uint8)
img_dilation = cv2.dilate(thresh, kernel, iterations=1)
cv2.imshow('dilated',img_dilation)
cv2.waitKey(0)

#find contours
#im2,ctrs, hier = cv2.findContours(img_dilation.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
ctrs, hier = cv2.findContours(img_dilation.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

#sort contours
#to improve
sorted_ctrs = sorted(ctrs, key=lambda ctr: cv2.boundingRect(ctr)[0])

for i, ctr in enumerate(sorted_ctrs):
    # Get bounding box
    x, y, w, h = cv2.boundingRect(ctr)

    #reject small contours
    if w/float(original_w)<0.1:
        continue
    elif h/float(original_h)<0.01:
        continue

    # Getting ROI
    roi = image[y:y+h, x:x+w]

    # show ROI
    cv2.imshow('segment no:'+str(i),roi)
    cv2.imwrite("./line-segmentation-results/segment_no_"+str(i)+".png",roi)
    #mark on original
    cv2.rectangle(image,(x,y),( x + w, y + h ),(90,0,255),2)
    cv2.waitKey(0)

cv2.imwrite('./line-segmentation-results/final_result.png',image)
cv2.imshow('marked areas',image)
cv2.waitKey(0)