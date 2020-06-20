package com.harry.renthouse.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Harry Xu
 * @date 2020/5/8 10:31
 */
@AllArgsConstructor
@Getter
public enum ApiResponseEnum {

    SUCCESS(200, "成功"),
    BAD_REQUEST(400, "请求失败"),
    UNAUTHORIZED(401, "未认证"),
    NOT_FOUND(404, "请求地址不存在"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    NOT_LOGIN(40001, "用户未登录"),
    USERNAME_PASSWORD_ERROR(40002, "用户名密码错误"),
    NO_PRIORITY_ERROR(40003, "无权访问"),
    NOT_VALID_PARAM(40005, "无效的参数"),
    UNSUPPORTED_OPERATION(40006, "不支持的操作"),
    NO_AUTHENTICATED_USER_ERROR(40007, "获取认证用户失败"),
    NOT_VALID_CREDENTIAL(40008, "无效的凭据"),
    USER_NOT_FOUND(40009, "用户不存在"),
    USER_NICK_NAME_ALREADY_EXIST(40010, "用户名已存在"),
    ORIGINAL_PASSWORD_ERROR(40011, "原密码错误"),
    ORIGINAL_PASSWORD_EMPTY_ERROR(40012, "原密码不能为空"),
    // 地址相关
    SUPPORT_ADDRESS_ERROR(50102, "地址选择有误"),
    SUBWAY_LINE_ERROR(50103, "地铁线路有误"),
    SUBWAY_STATION_ERROR(50104, "地铁站有误"),
    SUBWAY_AND_STATION_MATCH_ERROR(50105, "地铁线路与地铁站匹配错误"),
    ADDRESS_CITY_NOT_FOUND(50106, "城市未找到"),
    ADDRESS_REGION_NOT_FOUND(50107, "区县未找到"),
    // 房源相关
    HOUSE_NOT_FOUND_ERROR(50206, "房源信息未找到"),
    HOUSE_DETAIL_NOT_FOUND_ERROR(50207, "房源详情未找到"),
    TAG_ADD_FAIL(50210, "添加标签失败"),
    TAG_DELETE_FAIL(50211, "删除标签失败"),
    TAG_ALREADY_EXIST(50212, "标签已存在"),
    TAG_NOT_EXIST(50213, "标签不存在"),
    PICTURE_NOT_EXIST(50214, "图片不存在"),
    PICTURE_DELETE_FAIL(50215, "图片删除失败"),
    HOUSE_STATUS_NOT_CHANGE(50216, "房源状态未改变"),
    HOUSE_STATUS_CHANGE_ERROR_RENTED(50217, "不允许修改已出租房源状态"),
    HOUSE_STATUS_CHANGE_ERROR_DELETED(50218, "不允许修改已删除房源状态"),
    HOUSE_PRICE_RAGE_ERROR(50219, "价格区间有误"),
    HOUSE_AREA_RANGE_ERROR(50220, "面积区间有误"),
    HOUSE_STATUS_NOT_FOUND(50224, "房源状态未找到"),

    HOUSE_SUBSCRIBE_ALREADY_ORDER(500221, "已预约该房源"),
    HOUSE_SUBSCRIBE_ALREADY_FINISH(500222, "已看过该房源"),
    HOUSE_SUBSCRIBE_STATUS_ERROR(500223, "预约状态有误"),
    HOUSE_SUBSCRIBE_NOT_FOUND(500223, "预约信息未找到"),
    // 短信相关
    PHONE_ALREADY_REGISTERED(50323, "手机号已被注册"),
    PHONE_SEND_SMS_ERROR(50324, "发送短信失败"),
    PHONE_SEND_SMS_BUSY(50325, "短信发送太频繁"),
    PHONE_SMS_NOT_VALID_TYPE(50326, "无效的验证码类型"),
    PHONE_SMS_CODE_ERROR(50327, "验证码错误"),
    PHONE_SMS_CODE_EXPIRE(50328, "验证码已过期"),
    // 搜索相关
    ELASTIC_HOUSE_SUGGEST_CREATE_ERROR(50429, "房源补全分词错误"),
    ELASTIC_HOUSE_NOT_FOUND(50430, "房屋索引不存在"),

    // 文件相关
    FILE_UPLOAD_ERROR(50501, "文件上传失败"),
    FILE_SIZE_EXCEED_ERROR(50502, "文件超过限制大小:{0}"),
    FILE_TYPE_UNSUPPORTED_ERROR(50502, "文件类型不支持"),

    ;

    private Integer code;

    private String message;

}
