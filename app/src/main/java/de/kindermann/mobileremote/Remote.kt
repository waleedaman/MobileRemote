package de.kindermann.mobileremote

import android.app.Instrumentation
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.button.MaterialButton
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.*
import java.net.InetSocketAddress
import io.ktor.client.features.logging.*
import io.ktor.http.*
import io.ktor.http.HttpMethod.Companion.Get
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject


class Remote : AppCompatActivity() {
    private lateinit var client:HttpClient
    private lateinit var ip:String
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote)
        ip= intent.getStringExtra("IP").toString()
        client = HttpClient(OkHttp) {
            install(Logging)
        }

        connect(ip,"test","/")
        val upbtn = findViewById<MaterialButton>(R.id.btnup)
        upbtn.setOnClickListener {
            connect(ip,KeyEvent.KEYCODE_DPAD_UP.toString(),"/command")
        }

        val leftBtn: MaterialButton = findViewById<MaterialButton>(R.id.leftBtn)
        leftBtn.setOnClickListener {
            connect(ip,KeyEvent.KEYCODE_DPAD_LEFT.toString(),"/command")
        }

        val rightBtn: MaterialButton = findViewById<MaterialButton>(R.id.rightBtn)
        rightBtn.setOnClickListener {
            connect(ip,KeyEvent.KEYCODE_DPAD_RIGHT.toString(),"/command")
        }

        val okBtn: MaterialButton = findViewById<MaterialButton>(R.id.okBtn)
        okBtn.setOnClickListener {
            connect(ip,KeyEvent.KEYCODE_DPAD_CENTER.toString(),"/command")
        }

        val downBtn: MaterialButton = findViewById<MaterialButton>(R.id.downBtn)
        downBtn.setOnClickListener {
            connect(ip,KeyEvent.KEYCODE_DPAD_DOWN.toString(),"/command")
        }

        val menuBtn: MaterialButton = findViewById<MaterialButton>(R.id.menu)
        menuBtn.setOnClickListener {
            connect(ip,KeyEvent.KEYCODE_MENU.toString(),"/command")
        }

        val homeBtn: MaterialButton = findViewById<MaterialButton>(R.id.home)
        homeBtn.setOnClickListener {
            connect(ip,KeyEvent.KEYCODE_HOME.toString(),"/command")
        }

        val backBtn: MaterialButton = findViewById<MaterialButton>(R.id.back)
        backBtn.setOnClickListener {
            connect(ip,KeyEvent.KEYCODE_BACK.toString(),"/command")
        }

        val volDown: MaterialButton = findViewById<MaterialButton>(R.id.voldown)
        volDown.setOnClickListener {
            connect(ip,KeyEvent.KEYCODE_VOLUME_DOWN.toString(),"/command")
        }

        val volUp: MaterialButton = findViewById<MaterialButton>(R.id.volup)
        volUp.setOnClickListener {
            connect(ip,KeyEvent.KEYCODE_VOLUME_UP.toString(),"/command")
        }

        val muteBtn: MaterialButton = findViewById<MaterialButton>(R.id.mute)
        muteBtn.setOnClickListener {
            connect(ip,KeyEvent.KEYCODE_MUTE.toString(),"/command")
        }
    }




    private suspend fun connect(ktor: HttpClient, u: Url,command:String) {
        ktor.ws(Get, u.host, u.port, u.encodedPath) {
            send(Frame.Text(command))
            when (val frame = incoming.receive()) {
                is Frame.Text -> println(frame.readText())
            }
        }
    }

    private fun connect(ip:String,command:String,path:String) {
        val url = Url("ws://${ip}:8080${path}")
        val ktor = HttpClient(OkHttp) {
            install(WebSockets)
        }
        CoroutineScope(Dispatchers.Default).launch{
            connect(ktor, url,command)
        }
    }

}
