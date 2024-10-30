package org.codecritters.code_critters.application.service;

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

import org.codecritters.code_critters.persistence.entities.Course;
import org.codecritters.code_critters.persistence.repository.CourseRepository;
import org.codecritters.code_critters.web.dto.CourseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public CourseDTO saveCourse(CourseDTO courseDTO) {
        Course course = new Course();
        course.setName(courseDTO.getName());
        course.setRating(courseDTO.getRating());
        course.setEstimatedHours(courseDTO.getEstimatedHours());
        course.setDescription(courseDTO.getDescription());

        Course savedCourse = courseRepository.save(course);

        return convertToDTO(savedCourse);
    }

    public List<CourseDTO> getAllCourses() {
        // Call findAll on the courseRepository instance, not statically
        List<Course> courses = (List<Course>) courseRepository.findAll();
        
        // Convert the courses to DTOs using a stream and collect them into a list
        return courses.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
        return convertToDTO(course);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    private CourseDTO convertToDTO(Course course) {
        return new CourseDTO(
                course.getId(),
                course.getName(),
                course.getRating(),
                course.getEstimatedHours(),
                course.getDescription()
        );
    }
}
