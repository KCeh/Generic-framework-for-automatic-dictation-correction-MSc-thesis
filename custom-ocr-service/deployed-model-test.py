import googleapiclient.discovery
import numpy as np
import os
import cv2

class_names = ['A', 'B', 'C', 'D', 'E',
               'F', 'G', 'H', 'I', 'J',
               'K', 'L', 'M', 'N', 'O',
               'P', 'Q', 'R', 'S', 'T',
               'U', 'V', 'W', 'X', 'Y',
               'Z']


def predict_json(project, region, model, instances, version=None):
    """Send json data to a deployed model for prediction.

    Args:
        project (str): project where the Cloud ML Engine Model is deployed.
        model (str): model name.
        instances ([Mapping[str: Any]]): Keys should be the names of Tensors
            your deployed model expects as inputs. Values should be datatypes
            convertible to Tensors, or (potentially nested) lists of datatypes
            convertible to tensors.
        version: str, version of the model to target.
    Returns:
        Mapping[str: any]: dictionary of prediction results defined by the
            model.
    """
    # Create the ML Engine service object.
    # To authenticate set the environment variable
    # GOOGLE_APPLICATION_CREDENTIALS=<path_to_service_account_file>
    # needed???????
    service = googleapiclient.discovery.build('ml', 'v1')
    name = 'projects/{}/models/{}'.format(project, model)

    if version is not None:
        name += '/versions/{}'.format(version)

    response = service.projects().predict(
        name=name,
        body={'instances': instances}
    ).execute()

    if 'error' in response:
        raise RuntimeError(response['error'])

    return response['predictions']


if __name__ == "__main__":
    os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = r"MSc-thesis-test-web-app-4cb56bcc3c93.json"
    inputs_path = './characters'
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
    # print(letters)

    n = len(letters)
    inputs = []
    for letter in letters:
        filename = os.path.join(inputs_path, letter[3])
        x = cv2.imread(filename, cv2.IMREAD_GRAYSCALE)
        x = x.reshape(28, 28, 1)  # reshape for model
        x = x.astype('float32')
        x /= 255
        inputs.append(x)

    inputs = np.array(inputs)
    instances = inputs.tolist()
    respones = predict_json(project="msc-thesis-test-web-app",
                            region="europe-west1", model="predict_letters", instances=instances)
    

    result = []
    for out in respones:
        result.append(class_names[np.argmax(out['dense_1'])])
    print(result)
