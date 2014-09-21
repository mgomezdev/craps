import akka.actor.{Props, ActorSystem}
import akka.testkit.{EventFilter, TestActorRef, ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

import scala.concurrent.duration._

class PlaceBetActorSpec(_system: ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {

  def this() = this(
    ActorSystem("PlaceBetActorSystem",
    ConfigFactory.parseString(""" akka.loggers = ["akka.testkit.TestEventListener"]"""))
  )

  override def afterAll(): Unit = {
    system.shutdown()
    system.awaitTermination(15.seconds)
  }

  "Place bet actors" should "default with no bet" in {
    val placeActor = TestActorRef(Props[PlaceBetActor])
    placeActor.underlyingActor.asInstanceOf[PlaceBetActor].bet shouldBe 0
  }

  it should "not respond to messages before setup and log an error" in {
    val placeActor = TestActorRef(Props[PlaceBetActor])
    EventFilter.error(occurrences = 1) intercept {
      placeActor ! Roll(1,1)
      expectNoMsg(100.millis)
    }
    EventFilter.error(occurrences = 1) intercept {
      placeActor ! MakeBet(10)
      expectNoMsg(100.millis)
    }
    EventFilter.error(occurrences = 1) intercept {
      placeActor ! Press(10)
      expectNoMsg(100.millis)
    }
    EventFilter.error(occurrences = 1) intercept {
      placeActor ! ReduceBet(5)
      expectNoMsg(100.millis)
    }
  }

  it should "transition to responsive state after setup" in {
    val placeActor = TestActorRef(Props[PlaceBetActor])
    placeActor ! Setup(4,2)
    expectNoMsg(100.millis)
    placeActor ! MakeBet(10)
    expectMsg[Payout](Payout(0))
  }

  it should "allow player to make an initial bet" in {
    val placeActor = TestActorRef(Props[PlaceBetActor])
    placeActor ! Setup(4,2)
    expectNoMsg(100.millis)
    placeActor ! MakeBet(10)
    expectMsg[Payout](Payout(0))
    placeActor.underlyingActor.asInstanceOf[PlaceBetActor].bet shouldBe 10
  }

  it should "allow player to increase (press) the bet" in {
    val placeActor = TestActorRef(Props[PlaceBetActor])
    placeActor ! Setup(4,2)
    expectNoMsg(100.millis)
    placeActor ! MakeBet(10)
    expectMsg[Payout](Payout(0))
    placeActor ! Press(10)
    expectNoMsg(100.millis)
    placeActor.underlyingActor.asInstanceOf[PlaceBetActor].bet shouldBe 20
  }

  it should "allow player to decrease the bet" in {
    val placeActor = TestActorRef(Props[PlaceBetActor])
    placeActor ! Setup(4,2)
    expectNoMsg(100.millis)
    placeActor ! MakeBet(10)
    expectMsg[Payout](Payout(0))
    placeActor ! ReduceBet(3)
    expectMsg[Payout](Payout(3))
    placeActor.underlyingActor.asInstanceOf[PlaceBetActor].bet shouldBe 7
  }

  it should "allow player to set a new bet value (increase)" in {
    val placeActor = TestActorRef(Props[PlaceBetActor])
    placeActor ! Setup(4,2)
    expectNoMsg(100.millis)
    placeActor ! MakeBet(10)
    expectMsg[Payout](Payout(0))
    placeActor.underlyingActor.asInstanceOf[PlaceBetActor].bet shouldBe 10
    placeActor ! MakeBet(30)
    expectMsg[Payout](Payout(10))
    placeActor.underlyingActor.asInstanceOf[PlaceBetActor].bet shouldBe 30
  }

  it should "allow player to set a new bet value (decrease)" in {
    val placeActor = TestActorRef(Props[PlaceBetActor])
    placeActor ! Setup(4,2)
    expectNoMsg(100.millis)
    placeActor ! MakeBet(30)
    expectMsg[Payout](Payout(0))
    placeActor.underlyingActor.asInstanceOf[PlaceBetActor].bet shouldBe 30
    //test decrease
    placeActor ! MakeBet(5)
    expectMsg[Payout](Payout(30))
    placeActor.underlyingActor.asInstanceOf[PlaceBetActor].bet shouldBe 5
  }

  it should "payout the expected amount for a win" in {
    val placeActor = TestActorRef(Props[PlaceBetActor])
    val roll = 4
    val multiple = 2
    val bet = 30
    placeActor ! Setup(roll, multiple)
    expectNoMsg(100.millis)
    placeActor ! MakeBet(bet)
    expectMsg[Payout](Payout(0))
    (1 to 3).foreach { low =>
      placeActor ! Roll(low, 4-low)
      expectMsg[Payout](Payout(bet * multiple))
    }
  }

  it should "not respond for non-winning rolls" in {
    val placeActor = TestActorRef(Props[PlaceBetActor])
    val roll = 4
    val multiple = 2
    val bet = 30
    placeActor ! Setup(roll, multiple)
    expectNoMsg(100.millis)
    placeActor ! MakeBet(bet)
    expectMsg[Payout](Payout(0))
    placeActor ! Roll(5,5)
    expectNoMsg(100.millis)
  }
}