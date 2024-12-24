import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.defaultImageResultMemoryCache
import okio.Path.Companion.toOkioPath
import java.io.File

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KmpApp",
    ) {
        App()
    }
}

fun generateImageLoader(): ImageLoader {
    return ImageLoader {
        components {
            setupDefaultComponents()
        }
        interceptor {
            defaultImageResultMemoryCache()
            memoryCacheConfig {
                maxSizeBytes(32 * 1024 * 1024)
            }
            diskCacheConfig {
                directory(getCacheDir().toOkioPath().resolve("image_cache"))
                maxSizeBytes(512L * 1024 * 1024)
            }
        }
    }
}

private enum class OperatingSystem {
    WINDOWS, LINUX, MACOS, Unknown
}

private val currentOperatingSystem: OperatingSystem
    get() {
        val operSys = System.getProperty("os.name").lowercase()
        return if (operSys.contains("win")) {
            OperatingSystem.WINDOWS
        } else {
            OperatingSystem.Unknown
        }
    }

private fun getCacheDir() = when (currentOperatingSystem) {
    OperatingSystem.WINDOWS -> File(System.getenv("AppData"), "$ApplicationName/cache")
    OperatingSystem.LINUX -> File(System.getenv("AppData"), "$.cache/$ApplicationName")
    OperatingSystem.MACOS -> File(System.getenv("AppData"), "Library/Caches/$ApplicationName")
    else -> throw IllegalStateException("Unsupported operating system")
}

private const val ApplicationName = "KmpApp"