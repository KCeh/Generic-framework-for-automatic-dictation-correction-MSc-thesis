from flask import Flask, request, Response
import os
import googleapiclient.discovery
import numpy as np
import json
import ocr
import cv2
import uuid
import shutil



app = Flask(__name__)


@app.route('/')
def index():
    return "Service is running!"


@app.route('/api/ocr', methods=['POST'])
def do_ocr():
    id = uuid.uuid4()
    path = "/tmp/"+str(id)+".png"

    nparr = np.fromstring(request.data, np.uint8) #frombuffer?
    img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    cv2.imwrite(path,img)
    data = {}
    text, coordinates = ocr.ocr(path)
    data["text"] = text
    data["coordinates"]=coordinates
    response = json.dumps(data)

    return Response(response=response, status=200, mimetype="application/json")


if __name__ == '__main__':
    ocr.init()
    app.run()
