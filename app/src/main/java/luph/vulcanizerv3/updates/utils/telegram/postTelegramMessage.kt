package luph.vulcanizerv3.updates.utils.telegram

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

fun postTelegramMessage(
    message: String,
    chatId: String,
    token: String
) {
    Thread {
        val client = OkHttpClient()
        val url = "https://api.telegram.org/bot$token/sendMessage"
        val json = """
        {
            "chat_id": "$chatId",
            "text": "$message"
        }
    """.trimIndent()

        val body = json.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        val response = client.newCall(request).execute()

    }.start()
}