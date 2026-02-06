package luph.vulcanizerv3.updates.utils

import android.content.Context
import android.util.Log
import luph.vulcanizerv3.updates.MainActivity
import java.io.File
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable


class SerializableManager<T : Serializable> {

    fun save(fileName: String, obj: T) {
        try {
            val context: Context = MainActivity.applicationContext()
            val fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(obj)
            objectOutputStream.close()
            fileOutputStream.close()
        } catch (e: IOException) {
        }
    }

    fun load(fileName: String): T? {
        return if (!File(MainActivity.applicationContext().filesDir, fileName).exists()) {
            null
        } else try {
            val context: Context = MainActivity.applicationContext()
            val fileInputStream = context.openFileInput(fileName)
            val objectInputStream = ObjectInputStream(fileInputStream)
            val obj = objectInputStream.readObject() as T
            objectInputStream.close()
            fileInputStream.close()
            obj
        } catch (e: IOException) {
            Log.e("SerializableManager", "Error loading object", e)
            null
        } catch (e: ClassNotFoundException) {
            Log.e("SerializableManager", "Class not found", e)
            null
        }
    }

    fun delete(fileName: String) {
        try {
            val context: Context = MainActivity.applicationContext()
            context.deleteFile(fileName)
        } catch (e: IOException) {
            Log.e("SerializableManager", "Error deleting file", e)
        }
    }
}