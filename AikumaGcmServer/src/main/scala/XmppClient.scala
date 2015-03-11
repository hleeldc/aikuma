import org.jivesoftware.smack._
import org.jivesoftware.smack.tcp._
import org.jivesoftware.smack.packet._
import org.jivesoftware.smack.filter._
import javax.net._

object XmppClient {
  def main(argv: Array[String]): Unit = {
    val host = "gcm-preprod.googleapis.com"
    val port = 5236
    val config = new ConnectionConfiguration(host, port)
    config.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled)
    config.setReconnectionAllowed(true)
    config.setRosterLoadedAtLogin(false)
    config.setSendPresence(false)
    config.setSocketFactory(SocketFactory.getDefault)
    val con = new XMPPTCPConnection(config)
    con.addConnectionListener(Listener)
    con.addPacketListener(PktListener, new PacketTypeFilter(classOf[Message]))
    con.addPacketInterceptor(PktInterceptor, new PacketTypeFilter(classOf[Message]))
    con.connect
  }
}

object PktListener extends PacketListener {
  def processPacket(packet: Packet) {
    println("got packet")
  }
}

object PktInterceptor extends PacketInterceptor {
  def interceptPacket(packet: Packet) {
    println("intercepted packet")
  }
}

object Listener extends ConnectionListener {
  def connected(con: XMPPConnection) {
    println("connected")
  }
  def authenticated(con: XMPPConnection) {
    println("authenticated")
  }
  def reconnectionSuccessful {
    println("reconnecting")
  }
  def reconnectionFailed(e: Exception) {
    println("reconnection failed:  " + e.getMessage)
  }
  def reconnectingIn(n: Int) {
    println(s"reconnecting in $n seconds")
  }
  def connectionClosedOnError(e: Exception) {
    println("connection closed on error: " + e.getMessage)
  }
  def connectionClosed {
    println("connection closed")
  }
}

