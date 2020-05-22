package com.harry.renthouse.validate.code;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author Harry Xu
 * @date 2020/5/22 13:39
 */
@Data
public class ValidateCode implements Serializable {

    private String code;

    private Date expireTime;

    private Date reSendTime;

    public ValidateCode(String code, int expireIn, int reSendInterval) {
        this.code = code;
        this.expireTime = Date.from( LocalDateTime.now().plusSeconds(expireIn).atZone( ZoneId.systemDefault()).toInstant());
        this.reSendTime = Date.from( LocalDateTime.now().plusSeconds(reSendInterval).atZone( ZoneId.systemDefault()).toInstant());
    }

    public ValidateCode(String code, Date expireTime, Date reSendTime){
        this.code = code;
        this.expireTime = expireTime;
        this.reSendTime = reSendTime;
    }

    public boolean isExpired(){
        LocalDateTime ldt = this.expireTime.toInstant()
                .atZone( ZoneId.systemDefault() )
                .toLocalDateTime();
        return LocalDateTime.now().isAfter(ldt);
    }

    public boolean canReSend(){
        LocalDateTime ldt = this.expireTime.toInstant()
                .atZone( ZoneId.systemDefault() )
                .toLocalDateTime();
        return LocalDateTime.now().isAfter(ldt);
    }
}