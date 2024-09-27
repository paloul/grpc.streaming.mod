package edu.caltech.cast.indy

//#import
import scala.concurrent.Future
import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.stream.Attributes
import akka.stream.scaladsl.{BroadcastHub, Keep, MergeHub, PartitionHub, Sink, Source}
import com.google.protobuf.empty.Empty
import edu.caltech.cast.indy.grpc._
//#import

class GreeterServiceImpl(system: ActorSystem[_]) extends GreeterService {
  private implicit val sys: ActorSystem[_] = system
  import sys.executionContext

  //#service-request-reply
  private val (inboundHub: Sink[HelloRequest, NotUsed], outboundHub: Source[HelloReply, NotUsed]) =
    MergeHub.source[HelloRequest]
      .map(request => HelloReply(s"Hello, ${request.name}"))
      .toMat(BroadcastHub.sink[HelloReply])(Keep.both)
      .run()
  //#service-request-reply

  private val overflowStrategy = akka.stream.OverflowStrategy.dropHead
  private val (inbound, outbound) =
    Source.queue[HelloReply](100, overflowStrategy).toMat(BroadcastHub.sink[HelloReply])(Keep.both).run()

  override def sayHello(in: HelloRequest): Future[HelloReply] = {
    println(s"sayHello to ${in.name}")
    Future.successful(HelloReply(s"Hello, ${in.name}"))
  }

  override def keepSayingHello(in: Source[HelloRequest, NotUsed]): Future[HelloReply] = {
    println(s"sayHello to in stream...")
    in.runWith(Sink.seq).map(elements => HelloReply(s"Hello, ${elements.map(_.name).mkString(", ")}"))
  }

  override def sayHelloToAll(in: Source[HelloRequest, NotUsed]): Future[HelloReply] = {
    //in.runWith(inboundHub)
    in.runForeach(req => inbound.offer(HelloReply(s"Hello, ${req.name}")))
      .map(_ => HelloReply("Greeting Complete"))
  }

  override def keepGettingHello(in: HelloRequest): Source[HelloReply, NotUsed] = {
    outbound
  }
}
