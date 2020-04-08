from google.cloud import vision
import io
#if env var not set use:
#os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = r"ServiceAccountToken.json"

''' cloud configuration test
image_uri = 'gs://cloud-vision-codelab/otter_crossing.jpg'

client = vision.ImageAnnotatorClient()
image = vision.types.Image()
image.source.image_uri = image_uri

response = client.text_detection(image=image)

for text in response.text_annotations:
    print('=' * 79)
    print(f'"{text.description}"')
    vertices = [f'({v.x},{v.y})' for v in text.bounding_poly.vertices]
    print(f'bounds: {",".join(vertices)}')
'''

path="./test-pictures/scan0004.png"

client = vision.ImageAnnotatorClient()

with io.open(path, 'rb') as image_file:
    content = image_file.read()

image = vision.types.Image(content=content)

response = client.text_detection(image=image)
texts = response.text_annotations
print('Texts:')

for text in texts:
    print('\n"{}"'.format(text.description))

    vertices = (['({},{})'.format(vertex.x, vertex.y)
        for vertex in text.bounding_poly.vertices])
    print('bounds: {}'.format(','.join(vertices)))

    if response.error.message:
        raise Exception(
            '{}\nFor more info on error messages, check: '
            'https://cloud.google.com/apis/design/errors'.format(
                response.error.message))

#modify request(language hint):
#https://cloud.google.com/vision/docs/ocr#vision_text_detection-python