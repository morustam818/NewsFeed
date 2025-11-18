package com.webmd.newsfeed.data.mapper

import com.webmd.newsfeed.data.local.entity.ArticleEntity
import com.webmd.newsfeed.data.model.ArticleDto
import com.webmd.newsfeed.domain.model.Article
import com.webmd.newsfeed.domain.model.Source
import kotlin.collections.filter
import kotlin.collections.map
import kotlin.text.isNotBlank

/**
 * Mapper for converting between data layer and domain layer models
 */

fun List<ArticleEntity>.toArticleList(): List<Article> {
    return map { it.toArticle() }
}

fun ArticleEntity.toArticle(): Article {
    return Article(
        source = if (sourceId != null || sourceName != null) {
            Source(
                id = sourceId,
                name = sourceName
            )
        } else null,
        author = author,
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content
    )
}

fun List<ArticleDto>.toArticleEntity(): List<ArticleEntity> {
    return filter { it.url != null && it.url.isNotBlank() }
        .map {
            ArticleEntity(
                url = it.url ?: "",
                sourceId = it.source?.id,
                sourceName = it.source?.name,
                author = it.author,
                title = it.title,
                description = it.description,
                urlToImage = it.urlToImage,
                publishedAt = it.publishedAt,
                content = it.content
            )
        }
}
