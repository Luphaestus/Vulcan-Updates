package luph.vulcanizerv3.updates.ui.components

import android.content.Context
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.valentinilk.shimmer.ShimmerBounds
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.LinkResolver
import io.noties.markwon.image.ImagesPlugin.*
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.core.spans.LinkSpan
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImageProps
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.coil.CoilImagesPlugin
import io.noties.markwon.syntax.SyntaxHighlight
import luph.vulcanizerv3.updates.MainActivity
import org.commonmark.node.Image
import org.commonmark.node.Node
import org.jetbrains.annotations.TestOnly
import java.net.URI

class ImageLinkResolver(val original: LinkResolver) : LinkResolver {
    override fun resolve(view: View, link: String) {
        if (false) {
        } else
            original.resolve(view, link)
    }
}
@Composable
fun  MarkdownGenerator(markdown: String) {
    val context : Context = androidx.compose.ui.platform.LocalContext.current

    val markwon = Markwon.builder(context)
        .usePlugin(CoilImagesPlugin.create(context))
//        .usePlugin(object: AbstractMarkwonPlugin() {
//            override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
//                builder.appendFactory(Image::class.java) { configuration, porps ->
//                    val url = ImageProps.DESTINATION.require(porps)
//                    LinkSpan(
//                        configuration.theme(),
//                        url,
//                        ImageLinkResolver(configuration.linkResolver())
//                    )
//                }
//            }
//        })
        .usePlugin(HtmlPlugin.create())
        .usePlugin(TablePlugin.create(context))
        .usePlugin(TaskListPlugin.create(context))
        .build()
    val node: Node = markwon.parse(markdown)
    val spanned: Spanned = markwon.render(node)
    val textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f).toArgb()

    AndroidView(factory = { context ->
        TextView(context).apply {
            text = spanned
            setTextColor(textColor)
            movementMethod = LinkMovementMethod.getInstance()
        }
    })
}

fun markdownToText(markdown: String): String {
    val context = MainActivity.applicationContext()
    val markwon = Markwon.builder(context)
        .usePlugin(TablePlugin.create(context))
        .usePlugin(HtmlPlugin.create())
        .build()
    val node: Node = markwon.parse(markdown)
    val spanned: Spanned = markwon.render(node)
    return spanned.toString()
}


@TestOnly
@Composable
fun MarkDownContentTest()  {
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