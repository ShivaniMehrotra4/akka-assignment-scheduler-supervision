package com.knoldus

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.language.postfixOps

object Constants {
  val child = 5
}

object LogCounter extends App {
  val path = "/home/knoldus/Music"
  val actorSystem = ActorSystem("First-Actor-System")
  implicit val timeOut: Timeout = Timeout(5 seconds)
  val superActor = actorSystem.actorOf(Props[Supervisor], "superActor")
  val result = (superActor ? path).mapTo[CountItems]
  result

}
