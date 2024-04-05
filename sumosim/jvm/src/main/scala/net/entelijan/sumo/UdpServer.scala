package net.entelijan.sumo

import net.entelijan.sumo.reinforcement.NetConnector

import java.net.{DatagramPacket, DatagramSocket, InetAddress}

object UdpServer {

  case class Connection(
      socket: DatagramSocket,
      address: InetAddress,
      port: Int
  )

  private def answer(data: String, connection: Connection): Unit = {
    val array = data.getBytes()
    val packet = new DatagramPacket(
      array,
      array.length,
      connection.address,
      connection.port
    )
    connection.socket.send(packet)
  }

  def start(port: Int): Unit = {
    val socket = new DatagramSocket(port)

    val netConnector = new NetConnector[Connection]()

    try {
      var continue = true
      while (continue) {
        val buf: Array[Byte] = new Array[Byte](256)
        val packet = new DatagramPacket(buf, buf.length)
        println(s"-- Listening on $port")
        socket.receive(packet)
        val data = new String(packet.getData, 0, packet.getLength)
        println(s"-- Data: $data")
        Connection(socket, packet.getAddress, packet.getPort)
        continue = netConnector.receive(
          data,
          Connection(socket, packet.getAddress, packet.getPort),
          answer
        )
      }
    } finally {
      socket.close()
    }
  }

}
