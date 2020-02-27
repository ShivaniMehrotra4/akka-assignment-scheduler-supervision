package com.knoldus

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.BeforeAndAfterAll
import scala.concurrent.duration._
import scala.language.postfixOps

class SupervisorSpec extends TestKit(ActorSystem("First-Actor-System")) with ImplicitSender with AnyWordSpecLike with BeforeAndAfterAll {
    override def afterAll(): Unit = {
      TestKit.shutdownActorSystem(system)
    }

    "A simple actor " should {
      "send back" in{
        within(20 second){
          val testActor = system.actorOf(Props[Supervisor])
          val message = "/home/knoldus/Videos/akka-assignment-3/src/main/resources/DirectoryLogs"
          testActor ! message
          val expectedMessage= List(0,377,65)
          expectMsg(expectedMessage)
        }}
    }

}
