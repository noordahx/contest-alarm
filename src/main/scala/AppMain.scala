package app

import apis._
import services._
import courier._, Defaults._
import scala.concurrent.Await
import scala.concurrent.duration._

object AppMain extends App {
  // dev .env
  /*
  val envMap: Map[String, String] = DotEnvLoader.loadDotEnv(".env")
  val username: String = envMap.getOrElse("CLIST_USERNAME", "")
  val apiKey: String = envMap.getOrElse("CLIST_API_KEY", "")
  val timezone: String = envMap.getOrElse("TIMEZONE", "Asia/Almaty")
  val gmailUsername: String = envMap.getOrElse("GMAIL_USERNAME", "")
  val gmailPassword: String = envMap.getOrElse("GMAIL_PASSWORD", "")
  val destinationEmail: String = envMap.getOrElse("DESTINATION_EMAIL", "")
  */ 

  // sys env
  val username: String = sys.env("CLIST_USERNAME")
  val apiKey: String = sys.env("CLIST_API_KEY")
  val timezone: String = sys.env.getOrElse("TIMEZONE", "Asia/Almaty")
  val gmailUsername: String = sys.env("GMAIL_USERNAME")
  val gmailPassword: String = sys.env("GMAIL_PASSWORD")
  val destinationEmail: String = sys.env("DESTINATION_EMAIL")
  
  val platforms: List[String] = sys.env.getOrElse("PLATFORMS", "codefores.com,leetcode.com")
    .split(",")
    .map(_.trim)
    .toList


  val contests = platforms.flatMap { platform =>
    ClistApi.fetchUpcomingContests(
      username,
      apiKey,
      resource = platform
    )
  }.filter { contest =>
    contest.start.isDefined && contest.end.isDefined && contest.duration.isDefined
  }.sortBy(_.start)

  println(prettyString(contests))
  sendEmail()

  def sendEmail(): Unit = {
    val emailContent = prettyString(contests)

    val mailer = Mailer("smtp.gmail.com", 587)
      .auth(true)
      .as(gmailUsername, gmailPassword)
      .startTls(true)()

    val email = Envelope.from(gmailUsername.addr)
      .to(destinationEmail.addr)
      .subject("Daily contests alert")
      .content(Text(emailContent))

    try {
      Await.result(mailer(email), 30.seconds)
      println("Email sent!")
    } catch {
      case e: Exception => println(s"Failed to send email: $e")
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
