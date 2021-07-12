package de.kindermann.mobileremote

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.URI

class Remote : AppCompatActivity() {
    private lateinit var webSocketClient: WebSocketClient
    private lateinit var ip:String
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote)
        ip= intent.getStringExtra("IP").toString()
        initWebSocket()

    }

    override fun onResume() {
        super.onResume()
        initWebSocket()
    }

    override fun onPause() {
        super.onPause()
        webSocketClient.close()
    }

    private fun initWebSocket() {
        val uri: URI = URI("ws://${ip}:4444/command")
        Log.d("TAG", uri.toString())
        createWebSocketClient(uri)
    }

    private fun createWebSocketClient(uri: URI?) {
        webSocketClient = object : WebSocketClient(uri) {

            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d("TAG", "onOpen")
                subscribe()
            }

            override fun onMessage(message: String?) {
                Log.d("TAG", "onMessage: $message")
                //setUpBtcPriceText(message)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d("TAG", "onClose")
                //unsubscribe()
            }

            override fun onError(ex: Exception?) {
                Log.e("createWebSocketClient", "onError: ${ex?.message}")
            }

            private fun subscribe() {
                webSocketClient.send(
                    "Hello"
                )
            }

        }
    }


    suspend fun sendCommand(command:String, client: HttpClient, path:String,ip:String){
        client.ws(
            host = ip,
            port = 4444, path = path
        ) { // this: DefaultClientWebSocketSession

            // Send text frame.
            //send(Frame.Text(command))

            // Receive frame.
            when (val frame = incoming.receive()) {
                is Frame.Text -> println(frame.readText())
                is Frame.Binary -> println(frame.readBytes())
            }
        }
    }
}
