package app.service;


//import app.data.UserRepository;

import app.rabbit.config.MessagingConfig;
import app.rabbit.consumer.MessageReceiver;
import app.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final Environment env;
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private final RabbitTemplate rabbitTemplate;
    private final MessageReceiver receiver;

    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder, Environment env, RabbitTemplate rabbitTemplate, MessageReceiver receiver) {
        this.passwordEncoder = passwordEncoder;
        this.env = env;
        this.rabbitTemplate = rabbitTemplate;
        this.receiver = receiver;
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserDto userDto = receiver.getUserDto();
        if (userDto == null) throw new UsernameNotFoundException(email);
        return userDto;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("sending the email: {}", email);
        rabbitTemplate.convertAndSend(MessagingConfig.EXCHANGE, MessagingConfig.ROUTING_KEY, email);
        logger.info("getting the user dto");
        UserDto userDto = receiver.getUserDto();
        logger.info("user dto's email: {}", userDto.getEmail());
        return new User(userDto.getEmail(), userDto.getEncryptedPassword(), true, true, true, true, new ArrayList<>());
    }


}
