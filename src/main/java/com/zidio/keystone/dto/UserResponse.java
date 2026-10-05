package com.zidio.keystone.dto;


public class UserResponse {


    private Long id;

    private String name;

    private String email;

    private String role;

    private String profilePic;



    public UserResponse(
            Long id,
            String name,
            String email,
            String role,
            String profilePic
    ) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.profilePic = profilePic;

    }



    public Long getId() {

        return id;

    }



    public String getName() {

        return name;

    }



    public String getEmail() {

        return email;

    }



    public String getRole() {

        return role;

    }



    public String getProfilePic() {

        return profilePic;

    }

}