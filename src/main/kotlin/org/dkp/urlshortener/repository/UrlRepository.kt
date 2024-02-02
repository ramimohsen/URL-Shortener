package org.dkp.urlshortener.repository

import org.dkp.urlshortener.entity.UrlEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository


@Repository
interface UrlRepository : MongoRepository<UrlEntity, String> {

    fun findByShortUrl(shortUrl: String): UrlEntity?

    fun existsByShortUrl(shortUrl: String): Boolean
}