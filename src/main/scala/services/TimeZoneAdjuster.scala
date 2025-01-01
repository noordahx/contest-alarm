package services

import java.time._
import java.time.format.DateTimeFormatter

object TimeZoneAdjuster {
    def adjustTimeZone(
        dateTimeStr: String,
        targetZoneId: String,
    ): String = {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val localDateTime = LocalDateTime.parse(dateTimeStr, inputFormatter)

        val utcZoned = localDateTime.atZone(ZoneId.of("UTC"))

        val userZone = ZoneId.of(targetZoneId)
        val adjustedZoned = utcZoned.withZoneSameInstant(userZone)

        adjustedZoned.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}