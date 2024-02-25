import tensorflow as tf
from tensorflow import keras


def decode_fn(record_bytes):
    example = tf.io.parse_single_example(  # Data
        record_bytes,

        # Schema
        {
            "board": tf.io.FixedLenFeature([18], dtype=tf.float32),
            "score": tf.io.FixedLenFeature([2], dtype=tf.float32),
            "special": tf.io.FixedLenFeature([2], dtype=tf.int64),
            "next": tf.io.FixedLenFeature([1], dtype=tf.int64),
            "output": tf.io.FixedLenFeature([3], dtype=tf.float32)
        }

    )

    return ({
                'score': example["score"],
                'special': example["special"],
                'board': example["board"],
                # 'board': tf.divide(example["board"], tf.math.reduce_sum(example["board"], axis = None)),
                'next': example["next"]
            },
            example["output"])


files = tf.data.Dataset.list_files("/var/shared_disk/training/v5/experimental/data-*-of-00016")

decoded_dataset = tf.data.TFRecordDataset(list(files.as_numpy_iterator())).map(decode_fn).batch(100000)

# Inputs
score = keras.layers.Input(shape=(2,), name='score')
special = keras.layers.Input(shape=(2,), name='special')
board = keras.layers.Input(shape=(18,), name='board')
next_move = keras.layers.Input(shape=(1,), name='next')

# Score sub-model
model_score = keras.layers.Dense(2, activation="relu")(score)
model_score = keras.Model(name='model_score', inputs=score, outputs=model_score)

# Special sub-model
model_special = keras.layers.CategoryEncoding(name="multi_hot", num_tokens=18, output_mode="multi_hot")(special)
model_special = keras.Model(name='model_special', inputs=special, outputs=model_special)

# Board sub-model
# model_board = keras.layers.Reshape((2, 9, 1))(board)
# model_board = keras.layers.Conv2D(16, (2, 3), padding='same', activation='relu')(model_board)
# model_board = keras.layers.Conv2D(32, (2, 3), padding='same', activation='relu')(model_board)
# model_board = keras.layers.Flatten()(model_board)
model_board = keras.layers.Dense(20, activation="relu")(board)
model_board = keras.Model(name='model_board', inputs=board, outputs=model_board)

# Next sub-model
model_next = keras.layers.Dense(1, activation="relu")(next_move)
model_next = keras.Model(name='model_next', inputs=next_move, outputs=model_next)

# combine the output of the two branches
ensemble = keras.layers.concatenate([model_score.output, model_special.output, model_board.output, model_next.output])
# apply a FC layer and then a regression prediction on the
# combined outputs
model_ensemble = keras.layers.Dense(320, activation="relu")(ensemble)
# model_ensemble = keras.layers.Dense(320, activation="relu")(model_ensemble)
# model_ensemble = keras.layers.Dropout(0.2)(model_ensemble)
# model_ensemble = keras.layers.Dense(320, activation="relu")(model_ensemble)
model_ensemble = keras.layers.Dense(3, name="output", activation='softmax')(model_ensemble)
# our model will accept the inputs of the two branches and
# then output a single value
model = keras.Model(name='model_ensemble',
                    inputs=[model_score.input, model_special.input, model_board.input, model_next.input],
                    outputs=model_ensemble)

model.summary()

model.compile(loss="mean_absolute_error", optimizer="adam",
              metrics=["mean_absolute_error", "mse", "categorical_crossentropy"])

callback = keras.callbacks.EarlyStopping(monitor='loss', min_delta=0.0001, patience=3)

model.fit(decoded_dataset, callbacks=[callback], epochs=70)

dot_img_file = '/home/anarbek/tmp/models/v5/model.png'
keras.utils.plot_model(model, to_file=dot_img_file, show_shapes=True)

model.save("/home/anarbek/tmp/models/v5/experimental/tf", save_format="tf", overwrite=True)

# converter = tf.lite.TFLiteConverter.from_keras_model(model)
#
# # converter.target_spec.supported_ops = [
# #   tf.lite.OpsSet.TFLITE_BUILTINS, # enable TensorFlow Lite ops.
# #   tf.lite.OpsSet.SELECT_TF_OPS # enable TensorFlow ops.
# # ]
#
# tflite_model = converter.convert()
#
# with open('/home/anarbek/tmp/models/v4/model.tflite', 'wb') as f:
#   f.write(tflite_model)
