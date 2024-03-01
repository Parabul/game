import numpy as np
import tensorflow as tf
from tensorflow import keras

def decode_fn(record_bytes):
  example = tf.io.parse_single_example(
      record_bytes,

      {"input": tf.io.FixedLenFeature([39], dtype=tf.float32),
       "output": tf.io.FixedLenFeature([3], dtype=tf.float32)}


  )
  return (example["input"], example["output"])

files = tf.data.Dataset.list_files("/var/shared_disk/training/v8/direct/data-*-of-00010")
files = list(files.as_numpy_iterator())


decoded_training_dataset = tf.data.TFRecordDataset(files).map(decode_fn).batch(10000)


model = loaded_model = keras.models.load_model('/var/shared_disk/models/v5/')

model.summary()

callback = keras.callbacks.EarlyStopping(monitor='loss', min_delta=0.0001, patience=3)

model.fit(decoded_training_dataset, callbacks=[callback], epochs=100)

print(model.predict([[0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0]]))

model.save("/var/shared_disk/models/v8/direct/tf", save_format = "tf", overwrite = True)

