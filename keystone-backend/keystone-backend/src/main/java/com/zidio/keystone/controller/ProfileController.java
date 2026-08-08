package com.zidio.keystone.controller;


import java.io.IOException;
import java.nio.file.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.zidio.keystone.entity.User;
import com.zidio.keystone.repository.UserRepository;


@RestController
@RequestMapping("/api/profile")
@CrossOrigin("*")
public class ProfileController {


    private final UserRepository userRepository;


    public ProfileController(UserRepository userRepository) {

        this.userRepository = userRepository;

    }



    @PostMapping("/upload/{email}")
    public ResponseEntity<?> uploadProfilePic(

            @PathVariable String email,

            @RequestParam("file") MultipartFile file

    ) throws IOException {



        String fileName =
                System.currentTimeMillis()
                + "_"
                + file.getOriginalFilename();



        Path uploadPath =
                Paths.get("uploads");



        if(!Files.exists(uploadPath)){

            Files.createDirectories(uploadPath);

        }


        Path path = uploadPath.resolve(fileName);

        Files.write(
                path,
                file.getBytes()
        );

        System.out.println(
                "Saved file at: "
                + path.toAbsolutePath()
        );



        User user =
                userRepository.findByEmail(email)
                .orElseThrow();



        user.setProfilePic(
                "/uploads/" + fileName
        );



        userRepository.save(user);



        return ResponseEntity.ok(user);

    }


}