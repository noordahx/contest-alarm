package apis

import sttp.client3._
import sttp.client3.circe._
import io.circe._
import io.circe.generic.semiauto._

case class ClistContest(
    id: Option[Int],
    resource: Option[String],
    resource_id: Option[Int],
    host: Option[String],
    event: Option[String],
    start: Option[String],
    end: Option[String],
    n_statistics: Option[Int],
    n_problems: Option[Int],
    parsed_at: Option[String],
    duration: Option[Int],
    href: Option[String],
    problems: Option[String]
)

case class ClistResponse(
    meta: ClistMeta,
    objects: List[ClistContest]
)

case class ClistMeta(
    limit: Int,
    next: Option[String],
    offset: Int,
    previous: Option[String],
    total_count: Option[Int]
)

object ClistApi {
    implicit val contestDecoder: Decoder[ClistContest] = deriveDecoder[ClistContest]
    implicit val metaDecoder: Decoder[ClistMeta] = deriveDecoder[ClistMeta]
    implicit val responseDecoder: Decoder[ClistResponse] = deriveDecoder[ClistResponse]

    /**
    * Fetches upcoming contests from Clist API
    * @param username The username of the user
    * @param apiKey The API key of the user
    * @return List of upcoming contests
    **/

    def fetchUpcomingContests(
        username: String,
        apiKey: String,
        resource: String,
    ): List[ClistContest] = {
        val url = uri"https://clist.by/api/v4/contest/?resource=${resource}&order_by=start&limit=50&start__gt=${currentDateTimeIso}"
        val backend = HttpURLConnectionBackend()
        val request = basicRequest
            .header("Authorization", s"ApiKey $username:$apiKey")
            .get(url)
            .response(asJson[ClistResponse])
        
        val response = request.send(backend)

        response.body match {
            case Right(json) => {
                json.objects
            }
            case Left(error) => 
                println(s"Error fetching contests: $error")
                List.empty
        }
    }

    private def currentDateTimeIso: String = {
        val formatter = java.time.format.DateTimeFormatter.ISO_INSTANT
        val now = java.time.Instant.now
        formatter.format(now)
    }
}