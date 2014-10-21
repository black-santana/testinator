package heuriqo.hsbctest.client

import java.net.URL

import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}

class TestinatorSessionImpl(httpClient: HttpClient, host: String, port: Int, override val token: String) extends TestinatorSession {
  private val log = LoggerFactory.getLogger("session")

  @throws[ConnectionToServerFailed]
  override def askNextQuestion(): Try[String] = Try {
    val url = new URL("HTTP", host, port, "/" + token + "/nextQuestion")
    try {
      log.debug("asking next question: " + url)
      httpClient.get(url)
    } catch {
      case ex: Exception => throw new ConnectionToServerFailed(ex)
    }
  }

  @throws[ConnectionToServerFailed]
  override def sendAnswer(answer: String): Try[AnswerResponse] = {
    val url = new URL("HTTP", host, port, "/" + token + "/answer/" + answer)
    try {
      log.debug("sending answer: " + url)
      httpClient.get(url).toLowerCase match {
        case "pass" => Success(AnswerResponse.PASS)
        case "fail" => Success(AnswerResponse.FAIL)
        case other => Failure(new ProtocolException("unexpected server response: " + other))
      }
    } catch {
      case ex: Exception => Failure(new ConnectionToServerFailed(ex))
    }
  }
}
