package com.harry.renthouse.service.auth;

import com.harry.renthouse.web.dto.UserDTO;

/**
 * @author Harry Xu
 * @date 2020/5/18 18:39
 */
public interface UserService {

    /**
     * 通过用户id查找用户
     * @param id 用户id
     * @return
     */
    UserDTO findUserById(Long id);
}
