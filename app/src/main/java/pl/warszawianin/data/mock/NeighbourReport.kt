package pl.warszawianin.data.mock

data class NeighbourReport(
    val id: Int,
    val title: String,
    val category: String,
    val distance: String,
    val supporters: Int,
    val address: String,
    val demoPhotoAsset: String  // filename in assets/demo_photos/
)
