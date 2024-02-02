package org.dkp.urlshortener.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "urls")
data class UrlEntity(
        @Id
        val id: String? = null,
        val originalUrl: String,
        @Indexed(unique = true) val shortUrl: String,
        val createdAt: LocalDateTime = LocalDateTime.now(),
)
