package com.webmd.newsfeed.ui.viewmodel

import com.webmd.newsfeed.domain.model.Article
import com.webmd.newsfeed.domain.model.Source
import com.webmd.newsfeed.domain.usecase.GetTopHeadlinesUseCase
import com.webmd.newsfeed.utils.AppNetworkResult
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModelTest : DescribeSpec({

    val testDispatcher = StandardTestDispatcher()
    val topHeadlinesUseCase = mockk<GetTopHeadlinesUseCase>(relaxed = true)

    beforeEach {
        Dispatchers.setMain(testDispatcher)
    }

    afterEach {
        Dispatchers.resetMain()
    }

    describe("NewsViewModel") {

        it("should show loading state when loading news") {
            runTest(testDispatcher) {
                // Given
                val articles = emptyList<Article>()
                every { topHeadlinesUseCase() } returns flowOf(
                    AppNetworkResult.Loading(articles)
                )

                // When
                val viewModel = NewsViewModel(topHeadlinesUseCase)
                advanceUntilIdle()

                // Then
                val state = viewModel.uiState.value
                state.shouldBeInstanceOf<NewsUiState.Loading>()
                state.articles shouldBe articles
            }
        }

        it("should show success state with articles when data loaded") {
            runTest(testDispatcher) {
                // Given
                val articles = listOf(
                    Article(
                        source = Source(id = "1", name = "Test Source"),
                        author = "Test Author",
                        title = "Test Title",
                        description = "Test Description",
                        url = "https://test.com",
                        urlToImage = "https://test.com/image.jpg",
                        publishedAt = "2024-01-01",
                        content = "Test Content"
                    )
                )
                every { topHeadlinesUseCase() } returns flowOf(
                    AppNetworkResult.Success(articles)
                )

                // When
                val viewModel = NewsViewModel(topHeadlinesUseCase)
                advanceUntilIdle()

                // Then
                val state = viewModel.uiState.value
                state.shouldBeInstanceOf<NewsUiState.Success>()
                state.articles shouldBe articles
            }
        }

        it("should show error state when loading fails") {
            runTest(testDispatcher) {
                // Given
                val errorMessage = "Network error occurred"
                val articles = emptyList<Article>()
                every { topHeadlinesUseCase() } returns flowOf(
                    AppNetworkResult.Failed(articles, errorMessage)
                )

                // When
                val viewModel = NewsViewModel(topHeadlinesUseCase)
                advanceUntilIdle()

                // Then
                val state = viewModel.uiState.value
                state.shouldBeInstanceOf<NewsUiState.Error>()
                state.message shouldBe errorMessage
                state.articles shouldBe articles
            }
        }

        it("should show default error message when error message is null") {
            runTest(testDispatcher) {
                // Given
                val articles = emptyList<Article>()
                every { topHeadlinesUseCase() } returns flowOf(
                    AppNetworkResult.Failed(articles, null)
                )

                // When
                val viewModel = NewsViewModel(topHeadlinesUseCase)
                advanceUntilIdle()

                // Then
                val state = viewModel.uiState.value
                state.shouldBeInstanceOf<NewsUiState.Error>()
                state.message shouldBe "Unable to load news. Please try again later."
            }
        }

        it("should clear error and show success when articles exist") {
            runTest(testDispatcher) {
                // Given
                val articles = listOf(
                    Article(
                        source = Source(id = "1", name = "Test Source"),
                        author = "Test Author",
                        title = "Test Title",
                        description = "Test Description",
                        url = "https://test.com",
                        urlToImage = null,
                        publishedAt = "2024-01-01",
                        content = "Test Content"
                    )
                )
                every { topHeadlinesUseCase() } returns flowOf(
                    AppNetworkResult.Failed(articles, "Error message")
                )

                // When
                val viewModel = NewsViewModel(topHeadlinesUseCase)
                advanceUntilIdle()

                // Verify error state first
                viewModel.uiState.value.shouldBeInstanceOf<NewsUiState.Error>()

                // Clear error
                viewModel.sendIntent(NewsIntent.ClearError)
                advanceUntilIdle()

                // Then
                val state = viewModel.uiState.value
                state.shouldBeInstanceOf<NewsUiState.Success>()
                state.articles shouldBe articles
            }
        }

        it("should clear error and show initial state when no articles exist") {
            runTest(testDispatcher) {
                // Given
                val articles = emptyList<Article>()
                every { topHeadlinesUseCase() } returns flowOf(
                    AppNetworkResult.Failed(articles, "Error message")
                )

                // When
                val viewModel = NewsViewModel(topHeadlinesUseCase)
                advanceUntilIdle()

                // Verify error state first
                viewModel.uiState.value.shouldBeInstanceOf<NewsUiState.Error>()

                // Clear error
                viewModel.sendIntent(NewsIntent.ClearError)
                advanceUntilIdle()

                // Then
                val state = viewModel.uiState.value
                state shouldBe NewsUiState.Initial
            }
        }

        it("should handle refresh news intent") {
            runTest(testDispatcher) {
                // Given
                val articles = listOf(
                    Article(
                        source = Source(id = "1", name = "Test Source"),
                        author = "Test Author",
                        title = "Test Title",
                        description = "Test Description",
                        url = "https://test.com",
                        urlToImage = null,
                        publishedAt = "2024-01-01",
                        content = "Test Content"
                    )
                )
                every { topHeadlinesUseCase() } returns flowOf(
                    AppNetworkResult.Success(articles)
                )

                // When
                val viewModel = NewsViewModel(topHeadlinesUseCase)
                advanceUntilIdle()

                // Send refresh intent
                viewModel.sendIntent(NewsIntent.RefreshNews)
                advanceUntilIdle()

                // Then
                val state = viewModel.uiState.value
                state.shouldBeInstanceOf<NewsUiState.Success>()
            }
        }
    }
})
