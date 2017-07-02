package com.zxl.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sun.javafx.collections.ChangeHelper;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.*;

/**
 * Created by Administrator on 2017/7/1.
 */
public class ChannelPool {

    public static final String HOST_NAME="192.168.202.130";
    public static final int PORT=5672;
    public static final String USER_NAME="guest";
    public static final String PASSWORD="guest";
    private static volatile ConnectionFactory connectionFactory;
    private static BlockingQueue<Channel> channelQueue = new LinkedBlockingQueue<Channel>(50);

    public static ConnectionFactory getConnectionFactory() {
        if (connectionFactory == null) {
            synchronized (HOST_NAME) {
                if (connectionFactory == null) {
                    connectionFactory = new ConnectionFactory();
                    connectionFactory.setHost(HOST_NAME);
                    connectionFactory.setPort(PORT);
                    connectionFactory.setUsername(USER_NAME);
                    connectionFactory.setPassword(PASSWORD);
                }
            }
        }
        return connectionFactory;
    }

    public static Connection getConnection() {
        try {
            return getConnectionFactory().newConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Channel getChannel() {
        Channel channel = null;

        try {
           channel = channelQueue.poll(3000, TimeUnit.MILLISECONDS);
            if (channel == null) {
                channel =getConnection().createChannel();
                channelQueue.offer(channel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return channel;
    }

    public static void releaseChannel(Channel channel) {
        if (channel != null) {
            channelQueue.offer(channel);
        }
    }
}
