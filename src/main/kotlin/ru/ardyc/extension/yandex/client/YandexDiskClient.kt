package ru.ardyc.extension.yandex.client

import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.reactive.function.client.WebClient
import ru.ardyc.extension.yandex.model.YandexDiskResponse

class YandexDiskClient(private val baseUrl: String, private val webClient: WebClient) {

    fun getInfo(@RequestParam("public_key") path: String): YandexDiskResponse {
        return webClient.get()
            .uri("$baseUrl?public_key=$path")
            .retrieve()
            .bodyToMono(YandexDiskResponse::class.java)
            .block()!!
    }
}