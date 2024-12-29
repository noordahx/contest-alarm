package apis

import sttp.client3._
import sttp.client3.circe._
import io.circe._
import io.circe.generic.semiauto._

object CodeforcesApi {
  case class CodeforcesContest(
    id: Long,
    name: String,
    startTimeSeconds: Long,
    durationSeconds: Long
  )

  implicit val cfContestDecoder: Decoder[CodeforcesContest] = deriveDecoder[CodeforcesContest]

  private val CodeforcesEndpoint = uri"https://codeforces.com/api/contest.list"

  def fetchUpcomingContests(): List[CodeforcesContest] = {
    val backend = HttpURLConnectionBackend()
    val request = basicRequest
      .get(CodeforcesEndpoint)
      .response(asJson[Json])

    val response = request.send(backend)

    response.body match {
      case Right(json) =>
        val resultCursor = json.hcursor.downField("result")
        resultCursor.as[List[CodeforcesContest]] match {
          case Right(contests) =>
            val currentTime = System.currentTimeMillis() / 1000
            contests.filter(_.startTimeSeconds > currentTime)
          case Left(err) =>
            println(s"Failed to decode Codeforces contests: $err")
            List.empty
        }
      case Left(error) =>
        println(s"Failed to fetch Codeforces data: $error")
        List.empty
    }
  }
}
