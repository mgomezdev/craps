import akka.actor.Actor
import akka.event.Logging

class PlaceBetActor extends Actor {
  val log = Logging.getLogger(context.system, this)

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
      sender ! Payout(bet)
      bet = amount
    }

    case ReduceBet(amount) => {
      if(amount <= bet) {
        sender ! Payout(amount)
        bet -= amount
      } else {
        sender ! Payout(bet)
        bet = 0
      }
    }

    case Press(amount) => {
      bet += amount
    }
  }

  def receive = setup

}
