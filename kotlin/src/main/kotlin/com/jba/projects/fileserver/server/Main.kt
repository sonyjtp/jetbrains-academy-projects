package server

import common.PORT
import common.getIpAddress
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket
import java.net.Socket

private lateinit var serverSocket: ServerSocket
private lateinit var socket: Socket
private lateinit var input: DataInputStream
private lateinit var output: DataOutputStream

fun main(args: Array<String>) {
    println("Server started!")
    listen()
    receive()
}

private fun listen() {
    serverSocket = ServerSocket(PORT, 50, getIpAddress())
    socket = serverSocket.accept()
    input = DataInputStream(socket.getInputStream())
}

private fun receive() {
    val message = input.readUTF()
    println("Received: $message")
    output = DataOutputStream(socket.getOutputStream())
    ack("All files were sent!")
}

private fun ack(message: String) {
    output.writeUTF(message)
    println("Sent: $message")
}


