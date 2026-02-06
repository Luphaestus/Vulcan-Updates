package luph.vulcanizerv3.updates.utils.root

import android.util.Log

enum class ROOTStatus {
    KSU,
    MAGISK,
    NONE
}


fun getROOTStatus(): ROOTStatus {

    val isROOTED = runRootShellCommand("echo test").value
    if (!isROOTED.second) {
        return ROOTStatus.NONE
    }
    else {
        val isMAgisk = runRootShellCommand("magisk -c").value
        Log.d("ROOTStatus", isMAgisk.toString())
        return if (isMAgisk.second) ROOTStatus.MAGISK
        else ROOTStatus.KSU
    }
}

