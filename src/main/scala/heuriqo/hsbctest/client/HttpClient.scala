package heuriqo.hsbctest.client

import java.net.URL

/**
 * HTTP client abstraction as needed for playing the game.
 */
trait HttpClient {
  def get(url: URL): String
}
