package com.harry.renthouse.task;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.harry.renthouse.property.EsMonitorProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Harry Xu
 * @date 2020/6/8 13:48
 */
@Component
@Slf4j
@EnableScheduling
public class ESMonitor {

    private static final String RED = "red";
    private static final String YELLOW = "yellow";
    private static final String GREEN = "green";
    private static final String MAIL_REDIS_PREFIX = "MAIL:INTERVAL:";
    public static final int MAIL_REDIS_INTERVAL = 60 * 60 * 24;

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private EsMonitorProperty esMonitorProperty;

    @Resource
    private Gson gson;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Scheduled(fixedDelay = 5000)
    public void healthCheck(){
        HttpClient httpClient = HttpClients.createDefault();

        HttpGet get = new HttpGet(esMonitorProperty.getApi());
        try {
            HttpResponse response = httpClient.execute(get);
            if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
                log.error("无法访问es服务！请检查服务器");
                return;
            }
            String body = EntityUtils.toString(response.getEntity(), "UTF-8");
            JsonObject result = gson.fromJson(body, JsonObject.class);
            String status = result.get("status").getAsString();
            String message = "";
            switch (status){
                case GREEN:
                    log.debug("ES状况健康");
                    break;
                case YELLOW:
                    message = "ES状况为yellow, 请检查ES服务器";
                    log.warn(message);
                    break;
                case RED:
                    message = "ES状况为yellow, 请检查ES服务器";
                    log.error(message);
            }
            if(StringUtils.isNotBlank(message)){
                String resultInterval = stringRedisTemplate.opsForValue().get(MAIL_REDIS_PREFIX + status);
                // 如果redis中不存在发过的邮件记录，则发送邮件
                if(!StringUtils.isNotBlank(resultInterval)){
                    stringRedisTemplate.opsForValue().set(MAIL_REDIS_PREFIX + status, message, MAIL_REDIS_INTERVAL, TimeUnit.SECONDS);
                    sendMail(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void sendMail(String message){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(esMonitorProperty.getMailForm());
        mailMessage.setTo(esMonitorProperty.getMailTo());
        mailMessage.setSubject(esMonitorProperty.getMailTitle());
        mailMessage.setText(message);
        javaMailSender.send(mailMessage);
    }
}
