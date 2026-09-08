package com.jetpackduba.gitnuro.data.repositories.configuration.mappers

import com.jetpackduba.gitnuro.data.mappers.DataMapper
import com.jetpackduba.gitnuro.domain.models.ui.Theme
import javax.inject.Inject

private const val DARK = "dark"
private const val LIGHT = "light"
private const val CUSTOM = "custom"
private const val RADIOACTIVE = "radioactive_dreams"
private const val GEN_X = "gen_x_soft_club"
private const val CALDERA_NIGHT = "caldera_night"
private const val CLAYMAKERS = "claymakers"
private const val CLAYMAKERS_NIGHT = "claymakers_night"

class ThemeMapper @Inject constructor() : DataMapper<Theme?, String?> {
    override fun toData(value: Theme?): String? {
        return when (value) {
            Theme.CalderaNight -> CALDERA_NIGHT
            Theme.Claymakers -> CLAYMAKERS
            Theme.ClaymakersNight -> CLAYMAKERS_NIGHT
            Theme.Custom -> CUSTOM
            null -> null
        }
    }


    override fun toDomain(value: String?): Theme? {
        return when (value) {
            CALDERA_NIGHT -> Theme.CalderaNight
            CLAYMAKERS -> Theme.Claymakers
            CLAYMAKERS_NIGHT -> Theme.ClaymakersNight
            // Retired themes. Anyone whose settings still name one must not hit the
            // throw below on startup — migrate them to the current default instead.
            LIGHT, DARK, RADIOACTIVE, GEN_X -> Theme.CalderaNight
            CUSTOM -> Theme.Custom
            null -> null
            else -> throw IllegalStateException("Unhandled theme $value")
        }
    }
}