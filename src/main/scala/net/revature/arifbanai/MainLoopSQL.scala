package net.revature.arifbanai

import scala.util.{Failure, Success, Try, Using}

object MainLoopSQL {

  def login(username: String, password: String): Boolean = {

    var passwordFromDB = ""

    Using.Manager { use =>
      val validateLoginStatement = use(DataSource.getConnection
        .prepareStatement("SELECT userpass FROM users WHERE username = ?;"))
      validateLoginStatement.setString(1, username)
      val resultSet = use(validateLoginStatement.executeQuery())

      // Should return exactly one password given some username, as usernames are unique
      if (resultSet.next()) {
        passwordFromDB = resultSet.getString("userpass")
      }
    }

    if (password.equals(passwordFromDB)) {
      true
    } else {
      false
    }
  }

  def createUser(username: String, password: String): Boolean = {

    var userCreated = false

    Using.Manager { use =>
      val createUserStatement = use(DataSource.getConnection
        .prepareStatement("INSERT INTO users(username, userpass) VALUES(?, ?);"))
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
    }

    userCreated = createBankAccount(username)

    userCreated
  }

  private def createBankAccount(username: String): Boolean = {

    var accountCreated = false

    Using.Manager { use =>
      val createAccountStatement = use(DataSource.getConnection
        .prepareStatement("INSERT INTO accounts(userid, balance) VALUES((SELECT id FROM users WHERE username = ?), DEFAULT);"))
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
    }

    accountCreated
  }


}
