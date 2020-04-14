from google.cloud import vision
import io
import os


UPLOAD_FOLDER='uploads'

def init():
    #if env var not set use:
    os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = r"text-recognition-273618-655720c6137b.json"
    client_options = {'api_endpoint': 'eu-vision.googleapis.com'} #use EU endpoint
    client = vision.ImageAnnotatorClient(client_options=client_options)
    return client

def ocr(client, filename):
    file=os.path.join(UPLOAD_FOLDER, filename)
    with io.open(file, 'rb') as image_file:
        content = image_file.read()

    image = vision.types.Image(content=content)

    response = client.text_detection(image=image)
    texts = response.text_annotations
    print('Texts:')

    output=texts[0].description
    return output
