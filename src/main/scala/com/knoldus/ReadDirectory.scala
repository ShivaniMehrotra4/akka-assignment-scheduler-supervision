package com.knoldus

import java.io.File

/**
 * ReadDirectory class reads the current directory specified by the path name and returns list of files in that directory.
 */
class ReadDirectory {
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
