package luph.vulcanizerv3.updates.ui.components.info

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import luph.vulcanizerv3.updates.data.ModDetails
import luph.vulcanizerv3.updates.data.infoBoxesData
import luph.vulcanizerv3.updates.ui.components.MarkdownGenerator


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoBoxes(
    infoBoxesData: infoBoxesData
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                shape = ShapeDefaults.Large
            )
            .clickable { infoBoxesData.showDescription.value = true }
    ) {
        Text(
            text = "Description",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )
        if (infoBoxesData.modDetails.READMEsummary.length > 3)
            Text(
                text = buildAnnotatedString {
                    append(
                        infoBoxesData.modDetails.READMEsummary.substring(
                            0,
                            infoBoxesData.modDetails.READMEsummary.length - 3
                        )
                    )
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.5f
                            )
                        )
                    ) {
                        append(infoBoxesData.modDetails.READMEsummary[infoBoxesData.modDetails.READMEsummary.length - 3])
                    }
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.4f
                            )
                        )
                    ) {
                        append(infoBoxesData.modDetails.READMEsummary[infoBoxesData.modDetails.READMEsummary.length - 2])
                    }
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.2f
                            )
                        )
                    ) {
                        append(infoBoxesData.modDetails.READMEsummary[infoBoxesData.modDetails.READMEsummary.length - 1])
                    }
                    append("...more")
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = 8.dp,
                    bottom = 16.dp,
                    end = 16.dp
                )
            )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                shape = ShapeDefaults.Large
            )
            .clickable {infoBoxesData.showVersion.value = true }
    ) {
        Text(
            text = "Change Log",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )
        if (infoBoxesData.modDetails.changeLogSummary.length > 3)
            Text(
                text = buildAnnotatedString {
                    append(
                        infoBoxesData.modDetails.changeLogSummary.substring(
                            0,
                            infoBoxesData.modDetails.changeLogSummary.length - 3
                        )
                    )
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.5f
                            )
                        )
                    ) {
                        append(infoBoxesData.modDetails.changeLogSummary[infoBoxesData.modDetails.changeLogSummary.length - 3])
                    }
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.4f
                            )
                        )
                    ) {
                        append(infoBoxesData.modDetails.changeLogSummary[infoBoxesData.modDetails.changeLogSummary.length - 2])
                    }
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.2f
                            )
                        )
                    ) {
                        append(infoBoxesData.modDetails.changeLogSummary[infoBoxesData.modDetails.changeLogSummary.length - 1])
                    }
                    append("...more")
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = 8.dp,
                    bottom = 16.dp,
                    end = 16.dp
                )
            )
        if (infoBoxesData.showDescription.value || infoBoxesData.showVersion.value || infoBoxesData.showErrorText.value.isNotEmpty()) {
            ModalBottomSheet(onDismissRequest = {
                infoBoxesData.showDescription.value = false
                infoBoxesData.showVersion.value = false
                infoBoxesData.showErrorText.value = ""
            }) {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    if (infoBoxesData.showDescription.value)
                        item { MarkdownGenerator(markdown = infoBoxesData.modDetails.README) }
                    else if (infoBoxesData.showVersion.value) {
                        item {
                            val versionHtml = """
<h2 style="text-align:center;">Version ${infoBoxesData.modDetails.version}</h2>
<hr>
<br>
""" + infoBoxesData.modDetails.changeLog
                            MarkdownGenerator(markdown = versionHtml)
                        }
                    } else if (infoBoxesData.showErrorText.value.isNotEmpty()) {
                        item {
                            Text(infoBoxesData.showErrorText.value)
                        }
                    }
                }
            }

        }
    }
}
