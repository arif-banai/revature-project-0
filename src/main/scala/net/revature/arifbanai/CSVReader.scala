package net.revature.arifbanai

import java.io.BufferedReader
import scala.io.Source

object CSVReader {

  def importFromCSV(fileName: String): Boolean = {

    val br: BufferedReader = Source.fromResource("users.csv").bufferedReader()

    val lines = LazyList.continually(br.readLine()).takeWhile(_ != null)

    lines.foreach(line => {
      val values: Array[String] = line.split(",")

      val username = values(0)
      val password = values(1)

      MainLoopSQL.createUser(username, password)
    })

    true
  }

}
