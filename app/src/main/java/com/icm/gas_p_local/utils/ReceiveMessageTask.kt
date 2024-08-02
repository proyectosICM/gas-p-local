import android.os.AsyncTask
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket

class ReceiveMessageTask(private val serverIp: String, private val port: Int, private val callback: (String) -> Unit) : AsyncTask<Void, Void, String?>() {
    override fun doInBackground(vararg params: Void?): String? {
        return try {
            val socket = Socket(serverIp, port)
            val input = socket.getInputStream()
            val reader = BufferedReader(InputStreamReader(input))
            val message = reader.readLine()
            socket.close()
            message
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onPostExecute(result: String?) {
        result?.let {
            callback(it)
        }
    }
}
