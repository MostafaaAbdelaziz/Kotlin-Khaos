package com.kotlinkhaos.classes.utils

import android.Manifest.permission.READ_MEDIA_IMAGES
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Sets up callbacks for image picking from the gallery, including permission requests.
 * This function returns a pair of ActivityResultLaunchers: one for picking the image and another for requesting permissions.
 *
 * @param onImagePicked A lambda function to handle the picked image Uri.
 * @return A Pair of ActivityResultLauncher: the first for image picking and the second for permission requests.
 */
fun Fragment.setupImagePickerCallbacks(onImagePicked: (Uri?) -> Unit): Pair<ActivityResultLauncher<Intent>, ActivityResultLauncher<String>> {
    val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                onImagePicked(result.data?.data)
            }
        }

    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery(pickImageLauncher)
            }
        }

    return Pair(pickImageLauncher, requestPermissionLauncher)
}

/**
 * Initiates the process to open the picture gallery. It checks for the necessary permission and, if granted, opens the gallery.
 * Otherwise, it requests the required permission.
 *
 * @param pickImageLauncher The ActivityResultLauncher to handle the result of picking an image from the gallery.
 * @param requestPermissionLauncher The ActivityResultLauncher to request the necessary permission.
 */
fun Fragment.openPictureGallery(
    pickImageLauncher: ActivityResultLauncher<Intent>,
    requestPermissionLauncher: ActivityResultLauncher<String>
) {
    when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(
            requireContext(),
            READ_MEDIA_IMAGES
        ) -> {
            openGallery(pickImageLauncher)
        }

        else -> {
            requestPermissionLauncher.launch(READ_MEDIA_IMAGES)
        }
    }
}

/**
 * Opens the gallery for image selection. This function creates an intent for picking an image and launches it.
 *
 * @param launcher The ActivityResultLauncher used to start the activity for result.
 */
private fun openGallery(launcher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Intent.ACTION_PICK).apply {
        type = "image/*"
    }
    launcher.launch(intent)
}
