package ru.ardyc.extension.yandex

import org.springframework.http.HttpStatusCode
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import reactor.core.publisher.Mono
import ru.ardyc.extension.yandex.client.YandexDiskClient
import ru.bytebosses.extension.api.ExtensionProvider
import ru.bytebosses.extension.api.InformationProvider
import ru.bytebosses.extension.api.LinkUpdateEvent
import ru.bytebosses.extension.api.LinkUpdateInformation
import ru.bytebosses.extension.api.mapper.YamlTextMapper
import java.net.URI
import java.time.OffsetDateTime

@ExtensionProvider(name = "yandex", author = "ardyc", version = "1.0.0")
class YandexCloudExtensionProvider : InformationProvider {

    private lateinit var client: YandexDiskClient
    private val mapper = YamlTextMapper.default(this::class.java)

    override fun initialize() {

        val httpServiceProxyFactory = HttpServiceProxyFactory
            .builderFor(WebClientAdapter.create(createWebClient()))
            .build()
        client = httpServiceProxyFactory.createClient(YandexDiskClient::class.java)
    }

    override fun isSupported(uri: URI): Boolean {
        return uri.host == "disk.yandex.ru"
    }

    override fun retrieveInformation(
        uri: URI,
        metadata: Map<String, String>,
        lastUpdate: OffsetDateTime
    ): LinkUpdateInformation {
        val response = client.getInfo(uri.toString())
        val count = metadata["views_count"]?.toInt() ?: 0
        if (response.viewsCount > count) {
            val update = metadata.toMutableMap()
            update["views_count"] = response.viewsCount.toString()
            return LinkUpdateInformation(
                uri,
                listOf(
                    LinkUpdateEvent(
                        "yandex_disk.views_count",
                        OffsetDateTime.now(),
                        mapOf("count" to response.viewsCount.toString())
                    )
                ).map { it.copy(type = mapper.map(it.type)) },
                update
            )
        }
        return LinkUpdateInformation(
            uri,
            emptyList(),
            metadata
        )
    }

    private fun createWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl(API_URL)
            .defaultStatusHandler(
                { _: HttpStatusCode? -> true },
                { _: ClientResponse? -> Mono.empty() })
            .build()
    }

    companion object {
        private const val API_URL = "https://cloud-api.yandex.net"
    }
}