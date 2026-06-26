package com.jetpackduba.gitnuro.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * Caldera-inspired shape system.
 *
 * Caldera uses 40px as the house radius. For a dense Git GUI we scale
 * proportionally but keep the generous, warm roundness:
 *
 *   - Small: buttons, fields, toolbar items → 12dp
 *   - Medium: menus, tooltips, notifications → 16dp
 *   - Large: dialogs, cards, panels → 24dp
 *   - Pill: tags, badges, branch labels → 40dp (the Caldera house radius)
 *   - Circle: avatars, graph nodes → 50%
 */
object AppShapes {
    /** Buttons, text fields, small interactive elements */
    val small = RoundedCornerShape(12.dp)

    /** Menus, dropdowns, tooltips, notifications */
    val medium = RoundedCornerShape(16.dp)

    /** Dialogs, cards, large panels */
    val large = RoundedCornerShape(24.dp)

    /** Tags, badges, branch/tag labels — the Caldera house radius */
    val pill = RoundedCornerShape(40.dp)

    /** Fully circular — avatars, graph nodes, toggle thumbs */
    val circle = RoundedCornerShape(50)
}
