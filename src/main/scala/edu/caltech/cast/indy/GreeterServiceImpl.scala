package edu.caltech.cast.indy

//#import
import scala.concurrent.Future
import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.stream.scaladsl.{BroadcastHub, Keep, Sink, Source}
import com.google.protobuf.empty.Empty
import edu.caltech.cast.indy.grpc._
//#import

class GreeterServiceImpl(system: ActorSystem[_]) extends GreeterService {
  private implicit val sys: ActorSystem[_] = system
  import sys.executionContext

  // If the buffer is full when a new element arrives,
  // drops the oldest element from the buffer to make
  // space for the new element.
  private val bufferSize = 128
  private val overflowStrategy = akka.stream.OverflowStrategy.dropHead
  private val (inbound, outbound) =
    Source.queue[HelloReply](bufferSize, overflowStrategy).toMat(BroadcastHub.sink[HelloReply])(Keep.both).run()

  override def sayHello(in: HelloRequest): Future[HelloReply] = {
    println(s"sayHello to ${in.name}")
    Future.successful(HelloReply(s"Hello, ${in.name}"))
  }

  override def keepSayingHello(in: Source[HelloRequest, NotUsed]): Future[HelloReply] = {
    println(s"sayHello to in stream...")
    in.runWith(Sink.seq).map(elements => HelloReply(s"Hello, ${elements.map(_.name).mkString(", ")}"))
  }

  override def sayHelloToAll(in: Source[HelloRequest, NotUsed]): Future[HelloReply] = {
    in.runForeach(req => inbound.offer(HelloReply(s"Hello, ${req.name}")))
      .map(_ => HelloReply("Greeting Complete"))
    // Map executes and replies Future[HelloReply] on termination of [in] source stream
  }

  override def keepGettingHello(in: HelloRequest): Source[HelloReply, NotUsed] = {
    outbound
  }
}
