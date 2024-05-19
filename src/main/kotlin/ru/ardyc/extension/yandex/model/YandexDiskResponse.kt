package ru.ardyc.extension.yandex.model

import com.fasterxml.jackson.annotation.JsonProperty

data class YandexDiskResponse(
    @JsonProperty("views_count") val viewsCount: Int
) {
}