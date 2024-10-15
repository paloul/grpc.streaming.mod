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

# The packageBin command above generates a ZIP containing all necessary
# files and scripts to run the application. 
# Unzip the zip file under `target/universal`
unzip ./target/universal/groundstation-commandserver-0.1.0-SNAPSHOT.zip

# The contents of the unzipped folder 
# grpc.streaming.mod/target/universal/groundstation-commandserver-0.1.0-SNAPSHOT
$ groundstation-commandserver-0.1.0-SNAPSHOT git:(main) ls -l
total 0
drwxr-xr-x   8 paloul  staff   256 Oct 15 13:56 bin
drwxr-xr-x  46 paloul  staff  1472 Oct 15 13:56 lib


# Use the appropriate start script for your environment in the bin folder
./target/universal/groundstation-commandserver-0.1.0-SNAPSHOT/bin/greeter-server
```

### Key Part
The main change is how the Greeter Service is implemented in `GreeterServiceImpl.scala`.
Take a look at `Line 21-22` and the definition of inbound and outbound. These two are shared
between the implementations of gRPC services PublishHellos and SubscribeHellos.

