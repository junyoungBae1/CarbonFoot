package com.example.ptype1

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.Integer.min
import java.nio.ByteBuffer
import java.nio.ByteBuffer.allocateDirect
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TensorCls(private val context: Context) {

    private var interpreter: Interpreter
    private val MODEL_NAME = "yolov4-416-final-int8.tflite"
    private val LABELS_NAME = "labels.txt"
    private val labels: List<String>
    private lateinit var buffer: ByteBuffer
    private lateinit var outputTensor1: TensorBuffer
    private lateinit var outputTensor2: TensorBuffer



    init {
        val modelByteBuffer: MappedByteBuffer = FileUtil.loadMappedFile(context, MODEL_NAME)
        interpreter = Interpreter(modelByteBuffer)
        labels = loadLabels(LABELS_NAME)
        initModelShape()
    }

    private fun initModelShape() {

        //modelInputChannel = inputShape[0]
        outputTensor1 = TensorBuffer.createFixedSize(interpreter.getOutputTensor(0).shape(), DataType.FLOAT32)
        outputTensor2 = TensorBuffer.createFixedSize(interpreter.getOutputTensor(1).shape(), DataType.FLOAT32)

    }

    //Pair<Int, Float>
    fun classify(image: Bitmap):  List<Category> {

        val resizedBitmap = Bitmap.createScaledBitmap(image, 416, 416, true)
        buffer = allocateDirect(416 * 416 * 3 * 4).order(ByteOrder.nativeOrder())

        buffer.clear()

        for (y in 0 until 416) {
            for (x in 0 until 416) {
                val pixel = resizedBitmap.getPixel(x, y)
                buffer.putFloat((pixel shr 16 and 0xFF) / 255.0f) // red
                buffer.putFloat((pixel shr 8 and 0xFF) / 255.0f) // green
                buffer.putFloat((pixel and 0xFF) / 255.0f) // blue
            }
        }

        val outputs: MutableMap<Int, Any> = HashMap()
        outputs[0] = outputTensor1.buffer
        outputs[1] = outputTensor2.buffer

        outputTensor1.buffer.rewind()
        outputTensor2.buffer.rewind()



        interpreter.runForMultipleInputsOutputs(arrayOf(buffer), outputs)

        val topLabels = getTopKProbability(outputTensor2, 3)

        return topLabels

    }

    private fun getTopKProbability(tensorBuffer: TensorBuffer, k: Int): List<Category> {

        // Get the float array from the tensor buffer
        val probabilities = tensorBuffer.floatArray

        // Create a list to hold the labeled probabilities
        val labeledProbabilities = mutableListOf<Category>()

        for (i in probabilities.indices step 10) {
            // Get the slice of 10 elements
            val slice = probabilities.sliceArray(i until i + 10)

            // Find the index of the maximum value
            val maxIndex = slice.indices.maxByOrNull { slice[it] } ?: -1

            // Get the label of the maximum value
            val label = labels[maxIndex]

            // Get the probability of the maximum value
            val probability = slice[maxIndex]

            if(label=="dish"){continue}

            // Add the labeled probability to the list
            labeledProbabilities.add(Category(label, probability))
        }

        // Sort the labeled probabilities in descending order
        val sortedLabeledProbabilities = labeledProbabilities.sortedByDescending { it.score }

        // Return the top-K probabilities
        return sortedLabeledProbabilities.take(k)


    }


    private fun loadLabels(fileName: String): List<String> {
        val assetManager = context.assets
        val inputStream = assetManager.open(fileName)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        return bufferedReader.readLines()
    }
}

/* private fun initModelShape() {
       val inputTensor = interpreter.getInputTensor(0)
       val inputShape = inputTensor.shape()
       //modelInputChannel = inputShape[0]
       modelInputChannel = inputShape[0]
       modelInputWidth = inputShape[1]
       modelInputHeight = inputShape[2]

       val outputTensor = interpreter.getOutputTensor(0)
       val outputShape = outputTensor.shape()
       modelOutputClasses = outputShape[1]
   }*/

/* private fun argmax(array: ByteArray): Pair<Int, Byte> {
        var argmax = 0
        var max = array[0]
        for (i in 1 until array.size) {
            val f = array[i]
            if (f > max) {
                argmax = i
                max = f
            }
        }
        return Pair(argmax, max)
    }*/

/*private fun resizeBitmap(imageBitmap: Bitmap): Bitmap {
    return Bitmap.createScaledBitmap(imageBitmap, 416, 416, true)

    //val desiredWidth = Math.sqrt((110592 / modelInputChannel).toDouble()).toInt()
    //return Bitmap.createScaledBitmap(bitmap, desiredWidth, desiredWidth, false)
}

private fun convertBitmapToGrayByteBuffer(bitmap: Bitmap): ByteBuffer {
    // val byteBuffer = ByteBuffer.allocateDirect(bitmap.byteCount)
    val buffer = allocateDirect(416 * 416 * 3 * 4).order(ByteOrder.nativeOrder())

    //Log.d("classTest", bitmap.byteCount.toString())
    //byteBuffer.order(ByteOrder.nativeOrder())

    val pixels = IntArray(bitmap.width * bitmap.height)
    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

    buffer.clear() // ByteBuffer 초기화

    for (pixel in pixels) {
        val r = pixel.shr(16) and 0xFF
        val g = pixel.shr(8) and 0xFF
        val b = pixel and 0xFF
        //val avgPixelValue = (r + g + b) / 3.0f
        //val avgPixelValue = ((r + g + b) / 3.0f).coerceIn(0.0f, 255.0f)
        val avgPixelValue = ((r + g + b) / 3).toFloat()
        //val normalizedPixelValue = avgPixelValue / 255.0f

        //byteBuffer.putFloat(normalizedPixelValue)
        buffer.putFloat(avgPixelValue)

    }
    return buffer

    private fun loadModelFIle(modelName: String): ByteBuffer {
        val am = context.assets
        val afd = am.openFd(modelName)
        val fis = FileInputStream(afd.fileDescriptor)
        val fc = fis.channel
        val startOffset = afd.startOffset
        val declaredLength = afd.declaredLength
        return fc.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

}*/