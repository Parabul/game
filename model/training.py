import sys

import numpy as np
import tensorflow as tf
from tensorflow import keras


if (len(sys.argv) < 2):
	print("The version parameter is missing");
	sys.exit();

version = int(sys.argv[1]);
print(version);



def decode_fn(record_bytes):
  example = tf.io.parse_single_example(
      record_bytes,

      {"input": tf.io.FixedLenFeature([39], dtype=tf.float32),
       "output": tf.io.FixedLenFeature([3], dtype=tf.float32)}


  )
  return (example["input"], example["output"])

files_path = "/mnt/shared_disk/iter/v{version}/training/direct/data-*-of-00016".format(version=version)
print(files_path)

files = tf.data.Dataset.list_files(files_path)
files = list(files.as_numpy_iterator())


decoded_training_dataset = tf.data.TFRecordDataset(files).map(decode_fn).batch(100000)

model_path = "/mnt/shared_disk/iter/v{version}/model/direct/tf/".format(version=version)
print(model_path)
model = keras.models.load_model(model_path)

model.summary()

callback = keras.callbacks.EarlyStopping(monitor='loss', min_delta=0.0001, patience=3)

model.fit(decoded_training_dataset, callbacks=[callback], epochs=50)

print(model.predict([[0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.055555556,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0]]))

model.save("/mnt/shared_disk/iter/v{version}/model/direct/tf".format(version=version+1), save_format = "tf", overwrite = True)
model.save("/mnt/shared_disk/iter/current_model/direct/tf", save_format = "tf", overwrite = True)

