package app

import apis._
import services._
import courier._, Defaults._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.Source

object AppMain extends App {
  // sys env
  val username: String = sys.env("CLIST_USERNAME")
  val apiKey: String = sys.env("CLIST_API_KEY")
  val timezone: String = sys.env.getOrElse("TIMEZONE", "Asia/Almaty")
  val gmailUsername: String = sys.env("GMAIL_USERNAME")
  val gmailPassword: String = sys.env("GMAIL_PASSWORD")

  val subscribers: List[(String, String, List[String])] = {
    val lines = Source.fromFile("subscribers.csv").getLines().toList
    lines.map { line =>
      val cols = line.split(",")
      (cols(0), cols(1), cols(2).split(",").map(_.trim).toList)
    }
  }

  subscribers.foreach { case (name, email, platforms) =>
    val contests: List[ClistContest] = platforms.flatMap { platform =>
      ClistApi.fetchUpcomingContests(
        username,
        apiKey,
        resource = platform
      )
    }.filter(_.start.isDefined).sortBy(_.start)
      
    val emailContent = prettyString(contests)
    sendEmail(email, s"$name, here are the upcoming contests", emailContent)
  }


  def sendEmail(to: String, subject: String, body: String): Unit = {
    // val emailContent = prettyString(contests)
    val mailer = Mailer("smtp.gmail.com", 587)
      .auth(true)
      .as(gmailUsername, gmailPassword)
      .startTls(true)()

    val email = Envelope.from(gmailUsername.addr)
      .to(to.addr)
      .subject(subject)
      .content(Text(body))

    try {
      Await.result(mailer(email), 30.seconds)
      println("Email sent to $to")
    } catch {
      case e: Exception => println(s"Failed to send email to $to: $e")
      e.printStackTrace()
    }
  }

  def prettyString(contests: List[ClistContest]): String = {
    contests.map { contest =>
      s"Platform:\t${contest.resource.getOrElse("N/A")}\n" +
      s"Contest:\t${contest.event.getOrElse("N/A")}\n" +
      s"Start:\t\t${formatDateTime(contest.start)}\n" +
      s"Duration:\t${formatDuration(contest.duration)}\n" +
      s"Link:\t${contest.href.getOrElse("N/A")}\n\n"
    }.mkString
  }

  def formatDateTime(dateTime: Option[String]): String = {
    dateTime match {
      case Some(dt) => 
        val ajdustedDateTime = TimeZoneAdjuster.adjustTimeZone(dt, timezone)
        val date = ajdustedDateTime.split("T")(0)
        val time = ajdustedDateTime.split("T")(1).split(":").slice(0, 2).mkString(":")
        s"$date $time ($timezone)"
      case None => "N/A"
    }
    
  }

  def formatDuration(duration: Option[Int]): String = {
    duration match {
      case Some(d) =>
        val hours = d / 3600
        val minutes = (d % 3600) / 60
        s"$hours hr $minutes min"
      case None => "N/A"
    }
  }

    
}
