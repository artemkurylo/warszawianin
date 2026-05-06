package pl.warszawianin.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * AI module — GeminiService is now a simple @Inject constructor class (no external deps).
 * This module exists as a placeholder for future real AI integration.
 */
@Module
@InstallIn(SingletonComponent::class)
object AiModule
