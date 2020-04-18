import requests
import json

adr = 'http://localhost:5000'
url = adr + '/api/ocr'

content_type = 'image/jpeg'
headers = {'content-type': content_type}
img_file="scan0005.png"

img = open(img_file, 'rb').read()
response = requests.post(url, data=img, headers=headers)
data = response.json()
print(data)