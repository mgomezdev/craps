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

  "Line bet actors" should "Pay the bet on a 7 when no point is set" in {
    val lineActor = TestActorRef(Props[LineBetActor])
    //Place the bet
    lineActor ! MakeBet(100)
    //should be nothing there to return
    expectMsgType[Int] shouldBe 0
    //roll a 7
    lineActor ! Roll(3,4)
    //get PAID
    expectMsgType[Payout].amount shouldBe 200
  }

  "Line bet actors" should "Pay the bet on a 11 when no point is set" in {
    val lineActor = TestActorRef(Props[LineBetActor])
    //Place the bet
    lineActor ! MakeBet(100)
    //should be nothing there to return
    expectMsgType[Int] shouldBe 0
    //roll a 7
    lineActor ! Roll(6,5)
    //get PAID
    expectMsgType[Payout].amount shouldBe 200
  }

  "Line bet actors" should "be able to set the point to 4 (1,3)" in {
    val lineActor = TestActorRef(Props[LineBetActor])
    lineActor ! Roll(1,3)
    lineActor.underlyingActor.asInstanceOf[LineBetActor].establishedPoint shouldBe 4
  }

  "Line bet actors" should "be able to set the point to 4 (2,2)" in {
    val lineActor = TestActorRef(Props[LineBetActor])
    lineActor ! Roll(2,2)
    lineActor.underlyingActor.asInstanceOf[LineBetActor].establishedPoint shouldBe 4
  }

  "Line bet actors" should "be able to set the point to 4 (3,1)" in {
    val lineActor = TestActorRef(Props[LineBetActor])
    lineActor ! Roll(3,1)
    lineActor.underlyingActor.asInstanceOf[LineBetActor].establishedPoint shouldBe 4
  }

  "Line bet actors" should "be able to set the point to 5 (1,4)" in {
    val lineActor = TestActorRef(Props[LineBetActor])
    lineActor ! Roll(1,4)
    lineActor.underlyingActor.asInstanceOf[LineBetActor].establishedPoint shouldBe 5
  }

  "Line bet actors" should "be able to set the point to 5 (2,3)" in {
    val lineActor = TestActorRef(Props[LineBetActor])
    lineActor ! Roll(2,3)
    lineActor.underlyingActor.asInstanceOf[LineBetActor].establishedPoint shouldBe 5
  }

  "Line bet actors" should "be able to set the point to 5 (3,2)" in {
    val lineActor = TestActorRef(Props[LineBetActor])
    lineActor ! Roll(3,2)
    lineActor.underlyingActor.asInstanceOf[LineBetActor].establishedPoint shouldBe 5
  }

  "Line bet actors" should "be able to set the point to 5 (4,1)" in {
    val lineActor = TestActorRef(Props[LineBetActor])
    lineActor ! Roll(4,1)
    lineActor.underlyingActor.asInstanceOf[LineBetActor].establishedPoint shouldBe 5
  }

  "Line bet actors" should "be able to set the point to 6 (1,5)" in {
    val lineActor = TestActorRef(Props[LineBetActor])
    lineActor ! Roll(1,5)
    lineActor.underlyingActor.asInstanceOf[LineBetActor].establishedPoint shouldBe 6
  }

  "Line bet actors" should "be able to set the point to 6 (2,4)" in {
    val lineActor = TestActorRef(Props[LineBetActor])
    lineActor ! Roll(2,4)
    lineActor.underlyingActor.asInstanceOf[LineBetActor].establishedPoint shouldBe 6
  }

  "Line bet actors" should "be able to set the point to 6 (3,3)" in {
    val lineActor = TestActorRef(Props[LineBetActor])
    lineActor ! Roll(3,3)
    lineActor.underlyingActor.asInstanceOf[LineBetActor].establishedPoint shouldBe 6
  }

  "Line bet actors" should "be able to set the point to 6 (4,2)" in {
    val lineActor = TestActorRef(Props[LineBetActor])
    lineActor ! Roll(4,2)
    lineActor.underlyingActor.asInstanceOf[LineBetActor].establishedPoint shouldBe 6
  }

  "Line bet actors" should "be able to set the point to 6 (5,1)" in {
    val lineActor = TestActorRef(Props[LineBetActor])
    lineActor ! Roll(5,1)
    lineActor.underlyingActor.asInstanceOf[LineBetActor].establishedPoint shouldBe 6
  }
}
