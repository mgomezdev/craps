import akka.actor.Actor

class LineBetActor extends Actor {
  var establishedPoint = 0
  var bet = 0
  var odds = 0
  def noPoint: Receive = {
    case Roll(d1, d2) =>
      val result = d1 + d2
      result match {
        case 7 => sender ! Payout(bet*2)
        case 11 => sender ! Payout(bet*2)
        case 2 => bet = 0
        case 3 => bet = 0
        case 12 => bet = 0
        case p => establishedPoint = p
      }

    case MakeBet(amount) =>
      //give the player back the initial amount
      sender ! bet

      //now place the delta
      bet = amount

  }

  def point: Receive = {
    case Roll(d1,d2) =>
      val result = d1 + d2
      result match {
        case 7 =>
          //Lose
          bet = 0
          odds = 0
          establishedPoint = 0
          context.become(noPoint)

        case thePoint =>
          //Win
          establishedPoint = 0
          //pay the line (give the player back the original bet too
          sender ! Payout(bet *2)
          bet = 0

          //calculate the odds winnings
          val oddsPayout = thePoint match {
            case 4 => odds * 2
            case 10 => odds * 2
            case 5 => odds * 3/2
            case 9 => odds * 3/2
            case 6 => odds * 6/5
            case 8 => odds * 6/5
          }

          //give back the odds and the winnings at the same time
          sender ! Payout(odds + oddsPayout)
          odds = 0
          context.become(noPoint)

      }

      //recycling MakeBet as a means to puts odds down
      // The user can adjust the odds
    case MakeBet(amount) =>
      //give the player back the original
      sender ! Payout(odds)

      //now place the odds
      odds = amount

  }

  def receive = noPoint

}
