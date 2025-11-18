package com.webmd.newsfeed.data.repository

import com.webmd.newsfeed.data.local.dao.ArticleDao
import com.webmd.newsfeed.data.local.database.NewsDatabase
import com.webmd.newsfeed.data.local.entity.ArticleEntity
import com.webmd.newsfeed.data.model.ArticleDto
import com.webmd.newsfeed.data.model.NewsResponse
import com.webmd.newsfeed.data.model.SourceDto
import com.webmd.newsfeed.data.remote.NewsApiService
import com.webmd.newsfeed.domain.model.Article
import com.webmd.newsfeed.utils.AppNetworkResult

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody

import retrofit2.HttpException
import retrofit2.Response

class NewsRepositoryTest : DescribeSpec({

    // Test fixtures
    val newsApiService = mockk<NewsApiService>()
    val newsDatabase = mockk<NewsDatabase>(relaxed = true)
    val articleDao = mockk<ArticleDao>()
    val apiKey = "test-api-key"
    val repository = NewsRepositoryImpl(newsApiService, newsDatabase, apiKey)

    // Helper function to create test article entity
    fun createTestArticleEntity(
        url: String = "https://test.com/article",
        title: String = "Test Article"
    ) = ArticleEntity(
        url = url,
        sourceId = "source-id",
        sourceName = "Source Name",
        author = "John Doe",
        title = title,
        description = "Test Description",
        urlToImage = "https://test.com/image.jpg",
        publishedAt = "2024-01-01",
        content = "Test Content"
    )

    // Helper function to create test article DTO
    fun createTestArticleDto(
        url: String = "https://test.com/article",
        title: String = "Test Article"
    ) = ArticleDto(
        source = SourceDto(id = "source-id", name = "Source Name"),
        author = "John Doe",
        title = title,
        description = "Test Description",
        url = url,
        urlToImage = "https://test.com/image.jpg",
        publishedAt = "2024-01-01",
        content = "Test Content"
    )

    beforeEach {
        every { newsDatabase.articleDao } returns articleDao
        coEvery { articleDao.insertArticles(any()) } returns Unit
    }

    describe("getTopHeadlines") {

        it("returns loading state initially when database has articles") {
            runTest {
                val cachedArticle = createTestArticleEntity()
                every { articleDao.getAllArticles() } returns flowOf(listOf(cachedArticle))

                val apiArticle = createTestArticleDto()
                val apiResponse = NewsResponse("ok", 1, listOf(apiArticle))

                coEvery { newsApiService.getTopHeadlines(apiKey = apiKey) } returns Response.success(
                    apiResponse
                )

                val result = repository.getTopHeadlines().first()

                result.shouldBeInstanceOf<AppNetworkResult.Loading<List<Article>>>()
            }
        }

        it("returns loading state when API fails") {
            runTest {
                every { articleDao.getAllArticles() } returns flowOf(emptyList())

                val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "")
                val errorResponse = Response.error<NewsResponse>(400, errorBody)

                coEvery { newsApiService.getTopHeadlines(apiKey = apiKey) } throws HttpException(
                    errorResponse
                )

                val result = repository.getTopHeadlines().first()

                result.shouldBeInstanceOf<AppNetworkResult.Loading<List<Article>>>()
            }
        }
    }

    describe("getArticleByUrl") {

        it("returns article when found in database") {
            runTest {
                val testUrl = "https://test.com/article"
                val articleEntity = createTestArticleEntity(url = testUrl)

                coEvery { articleDao.getArticleByUrl(testUrl) } returns articleEntity

                val result = repository.getArticleByUrl(testUrl)

                result?.title shouldBe "Test Article"
                result?.url shouldBe testUrl
            }
        }

        it("returns null when article not found") {
            runTest {
                val testUrl = "https://test.com/notfound"
                coEvery { articleDao.getArticleByUrl(testUrl) } returns null
                val result = repository.getArticleByUrl(testUrl)
                result shouldBe null
            }
        }
    }
})
