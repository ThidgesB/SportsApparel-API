package io.catalyte.training.sportsproducts.domains.user;

import static io.catalyte.training.sportsproducts.constants.Roles.CUSTOMER;

import io.catalyte.training.sportsproducts.auth.GoogleAuthService;
import io.catalyte.training.sportsproducts.exceptions.ResourceNotFound;
import io.catalyte.training.sportsproducts.exceptions.ServerError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implements user service interface
 */
@Service
public class UserServiceImpl implements UserService {

  private final Logger logger = LogManager.getLogger(UserController.class);
  private final UserRepository userRepository;
  private final GoogleAuthService googleAuthService;

  @Autowired
  public UserServiceImpl(UserRepository userRepository, GoogleAuthService googleAuthService) {
    this.userRepository = userRepository;
    this.googleAuthService = googleAuthService;
  }

  // METHODS

  /**
   * Updates user given valid credentials
   *
   * @param bearerToken String value in the Authorization property of the header
   * @param id          Id of the user to update
   * @param updatedUser User to update
   * @return User - Updated user
   */
  @Override
  public User updateUser(String bearerToken, Long id, User updatedUser) {

    // AUTHENTICATES USER - SAME EMAIL, SAME PERSON
    boolean isAuthenticated = googleAuthService.authenticateUser(bearerToken, updatedUser);

    if (!isAuthenticated) {
      logger.error("Email in the request body does not match email from JWT");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Email in the request body does not match email from JWT Token");
    }

    // UPDATES USER
    User existingUser;

    try {
      existingUser = userRepository.findById(id).orElse(null);
    } catch (DataAccessException dae) {
      logger.error(dae.getMessage());
      throw new ServerError(dae.getMessage());
    }

    if (existingUser == null) {
      logger.error("User with id: " + id + " does not exist");
      throw new ResourceNotFound("User with id: " + id + " does not exist");
    }

    // TEMPORARY LOGIC TO PREVENT USER FROM UPDATING THEIR ROLE
    updatedUser.setRole(existingUser.getRole());

    // GIVE THE USER ID IF NOT SPECIFIED IN BODY TO AVOID DUPLICATE USERS
    if (updatedUser.getId() == null) {
      updatedUser.setId(id);
    }

    try {
      logger.info("Saved user to");
      return userRepository.save(updatedUser);
    } catch (DataAccessException dae) {
      logger.error(dae.getMessage());
      throw new ServerError(dae.getMessage());
    }

  }

  /**
   * Creates user in the database, given email is not null and not taken
   *
   * @param user User to create
   * @return User
   */
  @Override
  public User createUser(User user) {

    String email = user.getEmail();

    // CHECK TO MAKE SURE EMAIL EXISTS ON INCOMING USER
    if (email == null) {
      logger.error("User must have an email field");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User must have an email field");
    }

    // CHECK TO MAKE SURE USER EMAIL IS NOT TAKEN
    User existingUser;

    try {
      existingUser = userRepository.findByEmail(user.getEmail());
    } catch (DataAccessException dae) {
      logger.error(dae.getMessage());
      throw new ServerError(dae.getMessage());
    }

    if (existingUser != null) {
      logger.error("Email is taken");
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is taken");
    }

    // SET DEFAULT ROLE TO CUSTOMER
    // NOT RUNNING CONDITIONAL DUE TO SOMEONE ASSIGNING THEMSELVES A ROLE
    // if (user.getRole() == null) {
    user.setRole(CUSTOMER);
    // }

    // SAVE USER
    try {
      logger.info("Saved user");
      return userRepository.save(user);
    } catch (DataAccessException dae) {
      logger.error(dae.getMessage());
      throw new ServerError(dae.getMessage());
    }

  }

  /**
   * Gets user by an email
   *
   * @param email Email of the user
   * @return The user
   */
  @Override
  public User getUserByEmail(String email) {

    User user;

    try {
      user = userRepository.findByEmail(email);
    } catch (DataAccessException dae) {
      logger.error(dae.getMessage());
      throw new ServerError(dae.getMessage());
    }

    if (user == null) {
      throw new ResourceNotFound("User with email " + email + " does not exist.");
    }

    return user;

  }

}
