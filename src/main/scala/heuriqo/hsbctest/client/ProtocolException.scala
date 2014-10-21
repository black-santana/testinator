package heuriqo.hsbctest.client

/**
 * For marking violations of the protocol (=server bugs) we discover while operating the bot.
 */
class ProtocolException(msg: String) extends Exception(msg) {

}
