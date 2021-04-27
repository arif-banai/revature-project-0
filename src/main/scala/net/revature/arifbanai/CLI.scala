package net.revature.arifbanai

import scala.io.StdIn
import scala.util.matching.Regex

class CLI {

  // regex pattern for extracting command and arguments from input
  val commandPatternRegex: Regex = "(\\w+)\\s*(.*)".r
  val transferCommandPatternRegex: Regex = "(\\w+)\\s*(\\w+)\\s*(.*)".r

  def startCommandLoop(): Unit = {

    // Setup JDBC
    classOf[org.postgresql.Driver].newInstance();


    printWelcome()
    var mainLoop = true

    while(mainLoop) {
      printInitMenu()

      var input = StdIn.readLine()
      input match {
        case commandPatternRegex(cmd, arg) if cmd == "login" => {
          println("Enter your username")
          var username = StdIn.readLine()

          println("Enter your password")
          var password = StdIn.readLine()

          if(MainLoopSQL.login(username, password)) {
            println(s"Welcome, $username")

            val userKey = UserSQL.getKey(username)

            if(userKey < 1) {
              throw new Exception("Invalid key obtained")
            } else {
              userLoop(userKey, username)
            }
          } else {
            println("Incorrect username or password, try again")
          }

        }

        case commandPatternRegex(cmd, arg) if cmd == "newuser" => {
          println("Enter a unique username")
          var username = StdIn.readLine()

          println("Enter a password")
          var password = StdIn.readLine()

          if(MainLoopSQL.createUser(username, password)) {
            println("New user created, you may now login")
          } else {
            println("User creation failed, maybe your username was not unique?")
          }
        }

        case commandPatternRegex(cmd, arg) if cmd == "import" => {
          if(CSVReader.importFromCSV(arg)) {
            println(s"Users imported from $arg")
          }
        }

        case commandPatternRegex(cmd, arg) if cmd == "exit" => {
          println("Goodbye!")
          mainLoop = false
        }

        case default => {
          System.err.println("Invalid command, please try again")
        }
      }
    }

    println("End of program execution")
  }

  def userLoop(userKey: Int, username: String): Unit = {
    var loggedIn = true

    while(loggedIn) {
      printUserOptions()

      var input = StdIn.readLine()
      input match {

        case commandPatternRegex(cmd, arg) if cmd == "balance" => {
          val balance: Long = UserSQL.checkBalance(userKey)

          if(balance < 0) {
            throw new Exception("Invalid balance.")
          }

          println(s"Your balance is $$${balance}" + "\n")
        }

        case commandPatternRegex(cmd, arg) if cmd == "deposit" => {
          if(UserSQL.deposit(userKey, arg.toLong)) {
            println(s"You deposited $$$arg to your account")
          }
        }
        case commandPatternRegex(cmd, arg) if cmd == "withdraw" => {
          if(UserSQL.withdraw(userKey, arg.toLong)) {
            println(s"You withdrew $$$arg from your account")
          }
        }
        case transferCommandPatternRegex(cmd, targetUser, amount) if cmd == "transfer" => {
          if(UserSQL.transfer(userKey, targetUser, amount.toLong)) {
            println(s"You transferred $$${amount.toLong} to $targetUser")
          }
        }
        case commandPatternRegex(cmd, arg) if cmd == "logout" => {
          loggedIn = false
          println(s"Logging out. See you later, $username!")
        }
        case commandPatternRegex(cmd, arg) if cmd == "close" => {
          println("Are you sure you want to close your account?")
          println("Y = yes, Anything else = no")
          input = StdIn.readLine()

          input match {
            case commandPatternRegex(cmd, arg) if cmd.equalsIgnoreCase("y") => {
              if(UserSQL.deleteAccount(userKey)) {
                println("Your account has been closed, thanks for using Smeefy Banking Services!")
                loggedIn = false
              }
            }
            case default =>
              println("Not closing account.")
          }
        }

        case default => {
          System.err.println("Invalid command, please try again")
        }
      }
    }

    println("Returning to main menu \n")
  }

  def printWelcome(): Unit = {
    println("Welcome to Smeefy Banking Services!")
  }

  def printInitMenu(): Unit = {
    List(
      "Menu options:",
      "newuser: create a new account with a balance of 0",
      "login: login to your account",
      "import [filename]: import data from csv into database",
      "exit: exit the banking service"
    ).foreach(println)
  }

  def printUserOptions(): Unit = {
    List(
      "balance: check your current balance",
      "deposit [amount]: deposit [amount] of money into [accountNumber]",
      "withdraw [amount]: withdraw [amount] of money from [accountNumber]",
      "transfer [username] [amount]: transfer [amount] of money from your account to [username]'s account",
      "logout: logout of your account",
      "close: close your account"
    ).foreach(println)
  }

  def importData(filename: String): Unit = {
    // Import data from csv file
  }


}
