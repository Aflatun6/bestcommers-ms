package app.service;

import app.ui.model.UserResponse;
import app.ui.model.UserSignup;

public interface UserService {

    UserResponse createUser(UserSignup userSignup);

}
