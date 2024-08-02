import android.os.AsyncTask
import java.io.OutputStream
import java.net.Socket

class SendMessageTask(private val serverIp: String, private val port: Int) : AsyncTask<String, Void, Void>() {
    override fun doInBackground(vararg params: String?): Void? {
        val message = params[0] ?: return null

        try {
            Socket(serverIp, port).use { socket ->
                socket.getOutputStream().use { outputStream ->
                    outputStream.write(message.toByteArray())
                    outputStream.flush()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}
