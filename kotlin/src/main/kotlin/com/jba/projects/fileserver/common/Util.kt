package common

import java.net.InetAddress

fun getIpAddress(): InetAddress = InetAddress.getByName(ADDRESS)
