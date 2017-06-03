package com.github.simonpercic.oklog3

import android.support.test.runner.AndroidJUnit4
import com.github.simonpercic.oklog.core.LogInterceptor
import junit.framework.Assert
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import java.io.IOException

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
@RunWith(AndroidJUnit4::class)
class OkLogInterceptorTest {

    protected val PLAIN_STRING = "text/plain; charset=utf-8"

    private val server = MockWebServer()
    @Rule @JvmField val serverPort = MockWebServerPort(server)

    private var client: OkHttpClient? = null
    private var url: HttpUrl? = null

    @Test
    fun testGetDefault() {
        val urlCaptor = ArgumentCaptor.forClass(String::class.java)

        val logInterceptor = Mockito.mock(LogInterceptor::class.java)
        Mockito.`when`(logInterceptor.onLog(urlCaptor.capture())).thenReturn(true)

        val interceptor = OkLogInterceptor.builder()
            .setLogInterceptor(logInterceptor)
            .withResponseDuration(false)
            .build()

        client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        url = server.url("/shows")

        val mockResponse = MockResponse()
            .setBody("[{\"id\":1,\"name\":\"Under the Dome\",\"runtime\":60,\"network\":{\"id\":2,\"name\":\"CBS\"" +
                "}},{\"id\":2,\"name\":\"Person of Interest\",\"runtime\":60,\"network\":{\"id\":2,\"name\":\"CBS\"" +
                "}},{\"id\":4,\"name\":\"Arrow\",\"runtime\":60,\"network\":{\"id\":5,\"name\":\"The CW\"}},{\"id\"" +
                ":5,\"name\":\"True Detective\",\"runtime\":60,\"network\":{\"id\":8,\"name\":\"HBO\"}},{\"id\"" +
                ":6,\"name\":\"The 100\",\"runtime\":60,\"network\":{\"id\":5,\"name\":\"The CW\"}},{\"id\"" +
                ":7,\"name\":\"Homeland\",\"runtime\":60,\"network\":{\"id\":9,\"name\":\"Showtime\"}}]")

        newCall(request().build(), mockResponse)

        val loggedUrl = urlCaptor.value
        Assert.assertEquals(
            "http://responseecho-simonpercic.rhcloud.com/v1/r/H4sIAAAAAAAAAIuuVspMUbIy1FHKS8xNVbJSCs1LSS1SKMlIVXDJBw" +
                "roKBWV5pVkgqTMDICKUkvK84uylawg2ozg2pydgpVqa3XQhQNSi4rz8xTy0xQ880pSi1KLS8gz0QQu7FhUlF9OwBBTuOoQoD-cw" +
                "xHmIMkUlQK9mFqSmlySWUbInxZwbR5O_gjTzFDsMTQwINtd5ggLgKGek5iXQsAoS7iG4Iz8crCy2tpYALkEI-7OAQAA?d=H4sIA" +
                "AAAAAAAAONidncNEZLOKCkpsNLXz8lPTszJyC8usTI1MDDQL87ILy_WYLBgcmDwOMEYxOTvnXCOuYCx4hwzAHrELHA3AAAA",
            loggedUrl)
    }

    @Test
    fun testPostDefault() {
        val urlCaptor = ArgumentCaptor.forClass(String::class.java)

        val logInterceptor = Mockito.mock(LogInterceptor::class.java)
        Mockito.`when`(logInterceptor.onLog(urlCaptor.capture())).thenReturn(true)

        val interceptor = OkLogInterceptor.builder()
            .setLogInterceptor(logInterceptor)
            .withResponseDuration(false)
            .build()

        client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        url = server.url("/watched")

        val mockResponse = MockResponse()
            .setBody("{\"show\":{\"id\":5,\"name\":\"True Detective\",\"runtime\":60,\"network\":{\"id\":8,\"name\"" +
                ":\"HBO\"}},\"watched_count\":107}")

        newCall(request().post(RequestBody.create(MediaType.parse(PLAIN_STRING), "{\"show\":5}")).build(), mockResponse)

        val loggedUrl = urlCaptor.value
        Assert.assertEquals(
            "http://responseecho-simonpercic.rhcloud.com/v1/r/H4sIAAAAAAAAAKtWKs7IL1eyqlbKTFGyMtVRykvMTVWyUgopKk1VcE" +
                "ktSU0uySxLVdJRKirNK8kESZkZABWllpTnF2XDtFnAtXk4-SvV1uoolSeWJGekpsQn5wO1KVkZGpjXAgANYYQRagAAAA==?qb=H" +
                "4sIAAAAAAAAAKtWKs7IL1eyMq0FAFlhsMYKAAAA&d=H4sIAAAAAAAAAONiCfAPDhGSzSgpKbDS18_JT07MycgvLrEyNTAw0C9PL" +
                "EnOSE3R4LJgdGDwOMEYxOTvnZBVwFiRBQDstqnDOAAAAA==",
            loggedUrl)
    }

    private fun request(): Request.Builder {
        return Request.Builder().url(url!!)
    }

    @Throws(IOException::class)
    private fun newCall(request: Request, mockResponse: MockResponse) {
        server.enqueue(mockResponse)
        val response = client!!.newCall(request).execute()
        response.body()!!.close()
    }

    class MockWebServerPort(private val mockWebServer: MockWebServer) : ExternalResource() {

        @Throws(Throwable::class)
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
