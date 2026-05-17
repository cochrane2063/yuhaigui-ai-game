package com.yupi.yuhaigui.database.mapper;

import com.yupi.yuhaigui.database.model.User;

public interface UserMapper {
    User selectByUsername(String username);
    int insert(User user);
}
