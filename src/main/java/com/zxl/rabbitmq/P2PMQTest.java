package com.zxl.rabbitmq;

import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Administrator on 2017/7/1.
 */
public class P2PMQTest {
    @org.junit.Test
    public void testPublish() throws IOException {
        Channel channel = ChannelPool.getChannel();
        if (channel == null) {
            System.out.println("channel can't get!");
            return;
        }
        String queueName = "queueName1";
        channel.queueDeclare(queueName, false, false, false, null);
        channel.basicPublish("",queueName,null,"hello p2pMq".getBytes());
        ChannelPool.releaseChannel(channel);
    }
    @Test
    public void testConsume() throws IOException, InterruptedException {
        Channel channel = ChannelPool.getChannel();
        String queueName = "queueName1";
        channel.queueDeclare(queueName, false, false, false, null);
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName,false,"",consumer);
        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String msg = new String(delivery.getBody());
            System.out.println(msg);
        }
    }
}
