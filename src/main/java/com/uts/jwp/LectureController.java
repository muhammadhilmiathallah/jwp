package com.uts.jwp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.uts.jwp.Domain.Lecture;

@RestController

public class LectureController {
    
    public static Map<String, Lecture> lectureMap = new HashMap<>();

    @GetMapping("/lecture")
    public List<Lecture> getCourses() {
        return lectureMap.values().stream().toList();
    }

    @PostMapping("/lecture")
    public ResponseEntity<String> addCourses(@RequestBody Lecture lecture) {
        lectureMap.put(lecture.getNip(), lecture);
        Lecture savedCourses = lectureMap.get(lecture.getNip());
        return new ResponseEntity<>("Lecture with Code: " + savedCourses.getNip() +
                "has been created", HttpStatus.OK);   
    }

    @GetMapping(value = "/lecture/{nip}")
	public ResponseEntity<Lecture> findStudent(@PathVariable("nip") String nip){
		final Lecture lecture = lectureMap.get(nip);
		return new ResponseEntity<>(lecture, HttpStatus.OK);
	}

    @PutMapping(value = "/lecture/{nip}")
	public ResponseEntity<String> updateLecture(@PathVariable("nip") String code, @RequestBody Lecture lecture){
		final Lecture lectureToBeUpdate = lectureMap.get(lecture.getNip());
		lectureToBeUpdate.setFullName(lecture.getFullName());
		lectureToBeUpdate.setEmail(lecture.getEmail());
		lectureToBeUpdate.setPhoneNumber(lecture.getPhoneNumber()); 

		lectureMap.put(lecture.getNip(), lectureToBeUpdate);
		return new ResponseEntity<String>("Lecture with Nip: " + lectureToBeUpdate.getNip() + " has been updated", HttpStatus.OK);
	}

    @DeleteMapping(value = "/lecture/{nip}")
	public ResponseEntity<Void> deleteStudent(@PathVariable("nip") String nip){
		lectureMap.remove(nip);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
}
    
