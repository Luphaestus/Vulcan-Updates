package luph.vulcanizerv3.updates.ui.page.settings.options


import android.util.Log
import android.view.View
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.data.Options
import luph.vulcanizerv3.updates.ui.components.PageNAv
import luph.vulcanizerv3.updates.ui.components.PageNavProgress
import luph.vulcanizerv3.updates.ui.components.SingleOptionsSection
import luph.vulcanizerv3.updates.ui.page.showNavigation
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import luph.vulcanizerv3.updates.data.Options.SingleChoice
import luph.vulcanizerv3.updates.data.SurveyQuestion
import luph.vulcanizerv3.updates.ui.components.SurveyTextINput
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

const val stronglyDeemphasizedAlpha = 0.6f
const val slightlyDeemphasizedAlpha = 0.87f



@Preview(showBackground = true)
@Composable
fun FeedbackOption(
    navController: NavController = rememberNavController(),
    view: View? = null
) {
    showNavigation.show = false

    val sharedForm = mutableListOf(
        SurveyQuestion(
            "Feedback Type",
            "Select the type of feedback you would like to provide",
            SingleChoice(
                mapOf(
                    "Bug Report" to Icons.Outlined.BugReport,
                    "Feature Request" to Icons.Outlined.Add
                )
            ),
            remember { mutableStateOf("") }),

        SurveyQuestion(
            "Telegram Username",
            "Please enter your Telegram username so we can contact you for clarification if needed",
            Options.TextInput(
                hint = "Luphaestus",
                lines = 1,
                icon = Icons.Outlined.AlternateEmail,
                regex = mapOf(
                    "Username must be at least 5 characters long" to Regex(".{5,}"),
                    "Username can contain only letters, numbers, and underscores" to Regex("[a-zA-Z0-9_]+")
                )
            ),
            remember { mutableStateOf("") }),
    )

    val formPages = mapOf<String, MutableList<SurveyQuestion>>(
        "Bug Report" to mutableListOf(
            SurveyQuestion(
                "What is the title of the bug?",
                "Please provide a title for the bug you are reporting",
                Options.TextInput(
                    hint = "Title",
                    lines = 1,
                    regex = mapOf(
                        "Title must be at least 5 characters long" to Regex(".{5,}")
                    )
                )
            ),
            SurveyQuestion(
                "What is the frequency of the bug?",
                "Please describe how often the bug occurs",
                Options.SingleChoice(
                    mapOf(
                        "Always" to Icons.Outlined.Check,
                        "Sometimes" to Icons.Outlined.Schedule,
                        "Rarely" to Icons.Outlined.HourglassEmpty
                    )
                )
            ),
            SurveyQuestion(
                "Does the bug occurs after Formating and removing all KSU modules?",
                "",
                Options.SingleChoice(
                    mapOf(
                        "Yes, it works after formatting" to Icons.Outlined.Check,
                        "No, it does not work after formatting" to Icons.Outlined.Close,
                        "Formatting and removing KSU modules makes it work" to Icons.Outlined.HelpOutline,
                        "I have not tried formatting or removing KSU modules" to Icons.Outlined.HelpOutline
                    )
                )
            ),
            SurveyQuestion(
                "Expected Behavior",
                "Please describe what you expected to happen",
                Options.TextInput(
                    hint = "Expected Behavior",
                    lines = null,
                    regex = mapOf(
                        "Description must be at least 10 characters long" to Regex("(.|\\n){10,}")
                    )
                )
            ),
            SurveyQuestion(
                "Current Behavior",
                "Please describe what actually happened",
                Options.TextInput(
                    hint = "Current Behavior",
                    lines = null,
                    regex = mapOf(
                        "Description must be at least 10 characters long" to Regex("(.|\\n){10,}")
                    )
                )
            ),
            SurveyQuestion(
                "Steps to Reproduce",
                "Please describe the steps to reproduce the bug",
                Options.TextInput(
                    hint = "Steps to Reproduce",
                    lines = null,
                    regex = mapOf(
                        "Description must be at least 10 characters long" to Regex("(.|\\n){10,}")
                    )
                )
            ),
        ),
        "Feature Request" to mutableListOf(
            SurveyQuestion(
                "What is the feature?",
                "Please describe the feature you would like to request",
                Options.TextInput(
                    hint = "Feature",
                    lines = null,
                    regex = mapOf(
                        "Description must be at least 10 characters long" to Regex("(.|\\n){10,}")
                    )
                )
            ),
        )
    )

    val formPage by remember { derivedStateOf { formPages[if (sharedForm[0].value.value == "") "Bug Report" else sharedForm[0].value.value]!! } }
    var pageNumber by remember { mutableIntStateOf(-sharedForm.size) }
    val currentQuestion by remember { derivedStateOf { if (pageNumber < 0) sharedForm[pageNumber + sharedForm.size] else formPage[pageNumber] } }

    var canContinue by remember { mutableStateOf(false) }

    val animatedProgress by animateFloatAsState(
        targetValue = (pageNumber + 1) / (formPage.size).toFloat(),
        animationSpec = tween(durationMillis = 1000)
    )

    val confetties = mutableListOf(
        Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            position = Position.Relative(0.5, 0.3),
            emitter = Emitter(duration = 400, TimeUnit.MILLISECONDS).max(100)
        )
    )
    if (pageNumber <= formPage.size - 1) {
        Column {
            Scaffold(
                bottomBar = {
                    Row {
                        OutlinedButton(
                            onClick = {
                                pageNumber -= 1
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp),
                            enabled = pageNumber > -sharedForm.size
                        ) {
                            Text("Previous")
                        }
                        Button(

                            onClick = {
                                pageNumber += 1
                                canContinue = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp),
                            enabled = canContinue
                        ) {
                            Text("Next")
                        }
                    }
                }
            ) { padding ->

                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                        PageNavProgress(
                            stringResource(R.string.feedback_title),
                            navController,
                            animatedProgress
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                    }
                    item {
                        Column(
                            modifier = Modifier,
                        ) {

                            Text(
                                currentQuestion.question,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp, horizontal = 10.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(color = MaterialTheme.colorScheme.inverseOnSurface)
                                    .padding(20.dp),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = slightlyDeemphasizedAlpha),
                            )
                            if (currentQuestion.description != "")
                                Text(
                                    text = currentQuestion.description,
                                    modifier = Modifier.padding(bottom = 20.dp),
                                    style = MaterialTheme.typography.bodySmall,

                                    color = MaterialTheme.colorScheme.onSurface
                                        .copy(alpha = stronglyDeemphasizedAlpha),
                                )
                            Column {
                                canContinue = currentQuestion.canContinue.value
                                when (val options = currentQuestion.options) {
                                    is SingleChoice -> {
                                        SingleOptionsSection(
                                            currentQuestion.value.value,
                                            SingleChoice(
                                                options = options.options
                                            ),
                                            onOptionSelected = {
                                                currentQuestion.value.value = it
                                                currentQuestion.canContinue.value = true
                                            }
                                        )
                                    }

                                    is Options.TextInput -> {
                                        SurveyTextINput(
                                            value = currentQuestion.value.value,
                                            options = options,
                                            onValueChange = {
                                                currentQuestion.canContinue.value = true
                                                currentQuestion.value.value = it
                                            },
                                            onValidationError = {
                                                currentQuestion.canContinue.value = false
                                            })
                                    }

                                    else -> {
                                        Log.e("FeedbackOption", "Unknown option type")
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        Scaffold(bottomBar = {
            Row {
                OutlinedButton(
                    onClick = {
                        pageNumber -= 1
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    enabled = pageNumber > -sharedForm.size
                ) {
                    Text("Previous")
                }
                Button(
                    onClick = {
                        navController.popBackStack()
                    }, modifier = Modifier.weight(1f).padding(16.dp)
                ) {
                    Text("Submit")
                }
            }
        }) {padding ->
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                Spacer(modifier = Modifier.height(32.dp))
                PageNavProgress(
                    stringResource(R.string.feedback_title),
                    navController,
                    animatedProgress
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    "Thank you for your feedback!",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 32.sp,
                        lineHeight = 40.sp
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Your feedback has been submitted successfully. We will review it and get back to you as soon as possible.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                )
            }
        }
        Box(Modifier.fillMaxSize()) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = confetties,

                )

        }
    }
}
