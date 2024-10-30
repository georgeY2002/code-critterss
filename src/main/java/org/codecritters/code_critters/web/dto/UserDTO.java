package org.codecritters.code_critters.web.dto;

/*-
 * #%L
 * Code Critters
 * %%
 * Copyright (C) 2019 - 2024 Michael Gruber
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

import org.codecritters.code_critters.web.enums.Language;
import org.codecritters.code_critters.web.enums.Role;

public class UserDTO {

    private String id;  // Changed from Long to String to match UUID format
    private String username;
    private String email;
    private String password;
    private String oldPassword;
    private String cookie;
    private boolean active;
    private int points = 0;  // Points field, default value 0
    private Language language;
    private Role role;
    private long learningHours = 0;  // New field to store learning hours

    // Constructor including the learningHours parameter
    public UserDTO(String id, String username, String email, String password, String oldPassword, String cookie, boolean active, int points, Language language, Role role, long learningHours) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.oldPassword = oldPassword;
        this.cookie = cookie;
        this.active = active;
        this.points = points;
        this.language = language;
        this.role = role;
        this.learningHours = learningHours;
    }

    /** The parameters needed for changing user information. */
    public UserDTO(String username, String email, String oldPassword, String password, Language language) {
        this.username = username;
        this.email = email;
        this.oldPassword = oldPassword;
        this.password = password;
        this.language = language;
        this.points = 0;  // Default points value
    }

    /** The parameters needed for the user registration. */
    public UserDTO(String username, String email, String password, Language language) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.language = language;
        this.points = 0;  // Default points value
    }

    /** The parameters needed for the user login. */
    public UserDTO(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.points = 0;  // Default points value
    }

    /** Used for password resets. */
    public UserDTO(String password) {
        this.password = password;
        this.points = 0;  // Default points value
    }

    /** Default constructor */
    public UserDTO() {
        this.points = 0;  // Default points value
    }

    // Getter and Setter for id (changed to String type)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and Setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and Setter for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter and Setter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter and Setter for oldPassword
    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    // Getter and Setter for cookie
    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    // Getter and Setter for active
    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Getter and Setter for points
    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    // Getter and Setter for language
    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    // Getter and Setter for role
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // Getter and Setter for learningHours
    public long getLearningHours() {
        return learningHours;
    }

    public void setLearningHours(long learningHours) {
        this.learningHours = learningHours;
    }
}
