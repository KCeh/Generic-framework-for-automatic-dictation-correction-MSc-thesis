import os
import cv2
import numpy as np
from tensorflow import keras


class_names = ['A', 'B', 'C', 'D', 'E',
               'F', 'G', 'H', 'I', 'J',
               'K', 'L', 'M', 'N', 'O',
               'P', 'Q', 'R', 'S', 'T',
               'U', 'V', 'W', 'X', 'Y',
               'Z']

dir = os.path.dirname(__file__)
model_path = os.path.join(dir, "my_model.h5")
model = keras.models.load_model(model_path)

inputs_path='./characters'
letters = []
for (dirpath, dirnames, filenames) in os.walk(inputs_path):
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
#print(letters)

n=len(letters)
inputs=[]
for letter in letters:
    filename=os.path.join(inputs_path, letter[3])
    x = cv2.imread(filename,cv2.IMREAD_GRAYSCALE)
    x = x.reshape(28, 28, 1)  # reshape for model
    x = x.astype('float32')
    x /= 255
    inputs.append(x)

inputs=np.array(inputs)
#print(n)
#print(np.shape(inputs))

#classify
results=[]
outputs = model.predict(inputs)
for out in outputs:
    results.append(class_names[np.argmax(out)])
print(results)