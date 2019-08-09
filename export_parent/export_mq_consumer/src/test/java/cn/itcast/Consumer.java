package cn.itcast;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class Consumer {
    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext ac =
                new ClassPathXmlApplicationContext("applicationContext-rabbitmq-consumer.xml");
        System.in.read();
    }
}
