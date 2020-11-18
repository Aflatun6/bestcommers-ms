package app.rabbit.consumer;

import app.rabbit.config.MessagingConfig;
import app.rabbit.model.UserDto;
import app.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserService userService;

    public MessageReceiver(UserService userService) {
        this.userService = userService;
    }

    @RabbitListener(queues = MessagingConfig.QUEUE)
    public void consumeMessageFromQueue(String email) {
        logger.info("email is: {}", email);
        UserDto userDetails = userService.check(email);
        logger.info("got user dto: name: {}", userDetails.getMerchantName());
        userService.send(userDetails);
    }

}
