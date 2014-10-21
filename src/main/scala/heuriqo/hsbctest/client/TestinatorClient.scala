package heuriqo.hsbctest.client

import scala.util.Try

/**
 * Encapsulates client side of the game protocol.
 * In practive this works as sessions factory.
 */
trait TestinatorClient {

  /**
   * Connects to target host running the game server using HTTP protocol.
   * Of successfull, returns session (with sessin token).
   *
   * @param host target host
   * @param port port
   * @param userAlias user name
   * @return session or error
   */
  def connectTo(host: String, port: Int, userAlias: String): Try[TestinatorSession]
}
