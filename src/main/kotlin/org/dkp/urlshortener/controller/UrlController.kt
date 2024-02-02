package org.dkp.urlshortener.controller

import jakarta.servlet.http.HttpServletRequest
import org.dkp.urlshortener.service.UrlService
import org.hibernate.validator.constraints.URL
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/url")
class UrlController(private val urlService: UrlService) {

    /**
     * Assumes that one single database can handle the load and the data is not too large
     * Shortens the original URL and saves it in the database and returns the shortened URL
     * @param originalUrl the original URL to be shortened
     * @param request the HttpServletRequest object to get the server URL
     * @return the shortened URL
     */
    @PostMapping("/shorten")
    fun shortenUrl(@RequestParam(required = true) @URL originalUrl: String, request: HttpServletRequest): ResponseEntity<String> {
        val shortUrl = urlService.shortenUrl(originalUrl, request)
        return ResponseEntity.ok(shortUrl)
    }

    /**
     * Resolves the short URL to the original URL from the database
     * @param shortUrl the short URL to be resolved
     * @return the original URL
     * @return null if the short URL is not found
     */
    @GetMapping("/{shortUrl}")
    fun redirectUrl(@PathVariable shortUrl: String): ResponseEntity<Unit> {
        val originalUrl = urlService.resolveUrl(shortUrl)?.originalUrl
        return originalUrl?.let { ResponseEntity.status(302).header("Location", it).build() }
                ?: ResponseEntity.notFound().build()
    }
}