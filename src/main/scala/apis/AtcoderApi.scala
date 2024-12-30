package apis

import sttp.client3._
import sttp.client3.circe._
import io.circe._
import io.circe.generic.semiauto._

object AtcoderApi {
    case class AtcoderContest(
        title: String,
        start_time: String,
        end_time: String,
    )

    implicit val atcodeDecoder: Decoder[AtcoderContest] = deriveDecoder[AtcoderContest]

    private val AtcoderEndpoint = uri"https://atcoder.jp/public-api/contests"

    def fetchUpcomingContests(): List[AtcoderContest] = {
        val backend = HttpURLConnectionBackend()
        val request = basicRequest
            .get(AtcoderEndpoint)
            .response(asJson[List[AtcoderContest]])
        
        val response = request.send(backend)

        response.body match {
            case Right(contests) =>
                contests
            case Left(error) =>
                println(s"Failed to fetch Atcoder data: $error")
                List.empty
        }
    }
}