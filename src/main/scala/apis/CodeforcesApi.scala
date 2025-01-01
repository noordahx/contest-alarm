// Not used in the final project, but kept for reference

package apis

import sttp.client3._
import sttp.client3.circe._
import io.circe._
import io.circe.generic.semiauto._
import java.text.SimpleDateFormat
import java.util.Date

object CodeforcesApi {
  case class CodeforcesContest(
    id: Long,
    name: String,
    startTimeSeconds: Long,
    durationSeconds: Long
  )

  case class Contest(
    id: Option[Long],
    name: String,
    startDateTime: String,
    duration: String,
    link: Option[String]
  )

  implicit val cfContestDecoder: Decoder[CodeforcesContest] = deriveDecoder[CodeforcesContest]

  private val CodeforcesEndpoint = uri"https://codeforces.com/api/contest.list"

  def fetchUpcomingContests(): List[Contest] = {
    val backend = HttpURLConnectionBackend()
    val request = basicRequest
      .get(CodeforcesEndpoint)
      .response(asJson[Json])
    
    val response = request.send(backend)

    response.body match {
      case Right(json) =>
        val resultCursor = json.hcursor.downField("result")
        resultCursor.as[List[CodeforcesContest]] match {
          case Right(codeforcesContests) =>
            val currentTimeSec = System.currentTimeMillis() / 1000

            codeforcesContests
              .filter(_.startTimeSeconds > currentTimeSec)
              .map { c => 
                // 1. Convert startTimeSecods -> Date
                val date = new Date(c.startTimeSeconds * 1000L)
                val dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val startDateTime = dateFormatter.format(date)

                // 2. Convert durationSeconds -> "X hr Y min"
                val hours = c.durationSeconds / 3600
                val minutes = (c.durationSeconds % 3600) / 60
                val durationStr = s"$hours hr $minutes min"

                // 3. Final Contest object
                Contest(
                  id = Some(c.id),
                  name = c.name,
                  startDateTime = startDateTime,
                  duration = durationStr,
                  link = Some(s"https://codeforces.com/contest/${c.id}")
                )
              }
        }
    }
  }
}
