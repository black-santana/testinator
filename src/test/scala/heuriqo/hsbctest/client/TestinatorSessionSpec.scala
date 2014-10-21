package heuriqo.hsbctest.client

import java.net.URL

import heuriqo.hsbctest.BaseSpec

class TestinatorSessionSpec extends BaseSpec {

  trait CommonSetup {
    val httpClientMock = mock[HttpClient]
    val host = "foo.com"
    val port = 13
    val sessionToken = "wojtek501"
    val session = new TestinatorSessionImpl(httpClientMock, host, port, sessionToken)
  }

  "TestinatorSession" must "correctly translate <ask next question> request to HTTP layer" in new CommonSetup {
    (httpClientMock.get _).expects(new URL("HTTP", host, port, "/wojtek501/nextQuestion"))
    session.askNextQuestion()
  }

  it must "correctly translate <send answer> request to HTTP layer" in new CommonSetup {
    (httpClientMock.get _).expects(new URL("HTTP", host, port, "/wojtek501/answer/42"))
    session.sendAnswer("42")
  }

}
