from flask import Flask, render_template, request
import custom_ocr
import google_ocr
from werkzeug.utils import secure_filename
import os

UPLOAD_FOLDER = './static/uploads/'
ALLOWED_EXTENSIONS = set(['png', 'jpg', 'jpeg']) #check what opencv supports
app = Flask(__name__)

def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route('/')
def home_page():
    return render_template('index.html')

# route and function to handle the upload page
@app.route('/upload', methods=['GET', 'POST'])
def upload_page():
    if request.method == 'POST':
        # check if there is a file in the request
        if 'file' not in request.files:
            return render_template('upload.html', msg='No file selected')
        file = request.files['file']
        # if no file is selected
        if file.filename == '':
            return render_template('upload.html', msg='No file selected')

        if file and allowed_file(file.filename):
            filename = secure_filename(file.filename)
            #some random id for name?
            file.save(os.path.join(UPLOAD_FOLDER, filename))

            # call the OCR function on it
            print(filename)
            extracted_text_custom = custom_ocr.ocr(model, filename)
            extracted_text_vision_api=google_ocr.ocr(client, filename)

            # extract the text and display it
            return render_template('upload.html',
                                   msg='Successfully processed',
                                   extracted_text_custom=extracted_text_custom,
                                   extracted_text_vision_api=extracted_text_vision_api)
    elif request.method == 'GET':
        return render_template('upload.html')


if __name__ == '__main__':
    model=custom_ocr.init()
    client=google_ocr.init()
    app.run()