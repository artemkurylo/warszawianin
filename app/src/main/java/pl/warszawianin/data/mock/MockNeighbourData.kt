package pl.warszawianin.data.mock

object MockNeighbourData {
    val reports = listOf(
        NeighbourReport(
            id = 1,
            title = "Dziura w chodniku",
            category = "Drogi",
            distance = "340 m",
            supporters = 5,
            address = "ul. Marszałkowska 12"
        ),
        NeighbourReport(
            id = 2,
            title = "Zepsuta latarnia",
            category = "Oświetlenie",
            distance = "520 m",
            supporters = 3,
            address = "ul. Puławska 45"
        ),
        NeighbourReport(
            id = 3,
            title = "Przepełniony kosz",
            category = "Czystość",
            distance = "180 m",
            supporters = 8,
            address = "al. Jerozolimskie 22"
        ),
        NeighbourReport(
            id = 4,
            title = "Uszkodzona ławka",
            category = "Infrastruktura",
            distance = "750 m",
            supporters = 2,
            address = "Park Łazienkowski"
        ),
        NeighbourReport(
            id = 5,
            title = "Zniszczona nawierzchnia",
            category = "Drogi",
            distance = "1.2 km",
            supporters = 12,
            address = "ul. Nowy Świat 8"
        ),
        NeighbourReport(
            id = 6,
            title = "Połamane drzewo",
            category = "Zieleń",
            distance = "900 m",
            supporters = 0,
            address = "ul. Krakowskie Przedmieście"
        )
    )
}
