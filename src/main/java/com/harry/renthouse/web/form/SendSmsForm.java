package com.harry.renthouse.web.form;

import com.harry.renthouse.web.form.annotation.PhoneAnnotation;
import com.harry.renthouse.web.form.annotation.VerifyCodeTypeAnnotation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author Harry Xu
 * @date 2020/5/22 11:35
 */
@Data
@ApiModel("发送短信表单")
public class SendSmsForm {

    @NotNull(message = "手机号不能为空")
    @PhoneAnnotation()
    @ApiModelProperty(value = "手机号", example = "17879502601", required = true)
    private String phoneNumber;

    @NotNull(message = "业务类型不能为空")
    @ApiModelProperty(value = "业务类型, signUp: 注册", example = "signUp", allowableValues = "signUp", required = true)
    @VerifyCodeTypeAnnotation()
    private String operationType;

}
