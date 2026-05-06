package pl.warszawianin.data.mock

object MockNeighbourData {
    val reports = listOf(
        NeighbourReport(
            id = 1,
            title = "Dziura w chodniku",
            category = "Drogi",
            distance = "340 m",
            supporters = 5,
            address = "ul. Marszałkowska 12",
            demoPhotoAsset = "pothole.jpg"
        ),
        NeighbourReport(
            id = 2,
            title = "Zepsuta latarnia",
            category = "Oświetlenie",
            distance = "520 m",
            supporters = 3,
            address = "ul. Puławska 45",
            demoPhotoAsset = "streetlight.jpg"
        ),
        NeighbourReport(
            id = 3,
            title = "Przepełniony kosz",
            category = "Czystość",
            distance = "180 m",
            supporters = 8,
            address = "al. Jerozolimskie 22",
            demoPhotoAsset = "trash.jpg"
        ),
        NeighbourReport(
            id = 4,
            title = "Uszkodzona ławka",
            category = "Infrastruktura",
            distance = "750 m",
            supporters = 2,
            address = "Park Łazienkowski",
            demoPhotoAsset = "broken_bench.jpg"
        ),
        NeighbourReport(
            id = 5,
            title = "Zniszczona nawierzchnia",
            category = "Drogi",
            distance = "1.2 km",
            supporters = 12,
            address = "ul. Nowy Świat 8",
            demoPhotoAsset = "sidewalk.jpg"
        ),
        NeighbourReport(
            id = 6,
            title = "Połamane drzewo",
            category = "Zieleń",
            distance = "900 m",
            supporters = 0,
            address = "ul. Krakowskie Przedmieście",
            demoPhotoAsset = "fallen_tree.jpg"
        )
    )

    /** The primary demo photo used when user "takes a photo" in demo mode */
    const val PRIMARY_DEMO_PHOTO = "pothole.jpg"
}
