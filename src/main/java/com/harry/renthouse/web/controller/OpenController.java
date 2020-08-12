package com.harry.renthouse.web.controller;

import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.base.UserRoleEnum;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.property.LimitsProperty;
import com.harry.renthouse.service.auth.AuthenticationService;
import com.harry.renthouse.service.auth.SmsCodeService;
import com.harry.renthouse.service.auth.UserService;
import com.harry.renthouse.util.RedisUtil;
import com.harry.renthouse.util.VerifyImageUtil;
import com.harry.renthouse.validate.code.ValidateCodeTypeEnum;
import com.harry.renthouse.web.dto.*;
import com.harry.renthouse.web.form.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    @Resource
    private RedisUtil redisUtil;


    @GetMapping("limits")
    @ApiOperation(value = "获取请求限制条件")
    public ApiResponse<LimitsDTO> getLimits(){
        return ApiResponse.ofSuccess(modelMapper.map(limitsProperty, LimitsDTO.class));
    }
    @PostMapping("registryByPhone")
    @ApiOperation("通过手机号注册用户")
    public ApiResponse<AuthenticationDTO> phoneRegistry(@Validated @RequestBody UserPhoneRegisterForm userPhoneRegisterForm){
        String smsCode = smsCodeService.getSmsCode(userPhoneRegisterForm.getPhoneNumber(), ValidateCodeTypeEnum.SIGN_UP.getValue());
        if(StringUtils.equals(smsCode, userPhoneRegisterForm.getVerifyCode())){
            UserDTO userDTO = userService.registerUserByPhone(userPhoneRegisterForm, Collections.singletonList(UserRoleEnum.ADMIN));
            AuthenticationDTO authenticationDTO = authenticationService.noPassLogin(userPhoneRegisterForm.getPhoneNumber());
            // return ApiResponse.ofSuccess());
            return ApiResponse.ofSuccess(authenticationDTO);
        }
        return ApiResponse.ofStatus(ApiResponseEnum.PHONE_SMS_CODE_ERROR);
    }

    @PostMapping("noPassLogin")
    @ApiOperation("免密登录")
    public ApiResponse<AuthenticationDTO> noPassLogin(@Validated @RequestBody NoPassLoginForm noPassLoginForm){
        String smsCode = smsCodeService.getSmsCode(noPassLoginForm.getPhoneNumber(), ValidateCodeTypeEnum.LOGIN.getValue());
        if(StringUtils.equals(smsCode, noPassLoginForm.getVerifyCode())){
            AuthenticationDTO authenticationDTO = authenticationService.noPassLogin(noPassLoginForm.getPhoneNumber());
            smsCodeService.deleteSmsCode(noPassLoginForm.getPhoneNumber(), ValidateCodeTypeEnum.LOGIN.getValue());
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
        // 校验图片验证码
        String verifyCode = sendSmsForm.getVerifyCode();
        if(org.springframework.util.StringUtils.isEmpty(verifyCode)){
            throw new BusinessException(ApiResponseEnum.IMAGE_VERIFY_CODE_NOT_FOUND);
        }
        String verifyKey = VerifyImageUtil.VERIFY_OPERATE_PREFIX + verifyCode;
        if(redisUtil.get(verifyKey) == null){
            throw new BusinessException(ApiResponseEnum.IMAGE_VERIFY_CODE_ERROR);
        }
        redisUtil.del(verifyKey);
        smsCodeService.sendSms(sendSmsForm.getPhoneNumber(), sendSmsForm.getOperationType());
        return ApiResponse.ofSuccess();
    }

    @GetMapping("nickName")
    @ApiOperation("校验用户名是否存在")
    public ApiResponse<OpenApiUerCheckDTO> checkNickNameExist(@ApiParam(value = "昵称") @RequestParam String nickName){
        if(userService.findByNickName(nickName).isPresent()){
            return ApiResponse.ofSuccess(new OpenApiUerCheckDTO(true, ApiResponseEnum.USER_ALREADY_EXIST.getMessage()));
        }
        return ApiResponse.ofSuccess(new OpenApiUerCheckDTO(false, ApiResponseEnum.USER_NOT_FOUND.getMessage()));
    }

    @GetMapping("phone")
    @ApiModelProperty("校验手机号是否存在")
    public ApiResponse checkPhoneExist(@ApiParam(value = "手机号") @RequestParam  String phone){
        phoneChecker(phone);
        if(userService.findByPhoneNumber(phone).isPresent()){
            return ApiResponse.ofSuccess(new OpenApiUerCheckDTO(true, ApiResponseEnum.USER_ALREADY_EXIST.getMessage()));
        }
        return ApiResponse.ofSuccess(new OpenApiUerCheckDTO(false, ApiResponseEnum.USER_NOT_FOUND.getMessage()));
    }

    @GetMapping("verifyImage")
    @ApiModelProperty("获取图片验证码")
    public ApiResponse<VerifyImageDTO> getVerifyImage(@ApiParam(value = "手机号") @RequestParam String phone){
        phoneChecker(phone);
        VerifyImageDTO verifyImageDTO = VerifyImageUtil.create(phone);
        return ApiResponse.ofSuccess(verifyImageDTO);
    }

    @GetMapping("checkImageCode")
    @ApiModelProperty("校验图片验证码")
    public ApiResponse<VerifyImageCheckDTO> checkImageCode(
            @ApiParam(value = "手机号") @RequestParam String phone,
            @ApiParam(value = "x轴的值") @RequestParam int x
            ){
        phoneChecker(phone);
        VerifyImageCheckDTO checkResult = VerifyImageUtil.check(phone, x);
        return ApiResponse.ofSuccess(checkResult);
    }

    @PostMapping("getResetPasswordToken")
    @ApiModelProperty("获取重置密码的令牌")
    public ApiResponse checkImageCode(@Validated @RequestBody NoPassLoginForm form){
        String smsCode = smsCodeService.getSmsCode(form.getPhoneNumber(), ValidateCodeTypeEnum.RESET_PASSWORD.getValue());
        if(!StringUtils.equals(smsCode, form.getVerifyCode())){
            throw new BusinessException(ApiResponseEnum.PHONE_SMS_CODE_ERROR);
        }
        smsCodeService.deleteSmsCode(form.getPhoneNumber(), ValidateCodeTypeEnum.RESET_PASSWORD.getValue());
        String token = userService.generateResetPasswordToken(form.getPhoneNumber());
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        return ApiResponse.ofSuccess(map);
    }

    @PostMapping("resetPasswordByToken")
    @ApiModelProperty("通过令牌重置密码")
    public ApiResponse checkImageCode(@Validated @RequestBody TokenResetPasswordForm form){
        userService.resetPasswordByToken(form.getPassword(), form.getToken());
        return ApiResponse.ofSuccess();
    }

    private void phoneChecker(String phone){
        // 手机号正则校验
        String regex = limitsProperty.getPhoneRegex();
        if(StringUtils.isBlank(phone) || !phone.matches(regex)){
            throw new BusinessException(ApiResponseEnum.NOT_VALID_PARAM);
        }
    }
}
