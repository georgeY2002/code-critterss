package org.codecritters.code_critters.persistence.repository;

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

import org.codecritters.code_critters.persistence.entities.User;
import org.codecritters.code_critters.web.enums.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

    boolean existsByUsernameOrEmail(String username, String email);

    User findByUsernameOrEmail(String username, String email);

    User findBySecret(String secret);

    User findByCookie(String cookie);

    User findByCookieAndLastUsedAfter(String cookie, Date date);

    User findByUsernameAndEmail(String username, String email);

    List<User> findAllByRole(Role role);

    boolean existsByCookie(String cookie);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findById(String id);
}
