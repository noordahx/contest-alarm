package app

import apis._
import services._

object AppMain extends App {
  val envMap: Map[String, String] = DotEnvLoader.loadDotEnv(".env")
  val username: String = envMap.getOrElse("CLIST_USERNAME", "")
  val apiKey: String = envMap.getOrElse("CLIST_API_KEY", "")
  val timezone: String = envMap.getOrElse("TIMEZONE", "Asia/Almaty")
  val platforms: List[String] = List("codeforces.com", "leetcode.com")

  val contests = platforms.flatMap { platform =>
    ClistApi.fetchUpcomingContests(
      username,
      apiKey,
      resource = platform
    )
  }
  
  prettyPrint(contests)

  def prettyPrint(contests: List[ClistContest]): Unit = {
    contests.foreach { contest =>
      println(s"Contest: ${contest.event.getOrElse("N/A")}")
      println(s"Start: ${formatDateTime(contest.start)}")
      println(s"End: ${formatDateTime(contest.end)}")
      println(s"Duration: ${formatDuration(contest.duration)}")
      println(s"Link: ${contest.href.getOrElse("N/A")}")
      println()
    }
  }

  def formatDateTime(dateTime: Option[String]): String = {
    dateTime match {
      case Some(dt) => 
        val ajdustedDateTime = TimeZoneAdjuster.adjustTimeZone(dt, timezone)
        val date = ajdustedDateTime.split("T")(0)
        val time = ajdustedDateTime.split("T")(1).split(":").slice(0, 2).mkString(":")
        s"$date $time $timezone"
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
