package heuriqo.hsbctest.client

import java.net.URL

import org.slf4j.LoggerFactory

import scala.util.Try

class TestinatorClientImpl(httpClient: HttpClient) extends TestinatorClient {
  private val log = LoggerFactory.getLogger("client")

  @throws[ConnectionToServerFailed]
  override def connectTo(host: String, port: Int, userAlias: String): Try[TestinatorSession] = Try {
    val url = new URL("HTTP", host, port, "/startTest/" + userAlias)

    try {
      log.debug("requesting token: " + url)
      val sessionStartResponse = httpClient.get(url)
      val sessionToken = sessionStartResponse.split(' ').last
      new TestinatorSessionImpl(httpClient, host, port, sessionToken)
    } catch {
      case ex: Exception => throw new ConnectionToServerFailed(ex)
    }

  }

}
