package client

import common.PORT
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

private val socket: Socket = Socket(common.getIpAddress(), PORT)
private val input: DataInputStream = DataInputStream(socket.getInputStream())
private val output: DataOutputStream = DataOutputStream(socket.getOutputStream())


fun main(args: Array<String>) {
    println("Client started!")
    send("Give me everything you have!")
    receive()
}


private fun send(message: String) {
    output.writeUTF(message)
    println("Sent: $message")
}

private fun receive() {
    val message = input.readUTF()
    println("Received: $message")
}
