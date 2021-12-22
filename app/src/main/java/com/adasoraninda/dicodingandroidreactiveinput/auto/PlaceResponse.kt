package com.adasoraninda.dicodingandroidreactiveinput.auto

import com.google.gson.annotations.SerializedName

data class PlaceResponse(
    @field:SerializedName("features")
    val features: List<PlacesItem>
)