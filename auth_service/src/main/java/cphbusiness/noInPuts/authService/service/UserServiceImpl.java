package cphbusiness.noInPuts.authService.service;

import cphbusiness.noInPuts.authService.dto.UserDTO;
import cphbusiness.noInPuts.authService.exception.UserAlreadyExistsException;
import cphbusiness.noInPuts.authService.exception.UserDoesNotExistException;
import cphbusiness.noInPuts.authService.exception.WeakPasswordException;
import cphbusiness.noInPuts.authService.exception.WrongCredentialsException;
import cphbusiness.noInPuts.authService.model.User;
import cphbusiness.noInPuts.authService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // TODO: Hashing password
    public UserDTO createUser(UserDTO userDTO) throws UserAlreadyExistsException, WeakPasswordException {

        // Check if user is already registered
        Optional<User> checkIfUserExist = userRepository.findByUsername(userDTO.getUsername());
        if (checkIfUserExist.isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        // Check if password is strong enough (1 Capital letter, 1 number, 1 special character)
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?!.*\\s).*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userDTO.getPassword());
        if (!matcher.matches()) {
            throw new WeakPasswordException("Password is not strong enough");
        }


        User user = userRepository.save(new User(userDTO.getUsername(), userDTO.getPassword()));

        return new UserDTO(user.getId(), user.getUsername());
    }

    public UserDTO login(UserDTO userDTO) throws WrongCredentialsException, UserDoesNotExistException {
        Optional<User> user = userRepository.findByUsername(userDTO.getUsername());
        if (user.isPresent()) {
            if (user.get().getPassword().equals(userDTO.getPassword())) {
                userDTO.setPassword(null);
                userDTO.setId(user.get().getId());
            } else {
                throw new WrongCredentialsException("Wrong password.");
            }
        } else {
            throw new UserDoesNotExistException("User not found.");
        }

        return userDTO;
    }
}
