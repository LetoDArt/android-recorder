package com.example.recorder.data.networking

import com.example.recorder.data.auth.AuthRepository
import com.example.recorder.data.auth.TokenStorage
import com.example.recorder.data.auth.model.RefreshRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber

class AuthorizationFailedInterceptor(
    private val TS: TokenStorage,
    private val AR: AuthRepository,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        return originalResponse
            .takeIf { it.code != 401 }
            ?: handleUnauthorizedResponse(chain, originalResponse)
    }

    private fun handleUnauthorizedResponse(
        chain: Interceptor.Chain,
        originalResponse: Response,
    ): Response {
        return handleTokenNeedRefresh(chain) ?: originalResponse
    }

    private fun handleTokenNeedRefresh(
        chain: Interceptor.Chain
    ): Response? {
        return if (refreshToken()) {
            updateTokenAndProceedChain(chain)
        } else {
            null
        }
    }

    private fun refreshToken(): Boolean {
        val tokenRefreshed = runBlocking {
            runCatching {
                AR.refreshTokens(RefreshRequest(TS.refreshToken))
            }
                .getOrNull()
                ?.let { tokens ->
                    TokenStorage.saveAccessToken(tokens.access)
                    TokenStorage.saveRefreshToken(tokens.refresh)
                    true
                } ?: false
        }

        if (!tokenRefreshed) {
            // не удалось обновить токен, произвести логаут
//            unauthorizedHandler.onUnauthorized()
            Timber.d("logout after token refresh failure")
        }
        return tokenRefreshed
    }

    private fun updateTokenAndProceedChain(
        chain: Interceptor.Chain
    ): Response {
        val newRequest = updateOriginalCallWithNewToken(chain.request())
        return chain.proceed(newRequest)
    }

    private fun updateOriginalCallWithNewToken(request: Request): Request {
        return request
                .newBuilder()
                .header("Authorization", TS.accessToken)
                .build()
    }
}
