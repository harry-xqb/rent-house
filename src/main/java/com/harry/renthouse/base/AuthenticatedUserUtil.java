package com.harry.renthouse.base;

import com.harry.renthouse.entity.User;
import com.harry.renthouse.exception.BusinessException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * 认证用户工具类
 * @author Harry Xu
 * @date 2020/5/9 15:29
 */
public class AuthenticatedUserUtil {

    /**
     * 获取当前登录的用户
     * @return
     */
    public static User getUserInfo(){
        Optional<Object> principal = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        User user = principal.map(obj -> (User) obj)
                .orElseThrow(() -> new BusinessException(ApiResponseEnum.NO_AUTHENTICATED_USER_ERROR));
        return user;
    }

    /**
     * 获取用户id， 如果不存在则返回-1
     */
    public static Long getUserId(){
        return getUserInfo().getId();
    }
}
