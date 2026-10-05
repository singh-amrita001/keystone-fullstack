package com.zidio.keystone.controller;


import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/test")
public class TestController {


    @GetMapping
    public String test(){

        return "JWT Authentication Working";

    }

}