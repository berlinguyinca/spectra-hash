package edu.ucdavis.fiehnlab.splash.splashanalysis;

import com.rabbitmq.client.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;

/**
 * Created by sajjan on 10/27/2015.
 */
@SpringBootApplication
@EnableAutoConfiguration
public class Application implements CommandLineRunner {
    public final static String RECEIVING_QUEUE_NAME = "splash_analysis";
    public final static String SENDING_QUEUE_NAME = "splash_aggregation";

    public final static String SERVER = "gose.fiehnlab.ucdavis.edu";
    public static String FILENAME = "/Users/sajjan/Projects/spectra-hash/test-precomputed.csv";

    private Channel receivingChannel;
    private Channel sendingChannel;


    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.setWebEnvironment(false);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        if(args.length > 0)
            FILENAME = args[0];
        System.out.println("Processing file "+ FILENAME);


        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(SERVER);
        factory.setUsername("sajjan");
        factory.setPassword("");
        Connection connection = factory.newConnection();

        receivingChannel = connection.createChannel();
        receivingChannel.queueDeclare(RECEIVING_QUEUE_NAME, false, false, false, null);
        receivingChannel.basicQos(1);

        sendingChannel = connection.createChannel();
        sendingChannel.queueDeclare(SENDING_QUEUE_NAME, false, false, false, null);

        receiveMessages();
    }

    public void receiveMessages() throws IOException {
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(receivingChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");

                SplashAnalyzer.analyzeSplashes(Integer.parseInt(message), sendingChannel);

                long deliveryTag = envelope.getDeliveryTag();
                receivingChannel.basicAck(deliveryTag, false);
            }
        };

        receivingChannel.basicConsume(RECEIVING_QUEUE_NAME, false, consumer);
    }
}
