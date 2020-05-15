package server;

import com.proto.greet.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

public class GreetServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        System.out.println("Receive a greet request");
        Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();
        String lastName = greeting.getLastName();

        if (firstName.length() == 0 || lastName.length() == 0) {
            System.out.println("The arguments are invalid");
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("First name and last name can not be empty")
                    .asRuntimeException()
            );
            return;
        }

        System.out.println("Send the response back to the client");
        String result = "Hello " + firstName + " " + lastName + ", welcome to the gPRC world!";
        GreetResponse response = GreetResponse.newBuilder()
                .setResult(result)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Greeting> greetClientStream(StreamObserver<GreetResponse> responseObserver) {
        StreamObserver<Greeting> requestObserver = new StreamObserver<Greeting>() {
            List<String> greetingList = new ArrayList<>();

            @Override
            public void onNext(Greeting value) {
                String user = value.getFirstName();
                if (value.getLastName().length() > 0) {
                    user = user + " " + value.getLastName();
                }

                greetingList.add(user);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                String result = "Welcome";
                for (int i = 0; i < greetingList.size(); i++) {
                    result += " " + greetingList.get(i);
                    if (i != greetingList.size() - 1) {
                        result += ",";
                    } else {
                        result += "!";
                    }
                }

                GreetResponse response = GreetResponse.newBuilder()
                        .setResult(result)
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };

        return requestObserver;
    }
}
