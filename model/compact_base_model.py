import numpy as np
import tensorflow as tf
from tensorflow import keras

def decode_fn(record_bytes):
  example = tf.io.parse_single_example(
      record_bytes,

      {"input": tf.io.FixedLenFeature([25], dtype=tf.float32),
       "output": tf.io.FixedLenFeature([3], dtype=tf.float32)}


  )
  input = example["input"]
  input = tf.where(tf.math.is_nan(input), tf.zeros_like(input), input)

  return (input, example["output"])

files = tf.data.Dataset.list_files("/var/shared_disk/compact/training/direct/data-*-of-00010")

files = list(files.as_numpy_iterator())


decoded_training_dataset = tf.data.TFRecordDataset(files).map(decode_fn).batch(100000)

#print(decoded_training_dataset.take(1).get_single_element())

model = keras.Sequential(
    [
        keras.Input(shape=(25), name= "input"),
        keras.layers.Dense(4, activation='relu'),
        keras.layers.Dense(4, activation='relu'),
        keras.layers.Dense(3, name= "output", activation ='softmax')
    ]
)

model.summary()
loss_fn = keras.losses.MeanSquaredError(
              reduction="sum_over_batch_size", name="mean_squared_error"
          )
cce = keras.losses.CategoricalCrossentropy()
model.compile(loss=cce, optimizer="adam",
              metrics=["mean_absolute_error", "mean_squared_error"])

callback = keras.callbacks.EarlyStopping(monitor='loss', min_delta=0.001, patience=3)

model.fit(decoded_training_dataset, callbacks=[callback], epochs=70)

print(model.predict([[0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.5,0.5,0.5555556,0.5555556]]))

model.save("/home/anarbek/tmp/models/compact_test/direct/tf", save_format = "tf", overwrite = True)

