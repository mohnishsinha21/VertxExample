package org.example;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.elements.exception.ConnectorException;

import java.io.IOException;

public class CoapClientExample {

    public static void main(String[] args) throws ConnectorException, IOException {
        // Create a CoAP client for the target URI
        CoapClient client = new CoapClient("coap://localhost/hello");

        // Send a GET request
        CoapResponse response = client.get();

        // Check if the response is valid
        if (response != null) {
            System.out.println("Response Code: " + response.getCode());
            System.out.println("Response Text: " + response.getResponseText());
        } else {
            System.out.println("No response received.");
        }

        // Close the client
        client.shutdown();
    }
}
