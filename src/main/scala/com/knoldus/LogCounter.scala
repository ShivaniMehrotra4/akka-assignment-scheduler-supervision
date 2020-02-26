package com.knoldus

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import scala.concurrent.duration._
import scala.language.postfixOps

object Constants {
  val child = 5
  val maxNrOfRetries = 5
  val withinTimeRange: FiniteDuration = 10.seconds
}

object LogCounter extends App {
  val path = "/home/knoldus/Music"
  val actorSystem = ActorSystem("First-Actor-System")
  import actorSystem.dispatcher
  implicit val timeOut: Timeout = Timeout(5 seconds)
  val superActor = actorSystem.actorOf(Props[Supervisor], "superActor")
  val cancellable = actorSystem.scheduler.scheduleWithFixedDelay(0 second, 5 minutes, superActor, path)

}