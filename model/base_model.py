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

files = tf.data.Dataset.list_files("/var/shared_disk/training/v6/direct/data-*-of-00016")

files = list(files.as_numpy_iterator())
training_files  = files[1:]
validation_files  = files[:1]

print(training_files)
print(validation_files)

decoded_training_dataset = tf.data.TFRecordDataset(training_files).map(decode_fn).batch(100000)
decoded_validation_dataset = tf.data.TFRecordDataset(validation_files).map(decode_fn).batch(10000)


model = keras.Sequential(
    [
        keras.Input(shape=(39), name= "input"),
        keras.layers.Dense(39, activation='relu'),
        keras.layers.Dense(312, activation='relu'),
        keras.layers.Dense(312, activation='relu'),
        keras.layers.Dense(3, name= "output", activation ='softmax')
    ]
)

model.summary()

model.compile(loss="mae", optimizer="adam",
              metrics=["mae", "mse"])

callback = keras.callbacks.EarlyStopping(monitor='loss', min_delta=0.0001, patience=3)

model.fit(decoded_training_dataset, validation_data=decoded_validation_dataset, callbacks=[callback], epochs=70)

print(model.predict([[0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0]]))

model.save("/home/anarbek/tmp/models/v5/direct/tf", save_format = "tf", overwrite = True)

