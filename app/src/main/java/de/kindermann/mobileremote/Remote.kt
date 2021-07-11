package de.kindermann.mobileremote

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Remote : AppCompatActivity() {
    private lateinit var client: HttpClient;

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote)
        val ip=intent.getStringExtra("IP")

        client = HttpClient(Android)
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                if (ip != null) {
                    sendCommand(
                        "test",
                        client,
                        "/test",
                        ip
                    )
                }
            }
        }
    }
    suspend fun sendCommand(command:String, client: HttpClient, path:String,ip:String){
        client.ws(
            method = HttpMethod.Get,
            host = ip,
            port = 8080, path = path
        ) { // this: DefaultClientWebSocketSession

            // Send text frame.
            send(Frame.Text(command))

            // Receive frame.
            when (val frame = incoming.receive()) {
                is Frame.Text -> println(frame.readText())
                is Frame.Binary -> println(frame.readBytes())
            }
        }
    }
}