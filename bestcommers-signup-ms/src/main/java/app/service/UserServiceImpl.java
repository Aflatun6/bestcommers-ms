package app.service;

import app.data.UserRepository;
import app.data.model.UserEntity;
import app.ui.model.UserResponse;
import app.ui.model.UserSignup;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder encoder;
    private final UserRepository repo;

    @Autowired
    public UserServiceImpl(PasswordEncoder encoder, UserRepository repo) {
        this.encoder = encoder;
        this.repo = repo;
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
}
