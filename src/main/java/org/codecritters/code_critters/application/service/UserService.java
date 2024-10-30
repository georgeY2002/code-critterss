package org.codecritters.code_critters.application.service;

/*-
 * #%L
 * Code Critters
 * %%
 * Copyright (C) 2019 Michael Gruber
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.codecritters.code_critters.application.exception.AlreadyExistsException;
import org.codecritters.code_critters.application.exception.IllegalActionException;
import org.codecritters.code_critters.application.exception.NotFoundException;
import org.codecritters.code_critters.persistence.entities.Course;
import org.codecritters.code_critters.persistence.entities.User;
import org.codecritters.code_critters.persistence.repository.CourseRepository;
import org.codecritters.code_critters.persistence.repository.ResultRepository;
import org.codecritters.code_critters.persistence.repository.UserRepository;
import org.codecritters.code_critters.web.dto.CourseDTO;
import org.codecritters.code_critters.web.dto.UserDTO;
import org.codecritters.code_critters.web.enums.Language;
import org.codecritters.code_critters.web.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ResultRepository resultRepository;
    private final MailService mailService;
    private final PasswordService passwordService;
    private Map<String, LocalDateTime> loginTimeMap = new HashMap<>();
    private Map<String, Long> learningHoursMap = new HashMap<>(); // Store learning hours per user

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvw0123456789";

    @Value("${spring.session.timeout}")
    private int timeout;

    @Autowired
    public UserService(UserRepository userRepository, CourseRepository courseRepository, MailService mailService, PasswordService passwordService, ResultRepository resultRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.mailService = mailService;
        this.passwordService = passwordService;
        this.resultRepository = resultRepository;
    }

    public void registerUser(UserDTO dto, String url) {
        if(dto.getUsername() == null || dto.getEmail() == null || dto.getPassword() == null) {
            throw new IllegalActionException("These fields cannot be empty", "fill_fields");
        }

        if (dto.getUsername().trim().equals("") || dto.getEmail().trim().equals("") || dto.getPassword().trim().equals("")) {
            throw new IllegalActionException("These fields cannot be empty", "fill_fields");
        }

        if ((dto.getUsername().length() > 50) || (dto.getEmail().length() > 50) || (dto.getPassword().length() > 50)) {
            throw new IllegalActionException("The input has to be less than 50 characters!", "long_data");
        }

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new AlreadyExistsException("User with this username already exists!", "username_exists");
        }

        if(userRepository.existsByEmail(dto.getEmail())) {
            throw new AlreadyExistsException("User with this email already exists!", "email_exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        if (dto.getLanguage() == null) {
            user.setLanguage(Language.en);
        }
        user.setLanguage(dto.getLanguage());
        user = passwordService.hashPassword(dto.getPassword(), user);
        user.setRole(Role.user);
        user.setActive(false);
        user.setSecret(generateSecret());

        Map<String, String> mailTemplateData = new HashMap<>();

        String link = url + "/users/activate/" + user.getSecret();

        mailTemplateData.put("receiver", user.getEmail());
        mailTemplateData.put("subject", "welcome");
        mailTemplateData.put("user", user.getUsername());
        mailTemplateData.put("secret", link);
        mailTemplateData.put("baseURL", url);
        mailService.sendMessageFromTemplate("register", mailTemplateData, user.getLanguage());

        userRepository.save(user);
    }

    public UserDTO loginUser(UserDTO dto, String cookie) {
        User user = userRepository.findByUsernameOrEmail(dto.getUsername(), dto.getEmail());
    
        if (user != null) {
            if ((dto.getPassword() != null) && (passwordService.verifyPassword(dto.getPassword(), user.getPassword(), user.getSalt()))) {
                user.setCookie(cookie);
                userRepository.save(user);
    
                // Track login time
                loginTimeMap.put(user.getId(), LocalDateTime.now());
    
                // Debugging log for login time
                System.out.println("User logged in at: " + loginTimeMap.get(user.getId()));
    
                return userToDTO(user);
            } else {
                throw new NotFoundException("Username or Password incorrect", "invalid_username_or_password");
            }
        }
        throw new NotFoundException("Username or Password incorrect", "invalid_username_or_password");
    }
    

    public void forgotPassword(UserDTO dto, String url) {
        User user = userRepository.findByUsernameOrEmail(dto.getUsername(), dto.getEmail());
        if (user != null) {
            user.setSecret(generateSecret());
            user.setResetPassword(true);
            userRepository.save(user);

            Map<String, String> mailTemplateData = new HashMap<>();

            String link = url + "/resetPassword?secret=" + user.getSecret();

            mailTemplateData.put("receiver", user.getEmail());
            mailTemplateData.put("subject", "reset_pw");
            mailTemplateData.put("user", user.getUsername());
            mailTemplateData.put("secret", link);
            mailTemplateData.put("baseURL", url);
            mailService.sendMessageFromTemplate("forgotPassword", mailTemplateData, user.getLanguage());
        } else {
            throw new NotFoundException("Username or Password incorrect", "invalid_username_or_password");
        }
    }

    public void resetPassword(String secret, UserDTO dto) {
        User user = userRepository.findBySecret(secret);
        if (user != null && user.getResetPassword()) {
            user.setSecret(null);
            user.setResetPassword(false);

            user = passwordService.hashPassword(dto.getPassword(), user);
            userRepository.save(user);
        } else {
            throw new NotFoundException("Incorrect Secret", "incorrect_secret");
        }
    }

    public boolean activateUser(String secret) {
        User user = userRepository.findBySecret(secret);
        if (user != null) {
            user.setSecret(null);
            user.setActive(true);

            return userRepository.save(user) != null;
        }
        return false;
    }

    private UserDTO userToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());  // Make sure to set the id here
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setActive(user.getActive());
        dto.setLanguage(user.getLanguage());
        dto.setRole(user.getRole());
        dto.setPoints(user.getPoints());  // Set points if they are required
    
        return dto;
    }

    private String generateSecret() {
        int count = 42;
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public UserDTO getUserByCookie(String cookie) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -timeout);
        Date date = cal.getTime();
        return getUserByCookieAndDate(cookie, date);
    }

    public UserDTO getUserByCookieAndDate(String cookie, Date date) {
        User user = userRepository.findByCookieAndLastUsedAfter(cookie, date);
        if (user != null) {
            userRepository.save(user);
            return userToDTO(user);
        }
        return null;
    }

    public void logoutUser(String cookie) {
        User user = userRepository.findByCookie(cookie);
        if (user != null) {
            user.setCookie(null);
            userRepository.save(user);
    
            // Calculate session duration and update learning hours
            LocalDateTime loginTime = loginTimeMap.get(user.getId());
            if (loginTime != null) {
                LocalDateTime logoutTime = LocalDateTime.now();
                
                // Calculate session duration in seconds
                long sessionDurationInSeconds = Duration.between(loginTime, logoutTime).getSeconds();
    
                // Convert seconds to minutes, accounting for small sessions
                long sessionDurationInMinutes = (sessionDurationInSeconds / 60) + ((sessionDurationInSeconds % 60 > 0) ? 1 : 0);
    
                // Update total learning hours for the user
                long totalLearningHours = learningHoursMap.getOrDefault(user.getId(), 0L) + sessionDurationInMinutes;
                learningHoursMap.put(user.getId(), totalLearningHours);
    
                // Debugging logs for logout, session duration, and learning hours
                System.out.println("User logged out at: " + logoutTime);
                System.out.println("Session duration: " + sessionDurationInMinutes + " minutes");
                System.out.println("Total learning hours: " + totalLearningHours);
    
                // Remove login time after logging out
                loginTimeMap.remove(user.getId());
            }
        }
    }
    
    
    

    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found", "user_not_found"));
        
        return userToDTO(user);
    }

    public void deleteUser(String cookie) {
        User user = userRepository.findByCookie(cookie);

        List<User> users = userRepository.findAllByRole(Role.admin);
        if (users.size() == 1 && users.contains(user)) {
            throw new IllegalActionException("Cannot delete last remaining admin", "delete_last_admin");
        }
        if (user != null) {
            resultRepository.deleteAllByUser(user);
            userRepository.delete(user);
        }
    }

    public void changeUser(UserDTO dto, String cookie, String url) {
        // Use the injected userRepository instance to call findByCookie()
        User user = userRepository.findByCookie(cookie); 
    
        if (user == null) {
            throw new NotFoundException("User not found", "user_not_found");
        }
    
        if(dto.getUsername() == null || dto.getEmail() == null || dto.getUsername().trim().equals("") || dto.getEmail().trim().equals("")) {
            throw new IllegalActionException("These fields cannot be empty", "fill_fields");
        }
    
        if ((dto.getUsername().length() > 50) || (dto.getEmail().length() > 50)) {
            throw new IllegalActionException("The input has to be less than 50 characters!", "long_data");
        }
    
        if (dto.getPassword() != null) {
            if (dto.getPassword().length() > 50) {
                throw new IllegalActionException("The input has to be less than 50 characters!", "long_data");
            }
            if (dto.getPassword().trim().equals("") || dto.getOldPassword() == null || dto.getOldPassword().trim().equals("")) {
                throw new IllegalActionException("These fields cannot be empty", "fill_fields");
            }
    
            if (!passwordService.verifyPassword(dto.getOldPassword(), user.getPassword(), user.getSalt())) {
                throw new NotFoundException("Password incorrect", "invalid_password");
            }
    
            // If old password matches, update the password
            user = passwordService.hashPassword(dto.getPassword(), user);
        }
    
        if (!dto.getUsername().equals(user.getUsername())) {
            if (!userRepository.existsByUsername(dto.getUsername())) {
                user.setUsername(dto.getUsername());
            } else {
                throw new AlreadyExistsException("User with this username already exists!", "username_exists");
            }
        }
    
        if (!dto.getEmail().equals(user.getEmail())) {
            if (!userRepository.existsByEmail(dto.getEmail())) {
                user.setEmail(dto.getEmail());
                user.setActive(false);
                user.setSecret(generateSecret());
    
                Map<String, String> mailTemplateData = new HashMap<>();
    
                String link = url + "/users/activate/" + user.getSecret();
    
                mailTemplateData.put("receiver", user.getEmail());
                mailTemplateData.put("subject", "Confirm Email");
                mailTemplateData.put("user", user.getUsername());
                mailTemplateData.put("secret", link);
                mailTemplateData.put("baseURL", url);
                mailService.sendMessageFromTemplate("confirmEmail", mailTemplateData, user.getLanguage());
            } else {
                throw new AlreadyExistsException("User with this email already exists!", "email_exists");
            }
        }
    
        // Save the updated user
        userRepository.save(user);
    }
    
    public void enrollInCourse(String userId, Long courseId) {
        // Fetch the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found", "user_not_found"));
    
        // Fetch the course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found", "course_not_found"));
    
        // Check if the user is already enrolled in the course
        boolean alreadyEnrolled = user.getCourses().stream()
                .anyMatch(enrolledCourse -> enrolledCourse.getId().equals(courseId));
    
        if (alreadyEnrolled) {
            throw new IllegalArgumentException("User is already enrolled in this course.");
        }
    
        // Add the course to the user's list of enrolled courses
        user.getCourses().add(course);
    
        // Save the updated user entity
        userRepository.save(user);
    }
    
    public List<CourseDTO> getCoursesEnrolled(String userId) {
        System.out.println("Fetching courses for userId: " + userId); // Add this line for debugging
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found", "user_not_found"));
        
        Set<Course> courses = user.getCourses();
        return courses.stream()
                      .map(this::convertToDTO)
                      .collect(Collectors.toList());
    }
    

    private CourseDTO convertToDTO(Course course) {
        return new CourseDTO(course.getId(), course.getName(), course.getRating(), course.getEstimatedHours(), course.getDescription());
    }
    public void updateUserPoints(String userId, int newPoints) {
        // Fetch the user from the database
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found", "user_not_found"));
    
        // Update the user's points
        int currentPoints = user.getPoints();  // Assuming points are stored as an integer
        user.setPoints(currentPoints + newPoints);
    
        // Save the updated user back to the database
        userRepository.save(user);
    }

    // API to get the total learning hours
    public long getTotalLearningHours(String userId) {
        return learningHoursMap.getOrDefault(userId, 0L);
    }
    public List<UserDTO> getAllUsers() {
    Iterable<User> usersIterable = userRepository.findAll(); // Fetch all users as Iterable
    List<User> users = StreamSupport.stream(usersIterable.spliterator(), false) // Convert Iterable to List
                                    .collect(Collectors.toList());
    
    return users.stream().map(this::userToDTO).collect(Collectors.toList()); // Convert to UserDTO
}
}
