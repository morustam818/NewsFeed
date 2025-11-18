package com.webmd.newsfeed.ui.viewmodel

import com.webmd.newsfeed.domain.model.Article
import com.webmd.newsfeed.domain.model.Source
import com.webmd.newsfeed.domain.usecase.GetArticleByUrlUseCase
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleDetailViewModelTest : DescribeSpec({

    val testDispatcher = StandardTestDispatcher()
    val getArticleByUrlUseCase = mockk<GetArticleByUrlUseCase>(relaxed = true)

    beforeEach {
        Dispatchers.setMain(testDispatcher)
    }

    afterEach {
        Dispatchers.resetMain()
    }

    fun buildVm() = ArticleDetailViewModel(getArticleByUrlUseCase)

    describe("ArticleDetailViewModel") {

        it("should show initial state on creation") {
            runTest(testDispatcher) {
                // When
                val viewModel = buildVm()

                // Then
                val state = viewModel.uiState.value
                state shouldBe ArticleDetailUiState.Initial
            }
        }

        it("should show loading state when loading article") {
            runTest(testDispatcher) {
                // Given
                val testUrl = "https://test.com/article"
                val article = Article(
                    source = Source(id = "1", name = "Test Source"),
                    author = "Test Author",
                    title = "Test Title",
                    description = "Test Description",
                    url = testUrl,
                    urlToImage = "https://test.com/image.jpg",
                    publishedAt = "2024-01-01",
                    content = "Test Content"
                )
                coEvery { getArticleByUrlUseCase(testUrl) } returns article

                // When
                val viewModel = buildVm()
                viewModel.sendIntent(ArticleDetailIntent.LoadArticle(testUrl))
                advanceUntilIdle()

                val state = viewModel.uiState.value
                state.shouldBeInstanceOf<ArticleDetailUiState.Success>()
            }
        }

        it("should show success state when article is loaded") {
            runTest(testDispatcher) {
                // Given
                val testUrl = "https://test.com/article"
                val article = Article(
                    source = Source(id = "1", name = "Test Source"),
                    author = "Test Author",
                    title = "Test Title",
                    description = "Test Description",
                    url = testUrl,
                    urlToImage = "https://test.com/image.jpg",
                    publishedAt = "2024-01-01",
                    content = "Test Content"
                )
                coEvery { getArticleByUrlUseCase(testUrl) } returns article

                // When
                val viewModel = buildVm()
                viewModel.sendIntent(ArticleDetailIntent.LoadArticle(testUrl))
                advanceUntilIdle()

                // Then
                val state = viewModel.uiState.value
                state.shouldBeInstanceOf<ArticleDetailUiState.Success>()
                state.article shouldBe article
            }
        }

        it("should show error state when URL is null") {
            runTest(testDispatcher) {
                // When
                val viewModel = buildVm()
                viewModel.sendIntent(ArticleDetailIntent.LoadArticle(null))
                advanceUntilIdle()

                // Then
                val state = viewModel.uiState.value
                state.shouldBeInstanceOf<ArticleDetailUiState.Error>()
                state.message shouldBe "Article URL is missing"
            }
        }

        it("should show error state when article not found") {
            runTest(testDispatcher) {
                // Given
                val testUrl = "https://test.com/notfound"
                coEvery { getArticleByUrlUseCase(testUrl) } returns null

                // When
                val viewModel = buildVm()
                viewModel.sendIntent(ArticleDetailIntent.LoadArticle(testUrl))
                advanceUntilIdle()

                // Then
                val state = viewModel.uiState.value
                state.shouldBeInstanceOf<ArticleDetailUiState.Error>()
                state.message shouldBe "Article not found"
            }
        }

        it("should show error state when exception occurs") {
            runTest(testDispatcher) {
                // Given
                val testUrl = "https://test.com/article"
                val errorMessage = "Network error"
                coEvery { getArticleByUrlUseCase(testUrl) } throws Exception(errorMessage)

                // When
                val viewModel = buildVm()
                viewModel.sendIntent(ArticleDetailIntent.LoadArticle(testUrl))
                advanceUntilIdle()

                // Then
                val state = viewModel.uiState.value
                state.shouldBeInstanceOf<ArticleDetailUiState.Error>()
                state.message shouldBe errorMessage
            }
        }

        it("should show default error message when exception has no message") {
            runTest(testDispatcher) {
                // Given
                val testUrl = "https://test.com/article"
                coEvery { getArticleByUrlUseCase(testUrl) } throws Exception()

                // When
                val viewModel = buildVm()
                viewModel.sendIntent(ArticleDetailIntent.LoadArticle(testUrl))
                advanceUntilIdle()

                // Then
                val state = viewModel.uiState.value
                state.shouldBeInstanceOf<ArticleDetailUiState.Error>()
                state.message shouldBe "Failed to load article"
            }
        }

        it("should clear error and show initial state") {
            runTest(testDispatcher) {
                // Given
                val testUrl = "https://test.com/notfound"
                coEvery { getArticleByUrlUseCase(testUrl) } returns null

                // When
                val viewModel = buildVm()
                viewModel.sendIntent(ArticleDetailIntent.LoadArticle(testUrl))
                advanceUntilIdle()

                // Verify error state first
                viewModel.uiState.value.shouldBeInstanceOf<ArticleDetailUiState.Error>()

                // Clear error
                viewModel.sendIntent(ArticleDetailIntent.ClearError)
                advanceUntilIdle()

                // Then
                val state = viewModel.uiState.value
                state shouldBe ArticleDetailUiState.Initial
            }
        }
    }
})

