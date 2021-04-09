package com.dphong.fcmpush.push;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class FcmPushTest {
    final String targetToken = "deviceTokenasdfasdfasdfasdf";
    final String imageUrl = "https://png.pngtree.com/png-vector/20191004/ourlarge/pngtree-person-icon-png-image_1788612.jpg";
    final String fireBaseAppName = "testFireBaseApp";

    FirebaseMessaging firebaseMessaging;

    @BeforeEach
    private void firebaseAppInitialize() {
        InputStream serviceAccount = null;
        FirebaseOptions options = null;

        try {
            ClassPathResource resource = new ClassPathResource("admin sdk json");
            serviceAccount = resource.getInputStream();
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options, fireBaseAppName);

            this.firebaseMessaging = FirebaseMessaging.getInstance(FirebaseApp.getInstance(fireBaseAppName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void normalPushTest() {
        try {
            Message message = Message.builder()
                    .putData("score", "850")
                    .putData("time", "2:45")
                    .setToken(targetToken)
                    .build();

            String response = this.firebaseMessaging.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void imagePushTest() {
        try {
            Message message = Message.builder()
                    .putData("score", "850")
                    .putData("time", "2:45")
                    .setNotification(Notification.builder()
                            .setTitle("제목")
                            .setBody("내용")
                            .setImage(imageUrl)
                            .build())
                    .setToken(targetToken)
                    .build();

            String response = this.firebaseMessaging.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void multicastPushTest() {
        List<String> multicastTokens = Arrays.asList(
                targetToken
                , "tokenA"
                , "tokenB"
        );

        MulticastMessage message = MulticastMessage.builder()
                .putData("sample", "test")
                .setNotification(Notification.builder()
                        .setTitle("hi")
                        .setBody("multicast test")
                        .setImage(imageUrl)
                        .build()
                )
                .addAllTokens(multicastTokens)
                .build();

        try {
            BatchResponse response = firebaseMessaging.sendMulticast(message);
            System.out.println(response.getSuccessCount() + " messages were sent successfully");
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void batchPushTest() {
        List<Message> messages = Arrays.asList(
                Message.builder()
                        .setNotification(Notification.builder()
                                .setTitle("hi")
                                .setBody("batch push test")
                                .setImage(imageUrl)
                                .build())
                        .setToken(targetToken)
                        .build(),
                Message.builder()
                        .setNotification(Notification.builder()
                                .setTitle("hi2")
                                .setBody("batch push test2")
                                .build())
                        .setToken(targetToken)
                        .build(),
                Message.builder()
                        .setNotification(Notification.builder()
                                .setTitle("hi3")
                                .setBody("batch push test3")
                                .build())
                        .setToken(targetToken)
                        .build()
        );

        try {
            BatchResponse response = firebaseMessaging.sendAll(messages);
            System.out.println(response.getSuccessCount() + " messages were sent successfully");
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void androidPush() {
        Message message = Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.NORMAL)
                        .setNotification(AndroidNotification.builder()
                                .setTitle("hi~~!!")
                                .setBody("android test")
                                .setColor("#f45342")
                                .setIcon(imageUrl)
                                .build()
                        ).build()
                )
                .setToken(targetToken)
                .build();

        try {
            String response = firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }
}
