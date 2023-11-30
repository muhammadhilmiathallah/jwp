package com.uts.jwp;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.uts.jwp.Domain.Lecture;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class LectureController {

    public static Map<String, Lecture> lectureMap = new HashMap<>();

	private static List<Lecture> fetchLectures() {
        return lectureMap.values().stream().toList();
    }

    @GetMapping("/lectures")
    public String getLectures(Model model) {
        model.addAttribute("lectures", fetchLectures());
        return "index";
    }

    @GetMapping("/signup")
    public String showAddForm(Lecture lecture) {
        return "addLectures";
    }

    @PostMapping("/lectures")
    public String addLecture(@Valid Lecture lecture, BindingResult bindingResult, Model model) {
        String errorNIP = validateNIP(lecture.getNip());
        if (errorNIP != null) {
            ObjectError error = new ObjectError("globalError", errorNIP);
            bindingResult.addError(error);
        }

        String errorEmail = validateEmail(lecture.getEmail());
        if (errorEmail != null) {
            ObjectError error = new ObjectError("globalError", errorEmail);
            bindingResult.addError(error);
        }

        String errorPhoneNumber = validatePhoneNumber(lecture.getPhoneNumber());
        if (errorPhoneNumber != null) {
            ObjectError error = new ObjectError("globalError", errorPhoneNumber);
            bindingResult.addError(error);
        }

        String duplicateDataError = checkDuplicateData(lecture);
        if (duplicateDataError != null) {
            ObjectError error = new ObjectError("globalError", duplicateDataError);
            bindingResult.addError(error);
        }

        log.info("bindingResult {}", bindingResult);

        if (bindingResult.hasErrors()) {
            return "addLectures";
        }

        String nip = lecture.getNip();
        boolean exists = lectureMap.values().stream()
                .anyMatch(data -> nip.equals(data.getNip()));

        if (exists) {
            throw new IllegalArgumentException("Lecture with ID:" + nip + " is already exist");
        }

        lectureMap.put(nip, lecture);
        model.addAttribute("lectures", fetchLectures());
        return "index";
    }

    private String validateNIP(String nip) {
        if (!nip.startsWith("LCT") || !nip.substring(3).matches("\\d{10}")) {
            return "NIP must start with 'LCT' and be followed by 10 digits";
        }
        return null;
    }

    private String validateEmail(String email) {
        return null; // Implement your email validation logic
    }

    private String validatePhoneNumber(String phoneNumber) {
        return null; // Implement your phone number validation logic
    }

    private String checkDuplicateData(Lecture lecture) {
        boolean exists = lectureMap.values().stream()
                .anyMatch(data ->
                        lecture.getEmail().equals(data.getEmail()) ||
                                lecture.getNip().equals(data.getNip()) ||
                                lecture.getPhoneNumber().equals(data.getPhoneNumber())
                );

        if (exists) {
            return "Lecture with the same NIP, Email, or Phone Number already exists";
        }

        return null;
    }

    @GetMapping("/cancelAdd")
    public String cancelAddLecture() {
        return "redirect:/lectures";
    }

    @GetMapping(value = "/lectures/{nip}")
    public ResponseEntity<Lecture> findLecture(@PathVariable("nip") String nip) {
        final Lecture lecture = lectureMap.get(nip);
        return new ResponseEntity<>(lecture, HttpStatus.OK);
    }

    @PostMapping(value = "/lectures/{nip}")
    public String updateLecture(@PathVariable("nip") String nip,
                                 @Valid Lecture updatedLecture,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "editLectures";
        }

        final Lecture lectureToBeUpdated = lectureMap.get(nip);
        if (lectureToBeUpdated == null) {
            throw new IllegalArgumentException("Lecture with NIP:" + nip + " is not found");
        }

        if (!isUpdateValid(updatedLecture, lectureToBeUpdated)) {
            ObjectError error = new ObjectError("globalError", "Edited data is the same as existing data");
            result.addError(error);
            return "editLectures";
        }

        lectureToBeUpdated.setFullName(updatedLecture.getFullName());
        lectureToBeUpdated.setEmail(updatedLecture.getEmail());
        lectureToBeUpdated.setPhoneNumber(updatedLecture.getPhoneNumber());
        lectureMap.put(nip, lectureToBeUpdated);

        model.addAttribute("lectures", fetchLectures());
        return "redirect:/lectures";
    }

    private boolean isUpdateValid(Lecture updatedLecture, Lecture existingLecture) {
        return !updatedLecture.equals(existingLecture) &&
                !isDuplicateData(updatedLecture);
    }

    private boolean isDuplicateData(Lecture lecture) {
        return lectureMap.values().stream()
                .anyMatch(data ->
                        lecture.getEmail().equals(data.getEmail()) ||
                                lecture.getFullName().equals(data.getFullName()) ||
                                lecture.getPhoneNumber().equals(data.getPhoneNumber())
                );
    }

    @GetMapping("/edit/{nip}")
    public String showUpdateForm(@PathVariable("nip") String nip, Model model) {
        final Lecture lectureToBeUpdated = lectureMap.get(nip);
        if (lectureToBeUpdated == null) {
            throw new IllegalArgumentException("Lecture with NIP:" + nip + " is not found");
        }
        model.addAttribute("lecture", lectureToBeUpdated);
        return "editLectures";
    }

    @GetMapping("/cancelEdit/{nip}")
    public String cancelEditLecture(@PathVariable("nip") String nip) {
        return "redirect:/lectures";
    }

    @GetMapping(value = "/lectures/{nip}/delete")
    public String deleteLecture(@PathVariable("nip") String nip) {
        lectureMap.remove(nip);
        return "redirect:/lectures";
    }
}