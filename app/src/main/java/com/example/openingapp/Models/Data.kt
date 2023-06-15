package com.example.openingapp.Models

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("recent_links"      ) var recentLinks     : ArrayList<Links>    = arrayListOf(),
    @SerializedName("top_links"         ) var topLinks        : ArrayList<Links>    = arrayListOf(),
    @SerializedName("overall_url_chart" ) var overallUrlChart : OverallUrlChart?       = OverallUrlChart()
)
