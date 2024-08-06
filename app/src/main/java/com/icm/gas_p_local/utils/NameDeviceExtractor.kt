package com.icm.gas_p_local.utils

import java.util.regex.Pattern

class NameDeviceExtractor {
    companion object {
        fun extractName(message: String?): String? {
            val pattern = Pattern.compile("""nombre:"([^"]+)"""")
            val matcher = pattern.matcher(message ?: "")
            return if (matcher.find()) matcher.group(1) else null
        }
    }
}