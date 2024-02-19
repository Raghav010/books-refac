package com.sismics.books.rest.resource;

import java.util.Date;

import org.codehaus.jettison.json.JSONException;

import com.sismics.books.core.constant.Constants;
import com.sismics.books.core.dao.jpa.dto.UserDto;
import com.sismics.books.core.model.jpa.User;
import com.sismics.rest.util.ValidationUtil;

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

    /*
     * email = email
     * password = username
     * localeId = localeId
     * themeId = id
     */
    public static UserDto validateUserDto(UserDto userDto) throws JSONException {

        String email = userDto.getEmail();
        if (email != null) {
            userDto.setEmail(ValidationUtil.validateLength(email, "email", Constants.MIN_EMAIL_LEN, Constants.MAX_EMAIL_LEN, Constants.EMAIL_NULLABLE));
        }

        String password = userDto.getUsername();
        if (password != null) {
            userDto.setUsername(ValidationUtil.validateLength(password, "password", Constants.MIN_PWD_LEN, Constants.MAX_PWD_LEN, Constants.PWD_NULLABLE));
        }

        String localeId = userDto.getLocaleId();
        if (localeId != null) {
            userDto.setLocaleId(ValidationUtil.validateLocale(localeId, "locale", Constants.LOCALE_ID_NULLABLE));
        }

        String themeId = userDto.getId();
        if (themeId != null) {
            userDto.setId(ValidationUtil.validateLength(themeId, "themeId", 0, 50, Constants.THEME_ID_NULLABLE));
        }

        return userDto;
    }
}
