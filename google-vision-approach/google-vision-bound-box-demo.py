from google.cloud import vision
from PIL import Image, ImageDraw
#import cv2
from enum import Enum
import io
import os
import sys


class FeatureType(Enum):
    PAGE = 1
    BLOCK = 2
    PARA = 3
    WORD = 4
    SYMBOL = 5



def draw_boxes(image, bounds, color):
    """Draw a border around the image using the hints in the vector list."""
    draw = ImageDraw.Draw(image)

    for bound in bounds:
        draw.polygon([
            bound.vertices[0].x, bound.vertices[0].y,
            bound.vertices[1].x, bound.vertices[1].y,
            bound.vertices[2].x, bound.vertices[2].y,
            bound.vertices[3].x, bound.vertices[3].y], None, color)
    return image


def get_document_bounds(image_file, client, feature):
    """Returns document bounds given an image."""
    bounds = []

    with io.open(image_file, 'rb') as image_file:
        content = image_file.read()

    image = vision.types.Image(content=content)

    response = client.document_text_detection(image=image)
    document = response.full_text_annotation

    # Collect specified feature bounds by enumerating all document features
    for page in document.pages:
        for block in page.blocks:
            for paragraph in block.paragraphs:
                for word in paragraph.words:
                    for symbol in word.symbols:
                        if (feature == FeatureType.SYMBOL):
                            bounds.append(symbol.bounding_box)

                    if (feature == FeatureType.WORD):
                        bounds.append(word.bounding_box)

                if (feature == FeatureType.PARA):
                    bounds.append(paragraph.bounding_box)

            if (feature == FeatureType.BLOCK):
                bounds.append(block.bounding_box)

    # The list `bounds` contains the coordinates of the bounding boxes.
    return bounds


def render_doc_text(folder_to_load, filein, folder_to_save, client):
    full_file_path = os.path.join(folder_to_load, filein)
    image = Image.open(full_file_path).convert('LA')
    # bounds = get_document_bounds(full_file_path, client, FeatureType.BLOCK)
    # draw_boxes(image, bounds, 'blue')
    # bounds = get_document_bounds(full_file_path, client, FeatureType.PARA)
    # draw_boxes(image, bounds, 'red')
    bounds = get_document_bounds(full_file_path, client, FeatureType.WORD)
    draw_boxes(image, bounds, 'green')
    #color not importat as image is in grayscale

    fileout = filein.split(".")[0]+'-result.png'
    full_file_path = os.path.join(folder_to_save, fileout)
    image.save(full_file_path)
    #cv2.imwrite(full_file_path, image)


if __name__ == "__main__":
    if len(sys.argv) != 2:
        sys.stderr.write(
            "You must pass folder name that contains images as argument !")
        sys.exit(1)

    # if env var not set use:
    os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = r"text-recognition-273618-655720c6137b.json"

    # use EU endpoint
    client_options = {'api_endpoint': 'eu-vision.googleapis.com'}
    client = vision.ImageAnnotatorClient(client_options=client_options)

    path = os.path.dirname(os.path.abspath(__file__))
    folder = os.path.join(path, sys.argv[1])
    print("Detecting text in images that are located in:")
    print(folder)

    # get list of files
    files = []
    for (dirpath, dirnames, filenames) in os.walk(folder):
        files.extend(filenames)
    print(f"Detecting text for {len(files)} images")

    # save image with bounds
    folder_to_save = os.path.join(path, "google-result")
    for file in files:
        render_doc_text(folder, file, folder_to_save, client)

    print("")
    print(f"Result images save to: {folder_to_save}")
