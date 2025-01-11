package web

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import java.io.FileWriter

object WebServer extends App {
    implicit val system = ActorSystem("contest-alert")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher
    val htmlContent: String =
        """
      |<!DOCTYPE html>
      |<html lang="en">
      |<head>
      |  <meta charset="UTF-8">
      |  <meta name="viewport" content="width=device-width, initial-scale=1.0">
      |  <title>Contest Alarm Signup</title>
      |  <style>
      |    body { font-family: Arial, sans-serif; margin: 20px; }
      |    .form-container { max-width: 500px; margin: 0 auto; }
      |    label { display: block; margin: 10px 0 5px; }
      |    input, button { width: 100%; padding: 10px; margin: 5px 0; }
      |  </style>
      |</head>
      |<body>
      |  <div class="form-container">
      |    <h1>Signup for Contest Notifications</h1>
      |    <form action="/signup" method="POST">
      |      <label for="name">Name:</label>
      |      <input type="text" id="name" name="name" required>
      |      <label for="email">Email:</label>
      |      <input type="email" id="email" name="email" required>
      |      <label for="platforms">Platforms (comma-separated):</label>
      |      <input type="text" id="platforms" name="platforms" value="codeforces.com,leetcode.com">
      |      <button type="submit">Sign Up</button>
      |    </form>
      |  </div>
      |</body>
      |</html>
    """.stripMargin

    val router = 
        path("") {
            get {
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, htmlContent))
            }
        } ~
            path("signup") {
                post {
                    formFields('name, 'email, 'platforms) { (name, email, platforms) =>
                        val record = s"$name,$email,$platforms\n"
                        val writer = new FileWriter("subscribers.csv", true)
                        try {
                            writer.write(record)
                        } finally {
                            writer.close()
                        }
                        complete(s"Rakhmet (Thanks), $name! You will now receive daily contest alerts.")
                    }
                }
            }
    Http().bindAndHandle(router, "0.0.0.0", 8080)
    println("Server online at http://localhost:8080/")
}