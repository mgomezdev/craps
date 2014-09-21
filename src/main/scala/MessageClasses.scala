/**
 * Created by mgomez on 8/12/14.
 */
case class Roll(d1: Int, d2: Int)
case class MakeBet(amount: Int)
case class ReduceBet(amount: Int)
case class Press(amount: Int)
case class Payout(amount: Int)
case class Setup(sum: Int, payout: Double)
