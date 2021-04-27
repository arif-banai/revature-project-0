package net.revature.arifbanai

import scala.util.{Failure, Success, Try}

object UserSQL {

  def transfer(userKey: Int, targetUser: String, moneyToTransfer: Long): Boolean = {

    val newSourceBalance = checkBalance(userKey) - moneyToTransfer

    if (newSourceBalance < 0) {
      System.err.println("You cannot withdraw more money than you have!")
      return false
    }

    val targetUserKey = Try(getKey(targetUser))

    targetUserKey match {
      case Success(targetKey: Int) => {
        val newTargetUserBalance = checkBalance(targetKey)

        if (newTargetUserBalance > Long.MaxValue) {
          System.err.println("Recipient cannot accept more money!")
          return false
        }

        withdraw(userKey, moneyToTransfer)
        deposit(targetKey, moneyToTransfer)
      }
      case Failure(exception: Exception) =>
        System.err.println(s"Could not find user $targetUser")
        false
    }
  }

  def getKey(username: String): Int = {

    var key = -1

    val conn = DataSource.getConnection
    val validateLoginStatement = conn
      .prepareStatement("SELECT id FROM users WHERE username = ?;")
    validateLoginStatement.setString(1, username)
    val resultSet = validateLoginStatement.executeQuery()

    // Should return exactly one password given some username, as usernames are unique
    if (resultSet.next()) {
      key = resultSet.getInt("id")
    }

    validateLoginStatement.close()
    conn.close()

    if(key < 1) {
      throw new Exception("User not found")
    }

    key
  }

  def deposit(userKey: Int, moneyToDeposit: Long): Boolean = {

    val newBalance = checkBalance(userKey) + moneyToDeposit

    if (newBalance > Long.MaxValue) {
      System.err.println("You cannot deposit more money into this account!")
      return false
    }

    var depositSuccessful = false

    val conn = DataSource.getConnection
    val depositStatement = DataSource.getConnection
      .prepareStatement("UPDATE accounts SET balance = ? WHERE userid = ?;")
    depositStatement.setLong(1, newBalance)
    depositStatement.setInt(2, userKey)

    val depositUpdate = Try(depositStatement.executeUpdate())

    depositUpdate match {
      case Success(value) => {
        depositSuccessful = true
      }
      case Failure(exception) => {
        depositSuccessful = false
        println("Exception occurred during depositing funds")
        exception.printStackTrace()
      }
    }

    depositStatement.close()
    conn.close()

    depositSuccessful
  }

  def withdraw(userKey: Int, moneyToWithdraw: Long): Boolean = {

    val newBalance = checkBalance(userKey) - moneyToWithdraw

    if (newBalance < 0) {
      System.err.println("You cannot withdraw more money than you have!")
      return false
    }

    var withdrawSuccessful = false

    val conn = DataSource.getConnection
    val withdrawStmt = conn
      .prepareStatement("UPDATE accounts SET balance = ? WHERE userid = ?;")
    withdrawStmt.setLong(1, newBalance)
    withdrawStmt.setInt(2, userKey)

    val withdrawUpdate = Try(withdrawStmt.executeUpdate())

    withdrawUpdate match {
      case Success(value) =>
        withdrawSuccessful = true
      case Failure(exception) =>
        withdrawSuccessful = false
        println("Exception occurred during withdrawal of funds")
        exception.printStackTrace()
    }

    withdrawStmt.close()
    conn.close()

    withdrawSuccessful
  }

  def checkBalance(userKey: Int): Long = {

    var balance = -1L

    val conn = DataSource.getConnection
    val checkBalanceStmt = conn
      .prepareStatement("SELECT balance FROM accounts WHERE userid = ?;")
    checkBalanceStmt.setInt(1, userKey)
    val resultSet = checkBalanceStmt.executeQuery()

    // Should return exactly one password given some username, as usernames are unique
    if (resultSet.next()) {
      balance = resultSet.getLong("balance")
    }

    checkBalanceStmt.close()
    conn.close()

    balance
  }

  def deleteAccount(userKey: Int): Boolean = {

    var deleteSuccessful = false

    val conn = DataSource.getConnection
    val deleteStmt = conn
      .prepareStatement("DELETE FROM users WHERE id = ?;")
    deleteStmt.setInt(1, userKey)
    val resultSet = deleteStmt.executeUpdate()

    val deleteUpdate = Try(deleteStmt.executeUpdate())

    deleteUpdate match {
      case Success(value) => {
        deleteSuccessful = true
      }
      case Failure(exception) => {
        deleteSuccessful = false
        println("Exception occurred during depositing funds")
        exception.printStackTrace()
      }
    }

    deleteStmt.close()
    conn.close()

    deleteSuccessful
  }

}
