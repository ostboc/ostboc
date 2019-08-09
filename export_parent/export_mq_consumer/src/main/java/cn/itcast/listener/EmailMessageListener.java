package cn.itcast.listener;

import cn.itcast.utils.MailUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EmailMessageListener implements MessageListener {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void onMessage(Message message) {
        try {
            // 获取消息内容，返回json节点对象
            JsonNode jsonNode = MAPPER.readTree(message.getBody());
            // 获取消息key对应的值
            String email = jsonNode.get("email").asText();
            String subject = jsonNode.get("subject").asText();
            String content = jsonNode.get("content").asText();

            // 发送邮件
            MailUtil.sendMsg(email,subject,content);
            // 测试...
            System.out.println("邮件发送成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
