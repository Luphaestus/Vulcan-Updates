package luph.vulcanizerv3.updates.utils

import android.provider.Settings
import android.util.Log
import luph.vulcanizerv3.updates.MainActivity

fun getAnimationScale (): Float {
    return Settings.Global.getFloat(MainActivity.applicationContext().contentResolver,
    Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f);

}


fun getStandardAnimationSpeed(): Int {
    return (400 * getAnimationScale()).toInt()
}

