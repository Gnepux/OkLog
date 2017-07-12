package com.github.simonpercic.oklog3

import android.support.test.runner.AndroidJUnit4
import com.github.simonpercic.oklog.core.BaseOkLogInterceptorTest
import com.github.simonpercic.oklog.core.LogInterceptor
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import org.junit.rules.ExternalResource
import org.junit.runner.RunWith
import java.io.IOException

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
@RunWith(AndroidJUnit4::class)
class OkLogInterceptorTest : BaseOkLogInterceptorTest<MockWebServer, HttpUrl, MockResponse, Request.Builder, Request, RequestBody, OkHttpClient, OkLogInterceptor.Builder, OkLogInterceptor>() {

    private val mockWebServer = MockWebServer()

    override val server: MockWebServer
        get() = mockWebServer

    @Rule @JvmField val serverPort = MockWebServerPort(server)

    override fun mockWebServerUrl(server: MockWebServer, path: String): HttpUrl = server.url(path)

    override fun mockWebServerEnqueue(server: MockWebServer, response: MockResponse) = server.enqueue(response)

    override fun mockResponse(): MockResponse = MockResponse()

    override fun mockResponseSetBody(mockResponse: MockResponse, body: String): MockResponse = mockResponse.setBody(body)

    override fun mockResponseAddHeader(mockResponse: MockResponse, name: String, value: Any): MockResponse = mockResponse.addHeader(name, value)

    override fun mockResponseSetStatus(mockResponse: MockResponse, status: String): MockResponse = mockResponse.setStatus(status)

    override fun request(url: HttpUrl): Request.Builder = Request.Builder().url(url)

    override fun requestAddHeader(requestBuilder: Request.Builder, name: String, value: String): Request.Builder = requestBuilder.addHeader(name, value)

    override fun requestPost(requestBuilder: Request.Builder, body: RequestBody): Request.Builder = requestBuilder.post(body)

    override fun requestPut(requestBuilder: Request.Builder, body: RequestBody): Request.Builder = requestBuilder.put(body)

    override fun requestHead(requestBuilder: Request.Builder): Request.Builder = requestBuilder.head()

    override fun requestDelete(requestBuilder: Request.Builder, body: RequestBody?): Request.Builder = requestBuilder.delete(body)

    override fun requestBuild(requestBuilder: Request.Builder): Request = requestBuilder.build()

    override fun clientNewCallExecute(client: OkHttpClient, request: Request) {
        val response = client.newCall(request).execute()
        response.body()!!.close()
    }

    override fun okHttpClientWithInterceptor(interceptor: OkLogInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    override fun createRequestBody(body: String): RequestBody = RequestBody.create(MediaType.parse(PLAIN_STRING), body)

    override fun okLogInterceptorBuilder(): OkLogInterceptor.Builder = OkLogInterceptor.builder()

    override fun okLogInterceptorBuilderSetLogInterceptor(okLogInterceptorBuilder: OkLogInterceptor.Builder, logInterceptor: LogInterceptor): OkLogInterceptor.Builder = okLogInterceptorBuilder.setLogInterceptor(logInterceptor)

    override fun okLogInterceptorBuilderWithResponseDuration(okLogInterceptorBuilder: OkLogInterceptor.Builder, responseDuration: Boolean): OkLogInterceptor.Builder = okLogInterceptorBuilder.withResponseDuration(responseDuration)

    override fun okLogInterceptorBuilderWithAllLogData(okLogInterceptorBuilder: OkLogInterceptor.Builder): OkLogInterceptor.Builder = okLogInterceptorBuilder.withAllLogData()

    override fun okLogInterceptorBuild(okLogInterceptorBuilder: OkLogInterceptor.Builder): OkLogInterceptor = okLogInterceptorBuilder.build()

    class MockWebServerPort(private val mockWebServer: MockWebServer) : ExternalResource() {

        override fun before() {
            super.before()
            mockWebServer.start(5000)
        }

        override fun after() {
            super.after()
            try {
                mockWebServer.shutdown()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }
}
