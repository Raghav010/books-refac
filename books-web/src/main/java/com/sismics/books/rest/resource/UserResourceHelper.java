package com.sismics.books.rest.resource;

import java.util.Date;

import com.sismics.books.core.constant.Constants;
import com.sismics.books.core.dao.jpa.dto.UserDto;
import com.sismics.books.core.model.jpa.User;

public class UserResourceHelper {
    
    public static User createUser(UserDto userDto) {
        User user = new User();
        user.setRoleId(Constants.DEFAULT_USER_ROLE);
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getLocaleId()); // this is actually the pwd
        user.setEmail(userDto.getEmail());
        user.setCreateDate(new Date());
        user.setLocaleId(Constants.DEFAULT_LOCALE_ID);
        return user;
    }
}
