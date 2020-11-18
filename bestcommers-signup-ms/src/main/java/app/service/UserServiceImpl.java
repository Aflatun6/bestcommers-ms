package app.service;

import app.data.UserRepository;
import app.data.model.UserEntity;
import app.rabbit.config.MessagingConfig;
import app.rabbit.model.UserDto;
import app.ui.model.UserResponse;
import app.ui.model.UserSignup;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder encoder;
    private final UserRepository repo;
    private final RabbitTemplate rabbitTemplate;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserServiceImpl(PasswordEncoder encoder, UserRepository repo, RabbitTemplate rabbitTemplate) {
        this.encoder = encoder;
        this.repo = repo;
        this.rabbitTemplate = rabbitTemplate;
    }


    @Override
    public UserResponse createUser(UserSignup userSignup) {

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(userSignup, UserEntity.class);

        userEntity.setMerchantId(UUID.randomUUID().toString());
        userEntity.setEncryptedPassword(encoder.encode(userSignup.getPassword()));
        UserEntity savedUser = repo.save(userEntity);

        return mapper.map(savedUser, UserResponse.class);
    }

    @Override
    public UserDto check(String email) {
        UserEntity userEntity = repo.findByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);
        return new ModelMapper().map(userEntity, UserDto.class);
    }

    @Override
    public void send(UserDto userDetails) {
        logger.info("sending user dto, email: {}", userDetails.getEmail());
        rabbitTemplate.convertAndSend(MessagingConfig.EXCHANGE, MessagingConfig.ROUTING_KEY, userDetails);
    }
}
