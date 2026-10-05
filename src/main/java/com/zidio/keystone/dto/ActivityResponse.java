
package com.zidio.keystone.dto;


import java.time.LocalDateTime;


public class ActivityResponse {


    private String message;

    private LocalDateTime time;



    public ActivityResponse(
            String message,
            LocalDateTime time
    ){

        this.message = message;
        this.time = time;

    }



    public String getMessage(){

        return message;

    }


    public LocalDateTime getTime(){

        return time;

    }

}