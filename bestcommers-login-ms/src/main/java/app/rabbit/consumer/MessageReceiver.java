package app.rabbit.consumer;

import app.rabbit.config.MessagingConfig;
import app.shared.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    private UserDto userDto;

    @RabbitListener(queues = MessagingConfig.QUEUE)
    public void consumeMessageFromQueue(UserDto userDto) {
        logger.info("received user dto");
        setUserDto(userDto);
        logger.info("set user dto as a local object");
    }

    private void setUserDto(UserDto userDto){
        this.userDto = userDto;
    }

    public UserDto getUserDto() {
        return userDto;
    }

}
