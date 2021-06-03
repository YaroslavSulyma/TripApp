package com.example.tripapp.ui.main.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.view.isVisible
import com.bumptech.glide.RequestManager
import com.example.tripapp.R
import com.example.tripapp.databinding.FragmentCameraBinding
import com.example.tripapp.ui.snackbar
import com.example.tripapp.utils.viewBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class CameraFragment : Fragment(R.layout.fragment_camera) {

    @Inject
    lateinit var glide: RequestManager

    private lateinit var bitmap: Bitmap
    private lateinit var base64encoded: String
    private lateinit var functions: FirebaseFunctions

    private val binding by viewBinding(FragmentCameraBinding::bind)

    private lateinit var cropContent: ActivityResultLauncher<Any?>

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            binding.tvLongitude.text = ""
            binding.tvLatitude.text = ""
            binding.tvLocationName.text = ""
            binding.tvScore.text = ""
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cropContent = registerForActivityResult(cropActivityResultContract) {
            it?.let {
                convertUriIntoBitmap(it)
                glide.load(it).into(binding.ivImage)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        functions = Firebase.functions

        binding.btnSetImage.setOnClickListener {
            cropContent.launch(null)
        }
        binding.ivImage.setOnClickListener {
            cropContent.launch(null)
        }
        binding.btnRecognize.setOnClickListener {
            binding.createPostProgressBar.isVisible = true
            encodeImage()
            sendRequest()
        }
    }

    private fun convertUriIntoBitmap(uri: Uri) {
        bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(
                    requireContext().contentResolver,
                    uri
                )
            )
        } else {
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
        }
    }

    private fun encodeImage() {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
        base64encoded = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
    }

    private fun annotateImage(requestJson: String): Task<JsonElement> {
        return functions
            .getHttpsCallable("annotateImage")
            .call(requestJson)
            .continueWith { task ->
                val result = task.result?.data
                JsonParser.parseString(Gson().toJson(result))
            }
    }

    private fun sendRequest() {
        var request = JsonObject()
        val image = JsonObject()
        image.add("content", JsonPrimitive(base64encoded))
        request.add("image", image)
        val feature = JsonObject()
        feature.add("maxResults", JsonPrimitive(5))
        feature.add("type", JsonPrimitive("LANDMARK_DETECTION"))
        val features = JsonArray()
        features.add(feature)
        request.add("features", features)

        annotateImage(request.toString())
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    binding.createPostProgressBar.isVisible = false
                    snackbar("Can't recognize this place")
                } else {
                    getData(task)
                }
            }
    }

    private fun getData(task: Task<JsonElement>) {
        for (label in task.result!!.asJsonArray[0].asJsonObject["landmarkAnnotations"].asJsonArray) {
            val labelObj = label.asJsonObject
            val landmarkName = labelObj["description"]
            val score = labelObj["score"]
            binding.createPostProgressBar.isVisible = false
            binding.tvLocationName.text = landmarkName.toString()
            binding.tvScore.text = score.toString()
            for (loc in labelObj["locations"].asJsonArray) {
                val latitude = loc.asJsonObject["latLng"].asJsonObject["latitude"]
                val longitude = loc.asJsonObject["latLng"].asJsonObject["longitude"]
                binding.tvLatitude.text = latitude.toString()
                binding.tvLongitude.text = longitude.toString()
            }
        }
    }
}