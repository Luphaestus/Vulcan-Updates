package luph.vulcanizerv3.updates.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Options{
    data class SingleChoice(val options: Map<String, ImageVector>): Options()
    data class TextInput(val hint: String, val lines: Int?=null, val icon: ImageVector?=null, val regex: Map<String, Regex> = mapOf()): Options()

    data class MultipleChoice(val options: List<String>): Options()
    object DateChoice: Options()
    data class SliderChoice(val sliderOptions: List<String>): Options()
    object ImageChoice: Options()
}

data class SurveyQuestion(
val question: String,
    val description: String = "",
    val options: Options,
    var value: MutableState<String> = mutableStateOf(""),
    var canContinue: MutableState<Boolean> = mutableStateOf(false)
)