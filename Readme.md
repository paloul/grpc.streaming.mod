## gRPC Streaming Pub/Sub Example

This example builds on the great tutorials and examples available at the [Akka Documentation](https://doc.akka.io). 
Specifically take a look at [Akka gRPC Quickstart with Scala](https://doc.akka.io/docs/akka-grpc/current/quickstart-scala/streaming.html)
to understand how to implement a bidirectional streaming gRPC call. 

My example here is a slightly modified version of the Akka gRPC Streaming example. It modifies 
the original to provide typical Pub/Sub functionality over separate gRPC service endpoints: one
endpoint to publish data, and one endpoint to subscribe to data. This is accomplished by using 
[Akka Streams](https://doc.akka.io/docs/akka/current/stream/index.html).

### Dependencies
1. Java 21
2. Scala 
3. SBT

### Build
```bash
# Compile first to generate protobuf templates (if missing)
sbt compile

# Run the example server with SBT
sbt "runMain edu.caltech.cast.indy.GreeterServer"
```
The very useful [SBT Native Packager](https://www.scala-sbt.org/sbt-native-packager/) is used. It provides
some basic abstractions for packaging SBT based projects and running them outside SBT.
```bash
# Use Native Packager to generate universal Java with all
# appropriate start scripts and fat/uber JARs
sbt universal:packageBin

# Unzip the zip file under `target/universal`
# Use the appropriate start script for your environment in the bin folder
./target/universal/unzip-folder-name/bin/greeter-server
```

### Key Part
The main change is how the Greeter Service is implemented in `GreeterServiceImpl.scala`.
Take a look at `Line 21-22` and the definition of inbound and outbound. These two are shared
between the implementations of gRPC services PublishHellos and SubscribeHellos.

