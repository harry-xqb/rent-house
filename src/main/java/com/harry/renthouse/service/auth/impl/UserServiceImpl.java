package com.harry.renthouse.service.auth.impl;

import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.entity.User;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.repository.UserRepository;
import com.harry.renthouse.service.auth.UserService;
import com.harry.renthouse.web.dto.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Harry Xu
 * @date 2020/5/18 18:40
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDTO findUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new BusinessException(ApiResponseEnum.USER_NOT_FOUND));
        return modelMapper.map(user, UserDTO.class);
    }
}
