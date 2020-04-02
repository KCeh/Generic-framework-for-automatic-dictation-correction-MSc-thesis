import numpy as np
import cv2
import os

path = os.path.dirname(os.path.abspath(__file__))
dir_for_test_samples=dir_for_results=os.path.join(path, "test-slike")
dir_for_results=os.path.join(path, "contrast-normalization-results")
print(dir_for_results)

for (dirpath, dirnames, filenames) in os.walk(dir_for_test_samples):
        print(filenames)
        for filename in filenames:
            image_path=os.path.join(dir_for_test_samples, filename)
            img = cv2.imread(image_path,0)
            clahe = cv2.createCLAHE(tileGridSize=(8,8))
            cl1 = clahe.apply(img)
            splited_filename=os.path.splitext(filename)
            path_to_save=os.path.join(dir_for_results, splited_filename[0]+'-result.png')
            print(path_to_save)
            cv2.imwrite(path_to_save,cl1)
'''
img = cv2.imread('scan0058.jpg',0)

# create a CLAHE object (Arguments are optional).
clahe = cv2.createCLAHE(tileGridSize=(8,8))
cl1 = clahe.apply(img)

cv2.imwrite('scan0058-result.png',cl1)
'''