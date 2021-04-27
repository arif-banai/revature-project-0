package net.revature.arifbanai

import scala.util.{Failure, Success, Try}

object MainLoopSQL {

  def login(username: String, password: String): Boolean = {

    var passwordFromDB = ""

    val conn = DataSource.getConnection
    val validateLoginStatement = conn
      .prepareStatement("SELECT userpass FROM users WHERE username = ?;")
    validateLoginStatement.setString(1, username)
    val resultSet = validateLoginStatement.executeQuery()

    // Should return exactly one password given some username, as usernames are unique
    if (resultSet.next()) {
      passwordFromDB = resultSet.getString("userpass")
    }

    validateLoginStatement.close()
    conn.close()

    if (password.equals(passwordFromDB)) {
      true
    } else {
      false
    }
  }

  def createUser(username: String, password: String): Boolean = {

    var userCreated = false

    val conn = DataSource.getConnection
    val createUserStatement = conn.prepareStatement("INSERT INTO users(username, userpass) VALUES(?, ?);")

    createUserStatement.setString(1, username)
    createUserStatement.setString(2, password)

    val updateResult = Try(createUserStatement.executeUpdate())

    updateResult match {
      case Success(value) => {
        userCreated = true
      }
      case Failure(exception) => {
        userCreated = false
        println("Exception occurred during user creation")
        exception.printStackTrace()
      }
    }

    createUserStatement.close()
    conn.close()

    userCreated = createBankAccount(username)

    userCreated
  }

  private def createBankAccount(username: String): Boolean = {

    var accountCreated = false

    val conn = DataSource.getConnection
    val createAccountStatement = conn
      .prepareStatement("INSERT INTO accounts(userid, balance) VALUES((SELECT id FROM users WHERE username = ?), DEFAULT);")
    createAccountStatement.setString(1, username)

    val updateResult = Try(createAccountStatement.executeUpdate())

    updateResult match {
      case Success(value) => {
        accountCreated = true
      }
      case Failure(exception) => {
        accountCreated = false
        println("Exception occurred during account creation")
        exception.printStackTrace()
      }
    }

    createAccountStatement.close()
    conn.close()

    accountCreated
  }

}