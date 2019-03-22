package com.tiansi.annotation.domain.body;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tiansi.annotation.domain.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestBody {
    private String id;
    private String username;
    private String password;
    private String role;

    public UserRequestBody(Users users) {
        this.setId(users.getId().toString());
        this.setUsername(users.getUsername());
//        this.setPassword(users.getPassword());
        this.setRole(users.getRole());
    }

    public static List<UserRequestBody> fromUsers(List<Users> users) {
        List<UserRequestBody> userRequestBodies = new ArrayList<>();
        users.forEach(user -> userRequestBodies.add(new UserRequestBody(user)));
        return userRequestBodies;
    }

    public Users toUsers() {
        Users users = new Users();
        Long id = this.getId() == null ? null : Long.valueOf(this.getId());
        users.setId(id);
        users.setUsername(this.getUsername());
        users.setPassword(this.getPassword());
        users.setRole(this.getRole());
        return users;
    }

    public static List<Users> toUsersList(List<UserRequestBody> userRequestBodies) {
        List<Users> usersList = new ArrayList<>();
        userRequestBodies.forEach(userRequestBody -> usersList.add(userRequestBody.toUsers()));
        return usersList;
    }
}
