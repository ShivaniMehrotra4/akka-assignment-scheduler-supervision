package com.knoldus

import akka.actor.SupervisorStrategy.{Escalate, Stop}
import akka.actor.{Actor, ActorKilledException, ActorLogging, DeathPactException, OneForOneStrategy, Props, SupervisorStrategy}
import akka.routing.RoundRobinPool
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * This class provides basic Actor functionality.
 * It calls functions and prints the final results with the help of Logging technique.
 */
class Supervisor extends Actor with ActorLogging {
  val s = new Services
  var finalResult: Future[CountItems] = Future(CountItems(0, 0, 0))

  override def receive: Receive = {

    case path: String =>
      val myActor = context.actorOf(RoundRobinPool(Constants.child).props(Props[LogActor]).withDispatcher("fixed-thread-pool"))
      val listOfFiles = s.getListOfFile(path).map(_.toString)
      val futureList = s.getFutureOfCountItems(listOfFiles, myActor, List())

      finalResult = Future.sequence(futureList).map(an => an.foldLeft(CountItems(0, 0, 0)) { (acc, elem) => s.caseClassMembersAddition(acc, elem) })
      finalResult.map(ss => log.info(s"$ss"))

      val avgResult = s.calcAverage(finalResult, listOfFiles.length)
      val finalAvgResult = Future.sequence(avgResult)
      finalAvgResult.map(av=>log.info(s"$av"))

  }

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(Constants.maxNrOfRetries, Constants.withinTimeRange) {
    case _: ActorKilledException => Stop
    case _: DeathPactException => Stop
    case _: Exception => Escalate
  }

}
