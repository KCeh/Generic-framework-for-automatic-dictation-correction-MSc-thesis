import numpy as np
import time
import os
import math
import sys
import tensorflow as tf
from tensorflow.keras import models
from scipy.misc import imsave, imread, imresize #in newer version of scipy deprecated!


class_names = ['A', 'B', 'C', 'D', 'E',
               'F', 'G', 'H', 'I', 'J', 
               'K', 'L', 'M', 'N', 'O', 
               'P', 'Q', 'R', 'S', 'T',
               'U', 'V', 'W', 'X', 'Y', 
               'Z']

def load_model():
    dir = os.path.dirname(__file__)
    file_path=os.path.join(dir, "my_model.h5")
    model=models.load_model(file_path)
    return model


def classify_letter(model, filename):
    x = imread(filename, mode='L') #L black and with
    #x = np.invert(x) #for ordinary images invert

    #x = imresize(x,(28,28))
    #splited_name=os.path.splitext(filename) 
    #imsave(splited_name[0]+'-inverted.png', x)

    x = x.reshape(1,28,28,1)#reshape for model
    x = x.astype('float32')

    x /= 255

    out = model.predict(x)
    return class_names[np.argmax(out)] #return name of letter for index with max prob

if __name__ == "__main__":
    path = os.path.dirname(os.path.abspath(__file__))
    dir=os.path.join(path, "izrezano")
    
    print("TF version:")
    print(tf.version.VERSION)#must be >= 2.0.0
    model=load_model()

    letters=[]
    for (dirpath, dirnames, filenames) in os.walk(dir):
        letters.extend(filenames)
    
    temp=[]
    for letter in letters:
        #remove filename extension
        #line, word, letter, full filename
        letter_list=os.path.splitext(letter)[0].split("_") 
        letter_list.append(letter)
        temp.append(letter_list)

    #sort by name (as int not string!)
    letters=sorted(temp, key=lambda l: (int(l[0]), int(l[1]), int(l[2])))

    i=0
    for letter in letters:
        i+=1
        out=classify_letter(model, os.path.join(dir,letter[3]))
        print(str(i)+": "+out)