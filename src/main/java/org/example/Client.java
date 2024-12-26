package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.grpc.GreetingServiceGrpc;
import org.example.grpc.GreetingServiceOuterClass;

import java.util.ArrayList;
import java.util.List;

public class Client {
    public static void main(String[] args) {
        String serverAddress = System.getenv("SERVER_ADDRESS");

        ManagedChannel channel = ManagedChannelBuilder.forTarget(serverAddress)
                .usePlaintext()
                .build();

        GreetingServiceGrpc.GreetingServiceBlockingStub stub =
                GreetingServiceGrpc.newBlockingStub(channel);

        List<String> names = new ArrayList<>();
        names.add("Roman");
        names.add("Alexandra");
        names.add("Artem");
        names.add("Natasha");
        names.add("Kirill");
        names.add("Olga");

        for (String name : names) {
            GreetingServiceOuterClass.HelloRequest request = GreetingServiceOuterClass.HelloRequest
                    .newBuilder()
                    .setName(name)
                    .build();

            GreetingServiceOuterClass.HelloResponse response = stub.greeting(request);

            System.out.println(response);

        }

        channel.shutdownNow();
    }
}