package pl.warszawianin.util

import android.content.Context
import java.io.File
import java.io.FileOutputStream

/**
 * Utility to copy demo photos from assets to app-private storage.
 * This makes them available via file:// URIs for Coil and FileProvider.
 */
object DemoPhotoUtils {

    fun copyDemoPhotoToStorage(context: Context, assetFileName: String): File {
        val photosDir = File(context.filesDir, "photos").also { it.mkdirs() }
        val destFile = File(photosDir, "demo_$assetFileName")

        if (!destFile.exists()) {
            context.assets.open("demo_photos/$assetFileName").use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
        }

        return destFile
    }

    fun ensureAllDemoPhotosExist(context: Context): Map<String, File> {
        val photos = listOf(
            "pothole.jpg", "broken_bench.jpg", "trash.jpg",
            "streetlight.jpg", "sidewalk.jpg", "fallen_tree.jpg"
        )
        return photos.associateWith { copyDemoPhotoToStorage(context, it) }
    }
}
