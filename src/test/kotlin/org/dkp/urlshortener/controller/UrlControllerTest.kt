package org.dkp.urlshortener.controller

import org.dkp.urlshortener.entity.UrlEntity
import org.dkp.urlshortener.service.UrlService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockHttpServletRequest

class UrlControllerTest {

    private val urlService = Mockito.mock(UrlService::class.java)
    private val urlController = UrlController(urlService)

    @Test
    fun `shortenUrl returns shortened URL for valid original URL`() {
        val originalUrl = "https://example.com"
        val shortenedUrl = "https://short.com"
        val request = MockHttpServletRequest()

        Mockito.`when`(urlService.shortenUrl(originalUrl, request)).thenReturn(shortenedUrl)

        val response = urlController.shortenUrl(originalUrl, request)

        assertEquals(ResponseEntity.ok(shortenedUrl), response)
    }

    @Test
    fun `redirectUrl returns original URL for valid short URL`() {
        val shortUrl = "abc123"
        val originalUrl = "https://example.com"

        Mockito.`when`(urlService.resolveUrl(shortUrl))
                .thenReturn(UrlEntity(originalUrl = originalUrl, shortUrl = shortUrl))

        val response = urlController.redirectUrl(shortUrl)

        assertEquals(HttpStatus.FOUND, response.statusCode)
        assertEquals(originalUrl, response.headers.location.toString())
    }

    @Test
    fun `redirectUrl returns not found for invalid short URL`() {
        val shortUrl = "invalid"

        Mockito.`when`(urlService.resolveUrl(shortUrl)).thenReturn(null)

        val response = urlController.redirectUrl(shortUrl)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }
}