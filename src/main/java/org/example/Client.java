package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.grpc.GreetingServiceGrpc;
import org.example.grpc.GreetingServiceOuterClass;

public class Client {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080")
                .usePlaintext()
                .build();

        GreetingServiceGrpc.GreetingServiceBlockingStub stub =
                GreetingServiceGrpc.newBlockingStub(channel);

        GreetingServiceOuterClass.HelloRequest request = GreetingServiceOuterClass.HelloRequest
                .newBuilder()
                .setName("Roman")
                .build();

        GreetingServiceOuterClass.HelloResponse response = stub.greeting(request);

        System.out.println(response);

        channel.shutdownNow();
    }
}