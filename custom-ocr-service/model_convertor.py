import os
import tensorflow as tf
from tensorflow.keras import backend, models


backend.set_learning_phase(0)
dir = os.path.dirname(__file__)
model_path=file_path = os.path.join(dir, "my_model.h5")
model=models.load_model(model_path)
model.save("saved_model")
