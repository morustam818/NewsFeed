package com.webmd.newsfeed.data.mapper

import com.webmd.newsfeed.data.local.entity.ArticleEntity
import com.webmd.newsfeed.data.model.ArticleDto
import com.webmd.newsfeed.domain.model.Article
import com.webmd.newsfeed.domain.model.Source
import kotlin.collections.filter
import kotlin.collections.map
import kotlin.let
import kotlin.text.isNotBlank

/**
 * Mapper for converting between data layer and domain layer models
 */
object ArticleMapper {
    
    // DTO to Domain
    fun toDomainFromDto(dto: ArticleDto): Article {
        return Article(
            source = dto.source?.let { 
                Source(
                    id = it.id,
                    name = it.name
                )
            },
            author = dto.author,
            title = dto.title,
            description = dto.description,
            url = dto.url,
            urlToImage = dto.urlToImage,
            publishedAt = dto.publishedAt,
            content = dto.content
        )
    }

    fun toDomainListFromDto(dtos: List<ArticleDto>): List<Article> {
        return dtos.map { toDomainFromDto(it) }
    }

    // Domain to Entity
    fun toEntity(article: Article): ArticleEntity {
        return ArticleEntity(
            url = article.url ?: "",
            sourceId = article.source?.id,
            sourceName = article.source?.name,
            author = article.author,
            title = article.title,
            description = article.description,
            urlToImage = article.urlToImage,
            publishedAt = article.publishedAt,
            content = article.content
        )
    }

    fun toEntityList(articles: List<Article>): List<ArticleEntity> {
        return articles
            .filter { it.url != null && it.url.isNotBlank() }
            .map { toEntity(it) }
    }

    // Entity to Domain
    fun toDomainFromEntity(entity: ArticleEntity): Article {
        return Article(
            source = if (entity.sourceId != null || entity.sourceName != null) {
                Source(
                    id = entity.sourceId,
                    name = entity.sourceName
                )
            } else null,
            author = entity.author,
            title = entity.title,
            description = entity.description,
            url = entity.url,
            urlToImage = entity.urlToImage,
            publishedAt = entity.publishedAt,
            content = entity.content
        )
    }

    fun toDomainListFromEntity(entities: List<ArticleEntity>): List<Article> {
        return entities.map { toDomainFromEntity(it) }
    }
}
