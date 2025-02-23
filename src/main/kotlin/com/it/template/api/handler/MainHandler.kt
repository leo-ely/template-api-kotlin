package com.it.template.api.handler

import com.it.template.api.model.exception.ApiException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets

abstract class MainHandler {

    fun optionHeaders(): (HttpHeaders) -> Unit = { headers ->
        headers[HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS] = "Content-Type, Accept"
        headers[HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS] = "GET, PUT, POST, DELETE, PATCH, HEAD, OPTIONS"
    }

    fun defaultHeaders(): (HttpHeaders) -> Unit = { headers ->
        headers.contentType = MediaType.APPLICATION_JSON
        headers[HttpHeaders.CONTENT_ENCODING] = StandardCharsets.UTF_8.name()
    }

    fun mapResponse(): (ServerResponse) -> Mono<out ServerResponse> = { response ->
        if (response.statusCode().isError) Mono.error(ApiException(HttpStatus.valueOf(response.statusCode().value())))
        else Mono.just(response)
    }

    fun mapErrorResume(): (Throwable) -> Mono<out ServerResponse> = { e ->
        Mono.just("Error: ${e.message}")
            .flatMap { ServerResponse.badRequest().contentType(MediaType.TEXT_PLAIN).bodyValue(it) }
    }

    fun mapNoContent(): Mono<ServerResponse> = ServerResponse.noContent().build()

    fun mapBadRequest(): Mono<ServerResponse> =
        ServerResponse.badRequest().bodyValue(ApiException(HttpStatus.BAD_REQUEST))

    fun mapUnauthorized(): Mono<ServerResponse> =
        ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue(ApiException(HttpStatus.UNAUTHORIZED))

    fun mapNotFound(): Mono<ServerResponse> =
        ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(ApiException(HttpStatus.NOT_FOUND))

    fun mapGone(): Mono<ServerResponse> =
        ServerResponse.status(HttpStatus.GONE).bodyValue(ApiException(HttpStatus.GONE))

    fun mapInternalServerError(): Mono<ServerResponse> = ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .bodyValue(ApiException(HttpStatus.INTERNAL_SERVER_ERROR))
}
