package luph.vulcanizerv3.updates.ui.components

import androidx.compose.runtime.Composable
import dev.jeziellago.compose.markdowntext.MarkdownText
import org.jetbrains.annotations.TestOnly


@Composable
fun MarkdownGenerator(markdown: String) {
    MarkdownText(markdown = markdown)
}


@TestOnly
@Composable
fun MarkDownContentTest() {
    val markdown = """
# Vulcan ROM Features

Vulcan ROM is a custom ROM that offers a variety of features and enhancements. Here are some of its highlights:

## 1. Performance
Vulcan ROM is optimized for speed and efficiency:
- **Fast Boot**: `Boots in under 10 seconds`
- *Smooth UI*: `Experience lag-free navigation`

## 2. Customization
You can customize your device extensively:
- **Themes**: `Choose from a variety of themes`
- *Icons*: `Customize your app icons`

## 3. Security
Vulcan ROM includes advanced security features:
- **Encryption**: `Full device encryption`
- *Privacy Guard*: `Control app permissions`

## 4. Battery Life
Optimized for long battery life:
- **Battery Saver**: `Extends battery life by up to 30%`
- *Adaptive Brightness*: `Automatically adjusts screen brightness`

## 5. Updates
Regular updates with new features and security patches:
- **OTA Updates**: `Over-the-air updates for easy installation`
- *Changelog*: `Detailed changelog for each update`

## 6. Code Example
Here is a simple code example to demonstrate a feature:
\```
fun main() {
    println("Welcome to Vulcan ROM!")
}
\```

## 7. Blockquotes
User feedback:
> "Vulcan ROM transformed my device. It's like having a new phone!"

## 8. Tables
You can create tables using the following syntax:

| Feature         | Description                          |
|-----------------|--------------------------------------|
| Fast Boot       | Boots in under 10 seconds            |
| Smooth UI       | Experience lag-free navigation       |
| Themes          | Choose from a variety of themes      |
| Icons           | Customize your app icons             |
| Encryption      | Full device encryption               |
| Privacy Guard   | Control app permissions              |
| Battery Saver   | Extends battery life by up to 30%    |
| Adaptive Brightness | Automatically adjusts screen brightness |

## Conclusion
Vulcan ROM offers a powerful and customizable experience for Android users. Try it out and see the difference!
"""
    MarkdownGenerator(markdown = markdown)
}