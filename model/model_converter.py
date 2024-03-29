import tensorflow as tf
from tensorflow import keras

# Convert the model
model = keras.models.load_model('/var/shared_disk/models/v9/direct/tf')
model.summary()

converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

# Save the model.
with open('/var/shared_disk/models/v9/model.tflite', 'wb') as f:
  f.write(tflite_model)