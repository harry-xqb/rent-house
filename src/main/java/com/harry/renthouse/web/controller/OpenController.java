package com.harry.renthouse.web.controller;

import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.base.UserRoleEnum;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.property.LimitsProperty;
import com.harry.renthouse.service.auth.AuthenticationService;
import com.harry.renthouse.service.auth.SmsCodeService;
import com.harry.renthouse.service.auth.UserService;
import com.harry.renthouse.validate.code.ValidateCodeTypeEnum;
import com.harry.renthouse.web.dto.AuthenticationDTO;
import com.harry.renthouse.web.dto.LimitsDTO;
import com.harry.renthouse.web.dto.UserDTO;
import com.harry.renthouse.web.form.NoPassLoginForm;
import com.harry.renthouse.web.form.PhonePasswordLoginForm;
import com.harry.renthouse.web.form.SendSmsForm;
import com.harry.renthouse.web.form.UserPhoneRegisterForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * @author Harry Xu
 * @date 2020/5/27 11:12
 */
@RestController
@RequestMapping("open")
@Api(tags = "开放接口")
public class OpenController {

    @Resource
    private LimitsProperty limitsProperty;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private SmsCodeService smsCodeService;

    @Resource
    private AuthenticationService authenticationService;

    @Resource
    private UserService userService;

    @GetMapping("limits")
    @ApiOperation(value = "获取请求限制条件")
    public ApiResponse<LimitsDTO> getLimits(){
        return ApiResponse.ofSuccess(modelMapper.map(limitsProperty, LimitsDTO.class));
    }
    @PostMapping("registryByPhone")
    @ApiOperation("通过手机号注册用户")
    public ApiResponse<UserDTO> phoneRegistry(@Validated @RequestBody UserPhoneRegisterForm userPhoneRegisterForm){
        String smsCode = smsCodeService.getSmsCode(userPhoneRegisterForm.getPhoneNumber(), ValidateCodeTypeEnum.SIGN_UP.getValue());
        if(StringUtils.equals(smsCode, userPhoneRegisterForm.getVerifyCode())){
            return ApiResponse.ofSuccess(userService.registerUserByPhone(userPhoneRegisterForm, Collections.singletonList(UserRoleEnum.ADMIN)));
        }
        return ApiResponse.ofStatus(ApiResponseEnum.PHONE_SMS_CODE_ERROR);
    }

    @PostMapping("noPassLogin")
    @ApiOperation("免密登录")
    public ApiResponse<AuthenticationDTO> noPassLogin(@Validated @RequestBody NoPassLoginForm noPassLoginForm){
        String smsCode = smsCodeService.getSmsCode(noPassLoginForm.getPhoneNumber(), ValidateCodeTypeEnum.LOGIN.getValue());
        if(StringUtils.equals(smsCode, noPassLoginForm.getVerifyCode())){
            AuthenticationDTO authenticationDTO = authenticationService.noPassLogin(noPassLoginForm.getPhoneNumber());
            return ApiResponse.ofSuccess(authenticationDTO);
        }
        return ApiResponse.ofStatus(ApiResponseEnum.PHONE_SMS_CODE_ERROR);
    }

    @PostMapping("login")
    @ApiOperation("用户登录")
    public ApiResponse<AuthenticationDTO> login(@Validated @RequestBody PhonePasswordLoginForm form){
        AuthenticationDTO authenticationDTO = authenticationService.loginByPhone(form.getPhone(), form.getPassword());
        return ApiResponse.ofSuccess(authenticationDTO);
    }

    @PostMapping("sendSmsToPhone")
    @ApiOperation("发送短信")
    public ApiResponse sendSmsToPhone(@Validated @RequestBody SendSmsForm sendSmsForm){
        smsCodeService.sendSms(sendSmsForm.getPhoneNumber(), sendSmsForm.getOperationType());
        return ApiResponse.ofSuccess();
    }

    @GetMapping("nickName")
    @ApiOperation("校验用户名是否存在")
    public ApiResponse checkNickNameExist(@ApiParam(value = "昵称") @RequestParam String nickName){
        userService.findByNickName(nickName).ifPresent(user -> {
            throw new BusinessException(ApiResponseEnum.USER_NICK_NAME_ALREADY_EXIST);
        });
        return ApiResponse.ofSuccess();
    }

}
