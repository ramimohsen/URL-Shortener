package org.dkp.urlshortener.service

import jakarta.servlet.http.HttpServletRequest
import org.apache.commons.codec.binary.Base64
import org.dkp.urlshortener.entity.UrlEntity
import org.dkp.urlshortener.repository.UrlRepository
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.security.MessageDigest


@Service
class UrlServiceImpl(private val urlRepository: UrlRepository) : UrlService {

    /**
     * Assumes that one single database can handle the load and the data is not too large
     * Shortens the original URL and saves it in the database and returns the shortened URL
     * @param originalUrl the original URL to be shortened
     * @param request the HttpServletRequest object to get the server URL
     * @return the full shortened URL
     */
    @Cacheable("shortUrlCache", key = "#originalUrl")
    override fun shortenUrl(originalUrl: String, request: HttpServletRequest): String {
        val uniqueShortUrl = generateUniqueShortUrl(originalUrl)
        urlRepository.save(UrlEntity(originalUrl = originalUrl, shortUrl = uniqueShortUrl))
        return "${buildServerUrl(request)}/$uniqueShortUrl"
    }


    /**
     * Resolves the short URL to the original URL from the database
     * @param shortUrl the short URL to be resolved
     * @return the original URL
     * @return null if the short URL is not found
     */
    @Cacheable("shortUrlCache", key = "#shortUrl")
    override fun resolveUrl(shortUrl: String): UrlEntity? {
        return urlRepository.findByShortUrl(shortUrl)?.also {
            addToCache(it.shortUrl, it)
        }
    }

    /**
     * Adds the resolved URL to the cache
     * @param shortUrl the short URL to be resolved
     * @param urlEntity the resolved URL
     * @return the resolved URL
     */
    @CachePut("shortUrlCache", key = "#shortUrl")
    fun addToCache(shortUrl: String, urlEntity: UrlEntity): UrlEntity {
        return urlEntity
    }

    private fun buildServerUrl(request: HttpServletRequest): String {
        return "${request.scheme}://${request.serverName}:${request.serverPort}"
    }

    /**
     * Generates a unique short URL by encoding the original URL using Base62 encoding
     * @param originalUrl the original URL to be shortened
     * @return the unique short URL
     */

    private fun generateBase62Encoded(originalUrl: String): String {
        val md = MessageDigest.getInstance("MD5")
        val md5HashBytes = md.digest(originalUrl.toByteArray())
        val base64Encoded = Base64.encodeBase64URLSafeString(md5HashBytes)
        val uniqueShortUrl = base64Encoded.take(6)
        return uniqueShortUrl
    }

    /**
     * Generates a unique short URL by appending the current time in milliseconds to the original URL
     * @param originalUrl the original URL to be shortened
     * @return the unique short URL
     */
    private fun generateUniqueShortUrl(originalUrl: String): String {
        var uniqueShortUrl = generateBase62Encoded(originalUrl)
        while (urlRepository.existsByShortUrl(uniqueShortUrl)) {
            uniqueShortUrl = generateBase62Encoded(originalUrl + System.currentTimeMillis())
        }
        return uniqueShortUrl
    }
}