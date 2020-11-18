package app.service;

import app.rabbit.model.UserDto;
import app.ui.model.UserResponse;
import app.ui.model.UserSignup;
import org.springframework.amqp.core.Message;

public interface UserService {

    UserResponse createUser(UserSignup userSignup);

    UserDto check(String message);

    void send(UserDto userDetails);
}
