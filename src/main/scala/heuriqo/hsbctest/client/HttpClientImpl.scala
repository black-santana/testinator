package heuriqo.hsbctest.client

import java.net.URL

import io.shaka.http.Http._
import io.shaka.http.Request.GET

class HttpClientImpl extends HttpClient {
  override def get(url: URL): String = http(GET(url.toString)).entityAsString
}
