package com.example.recorder.data.websocket

import com.example.recorder.ui.MainWindow.SocketViewModel
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject
import timber.log.Timber

class SocketListener(private val viewModel: SocketViewModel): WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        webSocket.send("{ \"message\": \"Smartphone connected\" }")
        Timber.tag("Attempt").d("connected")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        val parser = JSONObject(
            text.substring(text.indexOf("{"), text.lastIndexOf("}") + 1)
        ).getString("message")

        Timber.tag("Attempt").d("111 -> ${parser}")

        viewModel.outputData(parser)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)

    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
    }
}