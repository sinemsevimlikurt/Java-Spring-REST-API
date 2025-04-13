package com.workintech.spring17challenge.model;


import org.springframework.stereotype.Component;

@Component
public class LowCourseGpa implements CourseGpa{

    @Override
    public int getGpa() {
        return 3;
    }
}
