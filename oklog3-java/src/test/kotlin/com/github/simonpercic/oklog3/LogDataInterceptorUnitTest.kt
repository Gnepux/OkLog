package com.github.simonpercic.oklog3

import com.github.simonpercic.oklog.core.BaseLogDataInterceptorUnitTest
import com.github.simonpercic.oklog.core.LogManager
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import java.io.IOException
import java.util.Locale

/**
 * LogDataInterceptor unit test. Inspired by: https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor.
 *
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
@RunWith(MockitoJUnitRunner::class)
class LogDataInterceptorUnitTest : BaseLogDataInterceptorUnitTest<MockResponse, Request>() {

    @Rule
    @JvmField
    val server = MockWebServer()

    private lateinit var client: OkHttpClient
    private lateinit var url: HttpUrl

    override fun createClient(applicationLogManager: LogManager, networkLogManager: LogManager): String {
        val applicationInterceptor = OkLogInterceptor(applicationLogManager)
        val networkInterceptor = OkLogInterceptor(networkLogManager)

        client = OkHttpClient.Builder()
            .addInterceptor(applicationInterceptor)
            .addNetworkInterceptor(networkInterceptor)
            .build()

        url = server.url("/")

        return String.format(Locale.US, "%s:%d", server.hostName, server.port)
    }

    @Throws(IOException::class)
    override fun newCall(mockResponse: MockResponse) {
        newCall(request().build(), mockResponse)
    }

    @Throws(IOException::class)
    override fun newCall(request: Request, mockResponse: MockResponse) {
        server.enqueue(mockResponse)
        val response = client.newCall(request).execute()
        response.body()!!.close()
    }

    override val urlString: String
        get() = url.toString()

    override val urlPath: String
        get() = url.encodedPath()

    override fun createMockResponse(): MockResponse = MockResponse()

    override fun setMockResponseStatus(mockResponse: MockResponse, status: String): MockResponse =
        mockResponse.setStatus(status)

    override fun setMockResponseBody(mockResponse: MockResponse, body: String): MockResponse =
        mockResponse.setBody(body)

    override fun setMockResponseBody(mockResponse: MockResponse, body: Buffer): MockResponse =
        mockResponse.setBody(body)

    override fun setMockResponsePlainContentTypeHeader(mockResponse: MockResponse): MockResponse =
        mockResponse.setHeader(BaseLogDataInterceptorUnitTest.CONTENT_TYPE, PLAIN)

    override fun setMockResponseHeader(mockResponse: MockResponse, name: String, value: Any): MockResponse =
        mockResponse.setHeader(name, value)

    override fun setMockResponseChunked(mockResponse: MockResponse, body: String, maxChunkSize: Int): MockResponse =
        mockResponse.setChunkedBody(body, maxChunkSize)

    override fun createPlainPostRequest(body: String): Request = request().post(RequestBody.create(PLAIN, body)).build()

    private fun request(): Request.Builder = Request.Builder().url(url)

    @Test
    @Throws(IOException::class)
    fun bodyGetMalformedCharset() {
        bodyGetMalformedCharset(true, false, false)
    }

    companion object {

        private val PLAIN = MediaType.parse(BaseLogDataInterceptorUnitTest.PLAIN_STRING)
    }
}
