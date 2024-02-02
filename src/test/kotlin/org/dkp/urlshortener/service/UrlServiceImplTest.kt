package org.dkp.urlshortener.service

import org.dkp.urlshortener.entity.UrlEntity
import org.dkp.urlshortener.repository.UrlRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.mock.web.MockHttpServletRequest

class UrlServiceImplTest {

    private val urlRepository = Mockito.mock(UrlRepository::class.java)
    private val urlService = UrlServiceImpl(urlRepository)

    @Test
    fun `shortenUrl returns shortened URL for valid original URL`() {
        val originalUrl = "https://example.com"
        val request = MockHttpServletRequest()

        Mockito.`when`(urlRepository.existsByShortUrl(Mockito.anyString())).thenReturn(false)

        val response = urlService.shortenUrl(originalUrl, request)

        assertEquals("${request.scheme}://${request.serverName}:${request.serverPort}", response.substring(0, response.lastIndexOf("/")))
    }

    @Test
    fun `resolveUrl returns original URL for valid short URL`() {
        val shortUrl = "abc123"
        val originalUrl = "https://example.com"

        Mockito.`when`(urlRepository.findByShortUrl(shortUrl))
                .thenReturn(UrlEntity(originalUrl = originalUrl, shortUrl = shortUrl))

        val response = urlService.resolveUrl(shortUrl)

        assertEquals(originalUrl, response?.originalUrl)
    }

    @Test
    fun `resolveUrl returns null for invalid short URL`() {
        val shortUrl = "invalid"

        Mockito.`when`(urlRepository.findByShortUrl(shortUrl)).thenReturn(null)

        val response = urlService.resolveUrl(shortUrl)

        assertEquals(null, response)
    }

    @Test
    fun `generateBase62Encoded returns unique short URL for valid original URL`() {
        val originalUrl = "https://example.com"

        val response = urlService.generateBase62Encoded(originalUrl)

        assertEquals(6, response.length)
    }

    @Test
    fun `generateBase62Encoded returns different short URLs for different original URLs`() {
        val originalUrl1 = "https://example.com"
        val originalUrl2 = "https://github.com"

        val response1 = urlService.generateBase62Encoded(originalUrl1)
        val response2 = urlService.generateBase62Encoded(originalUrl2)

        assertNotEquals(response1, response2)
    }

}