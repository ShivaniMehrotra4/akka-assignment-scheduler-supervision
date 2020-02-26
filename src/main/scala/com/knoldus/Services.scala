package com.knoldus

import akka.actor.ActorRef
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Future
import akka.pattern.ask

class Services {

  /**
   * getFutureOfCountItems function returns a list that contains all case class objects with future wrapper.
   *
   * @param files          - a list of files from a directory
   * @param actorReference - a list of actor references.
   * @param futureLst      - a list containing futures of case class objects (initially empty).
   * @return - list of future of case class objects
   */
  @scala.annotation.tailrec
  final def getFutureOfCountItems(files: List[String], actorReference: ActorRef, futureLst: List[Future[CountItems]]): List[Future[CountItems]] = {
    implicit val timeout: Timeout = Timeout(5 second)
    files match {
      case Nil => futureLst
      case head :: rest =>
        val temp = (actorReference ? fileName(head)).mapTo[CountItems]
        getFutureOfCountItems(rest, actorReference, temp :: futureLst)
    }
  }

  /**
   * caseClassMembersAddition function performs addition of member's values on two case class objects
   *
   * @param acc - first case class object
   * @param y   - second case class object
   * @return - case class object after addition
   */
  def caseClassMembersAddition(acc: CountItems, y: CountItems): CountItems = {
    CountItems(acc.countError + y.countError, acc.countWarnings + y.countWarnings, acc.countInfo + y.countInfo)
  }

  /**
   * calcAverage function provides average of all the errors,warnings and info in all the files
   *
   * @param items  - object of case class
   * @param length - total number of files in the directory
   * @return - a list of type double containing average results
   */
  def calcAverage(items: CountItems, length: Int): List[Double] = {
    val err = items.countError / length
    val warn = items.countWarnings / length
    val info = items.countInfo / length
    List(err, warn, info)
  }
}
