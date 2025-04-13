package com.workintech.spring17challenge.controller;

import com.workintech.spring17challenge.entity.Course;
import com.workintech.spring17challenge.exceptions.ApiException;
import com.workintech.spring17challenge.model.HighCourseGpa;
import com.workintech.spring17challenge.model.LowCourseGpa;
import com.workintech.spring17challenge.model.MediumCourseGpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private List<Course> courses = new ArrayList<>();

    private final LowCourseGpa lowCourseGpa;
    private final MediumCourseGpa mediumCourseGpa;
    private final HighCourseGpa highCourseGpa;

    @Autowired
    public CourseController(LowCourseGpa lowCourseGpa, MediumCourseGpa mediumCourseGpa, HighCourseGpa highCourseGpa) {
        this.lowCourseGpa = lowCourseGpa;
        this.mediumCourseGpa = mediumCourseGpa;
        this.highCourseGpa = highCourseGpa;
    }

    @GetMapping
    @ResponseBody
    public List<Course> getCourses(){
        return courses;
    }

    @GetMapping("/{name}")
    @ResponseBody
    public Course getCourseByName(@PathVariable String name){
        return courses.stream()
                .filter(course -> course.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new ApiException("Course is not defined.", HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public CourseResponse addCourse(@RequestBody Course course){
        validateCourse(course);
        if(course == null || course.getName() == null || course.getName().isBlank()){
            throw new ApiException("Course name is cannot be null or blank.", HttpStatus.BAD_REQUEST);
        }

        courses.add(course);
        double totalGpa = calculateTotalGpa(course);
        return new CourseResponse(course, totalGpa);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public CourseResponse updateCourse(@PathVariable Integer id, @RequestBody Course course){
        validateCourse(course);

        Course existingCourse = courses.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ApiException("Course bulunamadı.", HttpStatus.NOT_FOUND));

        existingCourse.setName(course.getName());
        existingCourse.setCredit(course.getCredit());
        existingCourse.setGrade(course.getGrade());

        double totalGpa = calculateTotalGpa(existingCourse);
        return new CourseResponse(existingCourse, totalGpa);
    }

    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable Integer id){
        courses.removeIf(c -> c.getId().equals(id));
    }

    @DeleteMapping("/all")
    public void deleteAllCourses() {
        courses.clear();
    }

    private void validateCourse(Course course){
        if (course.getId() == null) {
            throw new ApiException("Kurs id boş geçilemez.", HttpStatus.BAD_REQUEST);
        }
        if (course.getName() == null || course.getName().trim().isEmpty()) {
            throw new ApiException("Kurs adı boş geçilemez.", HttpStatus.BAD_REQUEST);
        }
        if (course.getCredit() == null) {
            throw new ApiException("Kurs kredisi belirtilmelidir.", HttpStatus.BAD_REQUEST);
        }
        if (course.getCredit() < 1 || course.getCredit() > 4) {
            throw new ApiException("Kurs kredisi 1 ile 4 arasında olmalıdır.", HttpStatus.BAD_REQUEST);
        }
        if (course.getGrade() == null) {
            throw new ApiException("Kurs grade bilgisi boş geçilemez.", HttpStatus.BAD_REQUEST);
        }
        if (course.getGrade().getCoefficient() == null) {
            throw new ApiException("Kurs grade katsayısı boş geçilemez.", HttpStatus.BAD_REQUEST);
        }
        if (course.getGrade().getNote() == null || course.getGrade().getNote().trim().isEmpty()) {
            throw new ApiException("Kurs grade notu boş geçilemez.", HttpStatus.BAD_REQUEST);
        }
    }

    public static class CourseResponse {
        private Course course;
        private double totalGpa;

        public CourseResponse(Course course, double totalGpa) {
            this.course = course;
            this.totalGpa = totalGpa;
        }

        public Course getCourse() {
            return course;
        }

        public double getTotalGpa() {
            return totalGpa;
        }
    }

    private double calculateTotalGpa(Course course){
        int gpa;
        if(course.getCredit() <= 2) {
            gpa = lowCourseGpa.getGpa();
        } else if (course.getCredit() <= 5) {
            gpa = mediumCourseGpa.getGpa();
        } else if (course.getCredit() <= 10) {
            gpa = highCourseGpa.getGpa();
        } else {
            throw new ApiException("Invalid credit value", HttpStatus.BAD_REQUEST);
        }
        return course.getGrade().getCoefficient() * course.getCredit() * gpa;
    }
}