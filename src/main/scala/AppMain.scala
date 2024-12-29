package app

import apis._
import com.github.cdimascio.dotenv.Dotenv

import java.util.Properties
import javax.mail._
import javax.mail.internet._

object AppMain extends App {

  val dotenv = Dotenv.load()

  val senderEmail     = dotenv.get("SENDER_EMAIL")
  val senderPassword  = dotenv.get("SENDER_PASSWORD")
  val recipientEmail  = dotenv.get("RECIPIENT_EMAIL")
  val smtpHost        = dotenv.get("SMTP_HOST")
  val smtpPort        = dotenv.get("SMTP_PORT")



  val codeforcesContests = CodeforcesApi.fetchUpcomingContests()
  // val leetcodeContests = LeetcodeApi.fetchUpcomingContests()
  
  println(s"Upcoming Codeforces contests: $codeforcesContests")
  
  val contestsMessage = createContestsMessage(cfContests)

  if (contestsMessage.nonEmpty) {
    sendEmail(
      host        = smtpHost,
      port        = smtpPort,
      senderEmail = senderEmail,
      senderPass  = senderPassword,
      recipient   = recipientEmail,
      subject     = "Upcoming Programming Contests",
      messageBody = contestsMessage
    )
    println("Email with contests info sent successfully")
  } else {
    println("No upcoming contests found")
  }

  def createContestsMessage(
    cfContests: List[CodeforcesApi.COdeforcesContest]
  ): String = {
    val sb = new StringBuilder
    sb.append("=== Codeforces Contests ===\n")
    if (cfContests.isEmpty) {
      sb.append("No upcoming contests")
    } else {
      cfContests.foreach { c =>
        sb.append(s"* ${c.name} (ID: ${c.id}, starts at ${s.startTimeSeconds}, duration ${c.durationSeconds}\n")

    }
    sb.toString()
  }
  

  def sendEmail(
    host: String,
    port: String,
    senderEmail: String,
    senderPass: String,
    recipient: String,
    subject: String,
    messageBody: String
  ): Unit = {
    
  }
}
