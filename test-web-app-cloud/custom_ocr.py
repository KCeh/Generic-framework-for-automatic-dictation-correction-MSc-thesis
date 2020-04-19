import requests
import json
import os

UPLOAD_FOLDER='uploads'

#add url

content_type = 'image/jpeg'
headers = {'content-type': content_type}

def ocr(filename):
    file=os.path.join(UPLOAD_FOLDER, filename)
    img = open(file, 'rb').read()
    response = requests.post(url, data=img, headers=headers)
    if response.status_code == 500:
        output="Error in custom OCR engine, please try diffrent image"
    else:
        data = response.json()
        output=data['text']

    return output
