package heuriqo.hsbctest.client

import java.net.URL

import heuriqo.hsbctest.BaseSpec

class TestinatorClientSpec extends BaseSpec {

  trait CommonSetup {
    val httpClientMock = mock[HttpClient]
    val host = "foo.com"
    val port = 13
    val userAlias = "bob"
    val url = new URL("HTTP", host, port, "/startTest/" + userAlias)
    val testinatorClient = new TestinatorClientImpl(httpClientMock)
  }

  "Testinator client" must "issue correct HTTP GET request while opening connection to server" in new CommonSetup {
    (httpClientMock.get _).expects(url)
    testinatorClient.connectTo("foo.com", 13, "bob")
  }

  "Testinator client" must "correctly parse the <session token> response from server" in new CommonSetup {
    (httpClientMock.get _).expects(url).returns("Hi bob. Your token is: bob306")
    testinatorClient.connectTo("foo.com", 13, "bob")
  }

}
