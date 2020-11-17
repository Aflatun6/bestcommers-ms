package app.service;


import app.data.UserRepository;
import app.data.model.UserEntity;
import app.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, Environment env) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);
        return new ModelMapper().map(userEntity, UserDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email); // HERE WE WILL NO LONGER ASK A REPOSITORY FOR INFO, INSTEAD WE WILL PRODUCE A MESSAGE TO THE RABBITMQ BROKER AND THE SIGNUP SERVICE WILL CONSUME THE MESSAGE AND GIVE US INFO.
        if (userEntity == null) throw new UsernameNotFoundException(email);
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, true, true, true, new ArrayList<>());
    }


}
