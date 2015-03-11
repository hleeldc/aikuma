import org.jivesoftware.smack._
import org.jivesoftware.smack.tcp._
import org.jivesoftware.smack.packet._
import org.jivesoftware.smack.filter._
import org.jivesoftware.smack.provider.ProviderManager
import org.json.simple._
import javax.net.ssl._
import java.util.logging.{Logger,Level}
import java.util.{Map => JMap}
import collection.JavaConversions._

object XmppClient {
  val logger = Logger.getLogger("XmppClient")
  val host = "gcm-preprod.googleapis.com"
  val port = 5236
  val senderId = "780493739997@gcm.googleapis.com"
  val apiSecret = ""

  val con = connect
  protected var connectionDraining = false


  def main(argv: Array[String]): Unit = {
    while (true) {
      Thread.sleep(1000);
    }
  }

  def connect: XMPPTCPConnection = {
    ProviderManager.addExtensionProvider(
      GcmPacketExtension.GCM_ELEMENT_NAME,
      GcmPacketExtension.GCM_NAMESPACE,
      GcmPacketExtensionProvider)

    val config = new ConnectionConfiguration(host, port)
    config.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled)
    config.setReconnectionAllowed(true)
    config.setRosterLoadedAtLogin(false)
    config.setSendPresence(false)
    config.setSocketFactory(SSLSocketFactory.getDefault)

    val con = new XMPPTCPConnection(config)
    con.addConnectionListener(ConListener)
    con.addPacketListener(PktListener, new PacketTypeFilter(classOf[Message]))
    con.addPacketInterceptor(PktInterceptor, new PacketTypeFilter(classOf[Message]))
    con.connect
    con.login(senderId, apiSecret)

    con
  }

  def send(json: String): Unit = {
    val request = new GcmPacketExtension(json).toPacket
    con.sendPacket(request)
  }

  object PktListener extends PacketListener {
    def processPacket(packet: Packet) {
      println(s"got packet from ${packet.getFrom}")
      val gcmPacket = packet.asInstanceOf[Message]
        .getExtension(GcmPacketExtension.GCM_NAMESPACE)
        .asInstanceOf[GcmPacketExtension]
      val json = gcmPacket.getJson
      logger.info(json)

      try {
        val obj = JSONValue.parseWithException(json).asInstanceOf[JMap[String,String]]
        obj.getOrElse("message_type", null) match {
          case null => send(JSONValue.toJSONString(Map(
            "message_type" -> "ack",
            "message_id" -> obj("message_id"),
            "to" -> obj("from")
          ): JMap[String,String]))
          case "ack" => {
            val msg = s"ack from ${obj("from")} message id: ${obj("message_id")}"
            logger.info(msg)
          }
          case "nack" => {
            val msg = s"nack from ${obj("from")} message id: ${obj("message_id")}"
            logger.info(msg)
          }
          case "control" => {
            obj("control_type") match {
              case "CONNECTION_TRAINING" => connectionDraining = true
              case t: String => logger.log(Level.INFO, "unknown control type %s", t)
            }
          }
          case t => logger.log(Level.INFO, "unknown message type %s", t)
        }
      } catch {
        case e: org.json.simple.parser.ParseException =>
          logger.log(Level.SEVERE, "Error parsing json: " + json, e)
        case e: Exception =>
          logger.log(Level.SEVERE, "Failed to process packet", e)
      }
    }
  }

  object PktInterceptor extends PacketInterceptor {
    def interceptPacket(packet: Packet) {
      println("intercepted packet")
    }
  }

  object ConListener extends ConnectionListener {
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

}
