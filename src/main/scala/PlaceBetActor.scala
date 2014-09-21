import akka.actor.{ActorLogging, Actor}
import akka.event.Logging

class PlaceBetActor extends Actor with ActorLogging{

  var bet = 0

  def setup: Receive = {
    case Setup(sum, payMultiple) => {
      context.become( active(sum,payMultiple) )
    }
    case _ => log.error("PlaceBetActors must be initialized through a Setup message")
  }

  def active(sum: Int, multiplier: Double) : Receive = {
    case Roll(d1,d2) => {
      //Pays out assuming the bet will stay the same
      if(d1+d2 == sum){
        sender ! Payout( Math.floor( bet * multiplier ).toInt )
      }
    }

    case MakeBet(amount) => {
      log.debug(s"Changing active bet from $bet to $amount")
      val oldBet = bet
      bet = amount
      sender ! Payout(oldBet)
    }

    case ReduceBet(amount) => {
      log.debug(s"Reducing active bet by $amount")
      if(amount <= bet) {
        sender ! Payout(amount)
        bet -= amount
      } else {
        log.debug(s"Player only has $bet, cannot reduce by $amount. Returning full bet")
        sender ! Payout(bet)
        bet = 0
      }
    }

    case Press(amount) => {
      log.debug(s"Adding $amount to existing bet of $bet")
      bet += amount
    }

    case _ => log.error("Unsupported message type")
  }

  def receive = setup

}
