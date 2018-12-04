package nl.viasalix.horarium.utils

import android.net.Uri

object RetrofitUtils {
    fun buildBaseUrl(schoolName: String) = Uri.Builder().scheme("https")
            .encodedAuthority("$schoolName.zportal.nl")
            .encodedPath("api/v$ZERMELO_API_VERSION/")
            .toString()
}