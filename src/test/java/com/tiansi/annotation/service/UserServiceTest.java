package com.tiansi.annotation.service;

import com.tiansi.annotation.domain.Users;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    public void encodeTest() {
        String before = "123456";
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String after = bCryptPasswordEncoder.encode(before);
        Users users = userService.getById(2);
        users.setPassword(after);
        userService.updateById(users);
        System.out.println(after);
    }
    @Test
    public void generateAccount(){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        Users gxh=new Users(null,"gxh",bCryptPasswordEncoder.encode("123456"),"ADMIN",0);
        Users wym=new Users(null,"wym",bCryptPasswordEncoder.encode("123456"),"ADMIN",0);
        userService.save(gxh);
        userService.save(wym);
    }
}
