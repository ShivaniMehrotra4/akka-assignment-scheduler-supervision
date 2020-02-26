package com.knoldus

import akka.actor.SupervisorStrategy.{Escalate, Stop}
import akka.actor.{Actor, ActorKilledException, DeathPactException, OneForOneStrategy, Props, SupervisorStrategy}
import akka.routing.RoundRobinPool
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class Supervisor extends Actor {
  val s = new Services
  val rd = new ReadDirectory

  override def receive: Receive = {
    case path =>
      val myActor = context.actorOf(RoundRobinPool(Constants.child).props(Props[LogActor]).withDispatcher("fixed-thread-pool"))
      val listOfFiles = rd.getListOfFile(path.toString).map(_.toString)
      val x = s.getFutureOfCountItems(listOfFiles, myActor, List())

      val futureWrappedSolution = Future.sequence(x).map(an => an.foldLeft(CountItems(0, 0, 0)) { (acc, y) => s.caseClassMembersAddition(acc, y) })
      val finalResult = Await.result(futureWrappedSolution, 10 second)
      println(finalResult)

      val avgResult = s.calcAverage(finalResult, listOfFiles.length)
      println(avgResult)

  }

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 5, withinTimeRange = 10 seconds) {
    case _: ActorKilledException => Stop
    case _: DeathPactException => Stop
    case _: Exception => Escalate
  }

}
