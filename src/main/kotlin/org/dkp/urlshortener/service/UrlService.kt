package org.dkp.urlshortener.service

import jakarta.servlet.http.HttpServletRequest
import org.dkp.urlshortener.entity.UrlEntity

interface UrlService {

    fun shortenUrl(originalUrl: String,request: HttpServletRequest): String

    fun resolveUrl(shortUrl: String): UrlEntity?

}