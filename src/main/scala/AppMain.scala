package app

import apis._


object AppMain extends App {

  case class Contest(
    id: Option[Long],
    name: String,
    startDateTime: String,
    duration: String,
    link: Option[String]
  )


  val codeforcesContests: List[Contest] = CodeforcesApi.fetchUpcomingContests()
  
  println(s"Upcoming Codeforces contests: $codeforcesContests")

}
