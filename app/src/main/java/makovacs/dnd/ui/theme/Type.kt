// Main coding: Jordan

package makovacs.dnd.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import makovacs.dnd.R

val kirstyFamily = FontFamily(
    Font(R.font.kirsty),
    Font(R.font.kirsty_bold, FontWeight.Bold),
    Font(R.font.kirsty_italic, style = FontStyle.Italic),
    Font(R.font.kirsty_bold_italic, FontWeight.Bold, FontStyle.Italic)
)

// Set of Material typography styles to start with
val Typography = Typography().run {
    // Use the default values and modify them as needed
    copy(
        displayLarge = displayLarge.copy(
            fontFamily = kirstyFamily
        ),
        displayMedium = displayMedium.copy(
            fontFamily = kirstyFamily
        ),
        displaySmall = displaySmall.copy(
            fontFamily = kirstyFamily
        ),

        headlineLarge = headlineLarge.copy(
            fontFamily = kirstyFamily
        ),
        headlineMedium = headlineMedium.copy(
            fontFamily = kirstyFamily
        ),
        headlineSmall = headlineSmall.copy(
            fontFamily = kirstyFamily
        ),

        titleLarge = titleLarge.copy(
            fontFamily = kirstyFamily
        )
    )
}
