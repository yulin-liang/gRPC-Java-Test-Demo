package server;

import com.proto.greet.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

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
}
