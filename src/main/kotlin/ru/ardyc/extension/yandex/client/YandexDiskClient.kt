package ru.ardyc.extension.yandex.client

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange
import ru.ardyc.extension.yandex.model.YandexDiskResponse

interface YandexDiskClient {

    @GetExchange("/v1/disk/public/resources?public_key={path}")
    fun getInfo(@PathVariable path: String): YandexDiskResponse
}