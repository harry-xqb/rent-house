package com.harry.renthouse.util;

import com.harry.renthouse.base.ApiResponseEnum;
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
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       /* if(principal.)
        User user = principal.map(obj -> (User) obj)
                .orElseThrow(() -> new BusinessException(ApiResponseEnum.NO_AUTHENTICATED_USER_ERROR));*/
       if(principal instanceof User){
           return (User) principal;
       }
       throw new BusinessException(ApiResponseEnum.NO_AUTHENTICATED_USER_ERROR);
    }

    /**
     * 获取用户id， 如果不存在则返回-1
     */
    public static Long getUserId(){
        return getUserInfo().getId();
    }
}

class Shop {

    void printSomething(Printer printer){
        printer.print();
    }
}

class Consumer{
    public static void main(String[] args) {
        Shop shop = new Shop();
        // 我想打印彩印
        Printer colorPrinter = new ColorPrinter();
        shop.printSomething(colorPrinter);

        // 我想打印黑白照
        Printer blackPrinter = new BlackPrinter();
        shop.printSomething(blackPrinter);
    }
}


class Printer{

    void print(){

    };
}

class ColorPrinter extends Printer{};
class BlackPrinter extends Printer{};