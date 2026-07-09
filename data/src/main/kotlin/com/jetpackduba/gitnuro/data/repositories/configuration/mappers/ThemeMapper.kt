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

class ThemeMapper @Inject constructor() : DataMapper<Theme?, String?> {
    override fun toData(value: Theme?): String? {
        return when (value) {
            Theme.Light -> LIGHT
            Theme.Dark -> DARK
            Theme.RadioactiveDreams -> RADIOACTIVE
            Theme.GenXSoftClub -> GEN_X
            Theme.CalderaNight -> CALDERA_NIGHT
            Theme.Custom -> CUSTOM
            null -> null
        }
    }


    override fun toDomain(value: String?): Theme? {
        return when (value) {
            LIGHT -> Theme.Light
            DARK -> Theme.Dark
            RADIOACTIVE -> Theme.RadioactiveDreams
            GEN_X -> Theme.GenXSoftClub
            CALDERA_NIGHT -> Theme.CalderaNight
            CUSTOM -> Theme.Custom
            null -> null
            else -> throw IllegalStateException("Unhandled theme $value")
        }
    }
}