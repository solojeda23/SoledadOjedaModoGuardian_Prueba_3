package com.example.soledadojedamodoguardian.model

data class Camera(
    val id: String,
    val nombre: String,
    val zona: String,
    val online: Boolean = true
)

val camarasDemo = listOf(
    Camera(
        id = "CAM-01",
        nombre = "Entrada Principal",
        zona = "Zona Sur"
    ),
    Camera(
        id = "CAM-04",
        nombre = "Perímetro Norte",
        zona = "Sector B"
    ),
    Camera(
        id = "CAM-07",
        nombre = "Bodega A",
        zona = "Zona Sur",
        online = false
    ),
    Camera(
        id = "CAM-02",
        nombre = "Estacionamiento",
        zona = "Zona Sur"
    ),
    Camera(
        id = "CAM-03",
        nombre = "Acceso Peatonal",
        zona = "Zona Norte"
    ),
    Camera(
        id = "CAM-05",
        nombre = "Almacén Central",
        zona = "Zona Norte"
    ),
    Camera(
        id = "CAM-06",
        nombre = "Perímetro Este",
        zona = "Zona Este"
    ),
    Camera(
        id = "CAM-08",
        nombre = "Pasillo Central",
        zona = "Zona Sur"
    ),
    Camera(
        id = "CAM-09",
        nombre = "Portón Vehicular",
        zona = "Zona Oeste"
    ),
    Camera(
        id = "CAM-10",
        nombre = "Recepción",
        zona = "Zona Sur"
    ),
    Camera(
        id = "CAM-11",
        nombre = "Patio",
        zona = "Zona Norte"
    ),
    Camera(
        id = "CAM-12",
        nombre = "Salida",
        zona = "Zona Oeste"
    )
)

