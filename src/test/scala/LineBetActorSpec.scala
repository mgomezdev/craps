import akka.actor.{Props, ActorSystem}
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}
import scala.concurrent.duration._

class LineBetActorSpec(_system: ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("LineBetActorSpec"))

  override def afterAll(): Unit = {
    system.shutdown()
    system.awaitTermination(15.seconds)
  }

  "Line bet actors" should "default with no point established" in {
    val lineActor = TestActorRef(Props[LineBetActor])
    lineActor.underlyingActor.asInstanceOf[LineBetActor].establishedPoint shouldBe 0
  }

  it should "Pay the bet on a 7 when no point is set" in {
    (1 to 6).foreach { num =>
      val lineActor = TestActorRef(Props[LineBetActor])
      //confirm no point set
      lineActor.underlyingActor.asInstanceOf[LineBetActor].establishedPoint shouldBe 0
      //Place the bet
      lineActor ! MakeBet(100)
      //should be nothing there to return
      expectMsgType[Int] shouldBe 0
      //roll a 7
      lineActor ! Roll(num, 7-num)
      //get PAID
      expectMsgType[Payout].amount shouldBe 200
    }
  }

  it should "Pay the bet on a 11 when no point is set" in {
    (5 to 6).foreach { num =>
      val lineActor = TestActorRef(Props[LineBetActor])
      //confirm no point set
      lineActor.underlyingActor.asInstanceOf[LineBetActor].establishedPoint shouldBe 0
      //Place the bet
      lineActor ! MakeBet(100)
      //should be nothing there to return
      expectMsgType[Int] shouldBe 0
      //roll a 7
      lineActor ! Roll(num, 11-num)
      //get PAID
      expectMsgType[Payout].amount shouldBe 200
    }
  }

  it should "be able to set the point to 4" in {
    (1 to 2).foreach { num =>
      val lineActor = TestActorRef(Props[LineBetActor])
      lineActor ! Roll(num, 4-num)
      lineActor.underlyingActor.asInstanceOf[LineBetActor].establishedPoint shouldBe 4
    }
  }
  it should "be able to set the point to 5" in {
    (1 to 4).foreach { num =>
      val lineActor = TestActorRef(Props[LineBetActor])
      lineActor ! Roll(num, 5-num)
      lineActor.underlyingActor.asInstanceOf[LineBetActor].establishedPoint shouldBe 5
    }
  }
  it should "be able to set the point to 6" in {
    (1 to 5).foreach { num =>
      val lineActor = TestActorRef(Props[LineBetActor])
      lineActor ! Roll(num, 6-num)
      lineActor.underlyingActor.asInstanceOf[LineBetActor].establishedPoint shouldBe 6
    }
  }
  it should "be able to set the point to 8" in {
    (2 to 6).foreach { num =>
      val lineActor = TestActorRef(Props[LineBetActor])
      lineActor ! Roll(num, 8-num)
      lineActor.underlyingActor.asInstanceOf[LineBetActor].establishedPoint shouldBe 8
    }
  }
  it should "be able to set the point to 9" in {
    (3 to 6).foreach { num =>
      val lineActor = TestActorRef(Props[LineBetActor])
      lineActor ! Roll(num, 9-num)
      lineActor.underlyingActor.asInstanceOf[LineBetActor].establishedPoint shouldBe 9
    }
  }
  it should "be able to set the point to 10" in {
    (4 to 6).foreach { num =>
      val lineActor = TestActorRef(Props[LineBetActor])
      lineActor ! Roll(num, 10-num)
      lineActor.underlyingActor.asInstanceOf[LineBetActor].establishedPoint shouldBe 10
    }
  }
}
