package com.knoldus

import java.io.File
import akka.actor.ActorRef
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Future
import akka.pattern.ask
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * This class provides all the services including calculating list of case class objects,
 * addition of case class object's members and calculating average per file.
 */
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
        val temp = (actorReference ? FileName(head)).mapTo[CountItems]
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
  def calcAverage(items: Future[CountItems], length: Int): List[Future[Int]] = {
    val err = items.map(a => a.countError / length)
    val warn = items.map(b => b.countWarnings / length)
    val info = items.map(c => c.countInfo / length)
    List(err, warn, info)
  }

  /**
   * getListOfFile function returns the list of files in the directory
   *
   * @param pathName - directory path
   * @return - list of files in that path
   */
  def getListOfFile(pathName: String): List[File] = {

    val file = new File(pathName)
    if (file.isDirectory) {
      file.listFiles.toList
    } else {
      List[File]()
    }
  }
}
