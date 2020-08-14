package com.harry.renthouse.util;

/**
 * @author Harry Xu
 * @date 2020/8/10 11:14
 */

import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.base.ImageCodeTypeEnum;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.web.dto.VerifyImageCheckDTO;
import com.harry.renthouse.web.dto.VerifyImageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


/**
 * 图片验证码工具类
 */
@Component
@Slf4j
public class VerifyImageUtil {

    private static final String IMAGE_CODE_PREFIX = "IMAGE:CODE:";

    private static final int IMAGE_CODE_KEY_EXPIRE = 60 * 5;

    // 验证成功后存储的操作码前缀
    public static final String VERIFY_OPERATE_PREFIX  = "VERIFY:OPERATE:";

    private static final int VERIFY_OPERATE_KEY_EXPIRE = 60 * 15;

    /**
     * 模板图宽度
     */
    private static final int CUT_WIDTH = 50;
    /**
     * 模板图高度
     */
    private static final int CUT_HEIGHT = 50;
    /**
     * 抠图凸起圆心
     */
    private static final int circleR = 5;
    /**
     * 抠图内部矩形填充大小
     */
    private static final int RECTANGLE_PADDING = 6;
    /**
     * 抠图的边框宽度
     */
    private static final int SLIDER_IMG_OUT_PADDING = 1;

    @Autowired
    private RedisUtil redisUtil;

    private static RedisUtil redisStaticUtil;

    @PostConstruct
    public void init(){
        redisStaticUtil = redisUtil;
    }

    /**
     * 生成滑动对应图片
     * @return {backImage: base64, slidingImage: base64'}
     * @throws IOException
     */
    public static VerifyImageDTO create(String phone) {
        try{
            int[][] blockData = getBlockData();
            // 这里的图片可以准备几张随机获取
            int imageIndex = new Random().nextInt(7) + 1;
            Resource resourceOri = new ClassPathResource("static/verify-image-" + imageIndex +".jpg");
            InputStream fileStream = resourceOri.getInputStream();
            BufferedImage oriImage = ImageIO.read(fileStream);

            int x = new Random().nextInt(oriImage.getWidth() - 2 * CUT_WIDTH) + CUT_WIDTH;
            int y = new Random().nextInt(oriImage.getHeight() - CUT_HEIGHT);

            BufferedImage targetImage= new BufferedImage(CUT_WIDTH, CUT_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
            cutImgByTemplate(oriImage, targetImage, blockData, x, y);

            // 设置返回结果
            VerifyImageDTO verifyImageDTO = new VerifyImageDTO();
            verifyImageDTO.setBackImage(getImageBASE64(oriImage));
            verifyImageDTO.setSlideImage(getImageBASE64(targetImage));
            verifyImageDTO.setY(y);
            // 存储x轴信息
            redisStaticUtil.set(IMAGE_CODE_PREFIX + phone, x, IMAGE_CODE_KEY_EXPIRE);
            return verifyImageDTO;

        }catch (IOException e){
            e.printStackTrace();
            throw new BusinessException(ApiResponseEnum.IMAGE_CODE_CREATE_ERROR);
        }
    }

    public static VerifyImageCheckDTO check(String phone, int x){
        Object result = redisStaticUtil.get(IMAGE_CODE_PREFIX + phone);
        redisStaticUtil.del(phone);
        if(result == null){
            throw new BusinessException(ApiResponseEnum.IMAGE_CODE_EXPIRE);
        }
        int number = (int)result;
        if(x < (number - CUT_WIDTH * 0.3) || x > (number + CUT_WIDTH * 0.3)){
            throw new BusinessException(ApiResponseEnum.IMAGE_CODE_ERROR);
        }
        int percent = (int) ((double)(CUT_WIDTH - Math.abs(number - x)) / CUT_WIDTH * 100);
        String verifyCode = UUID.randomUUID().toString();
        redisStaticUtil.set(VERIFY_OPERATE_PREFIX + verifyCode, phone, VERIFY_OPERATE_KEY_EXPIRE);
        VerifyImageCheckDTO verifyImageCheckDTO = new VerifyImageCheckDTO();
        verifyImageCheckDTO.setVerifyCode(verifyCode);
        verifyImageCheckDTO.setVerifyPercent(percent);
        return verifyImageCheckDTO;
    }

    /**
     * 生成随机滑块形状
     * <p>
     * 0 透明像素
     * 1 滑块像素
     * 2 阴影像素
     * @return int[][]
     */
    private static int[][] getBlockData() {
        int[][] data = new int[CUT_WIDTH][CUT_HEIGHT];
        Random random = new Random();
        //(x-a)²+(y-b)²=r²
        //x中心位置左右5像素随机
        double x1 = RECTANGLE_PADDING + (CUT_WIDTH - 2 * RECTANGLE_PADDING) / 2.0 - 5 + random.nextInt(10);
        //y 矩形上边界半径-1像素移动
        double y1_top = RECTANGLE_PADDING - random.nextInt(3);
        double y1_bottom = CUT_HEIGHT - RECTANGLE_PADDING + random.nextInt(3);
        double y1 = random.nextInt(2) == 1 ? y1_top : y1_bottom;


        double x2_right = CUT_WIDTH - RECTANGLE_PADDING - circleR + random.nextInt(2 * circleR - 4);
        double x2_left = RECTANGLE_PADDING + circleR - 2 - random.nextInt(2 * circleR - 4);
        double x2 = random.nextInt(2) == 1 ? x2_right : x2_left;
        double y2 = RECTANGLE_PADDING + (CUT_HEIGHT - 2 * RECTANGLE_PADDING) / 2.0 - 4 + random.nextInt(10);

        double po = Math.pow(circleR, 2);
        for (int i = 0; i < CUT_WIDTH; i++) {
            for (int j = 0; j < CUT_HEIGHT; j++) {
                //矩形区域
                boolean fill;
                if ((i >= RECTANGLE_PADDING && i < CUT_WIDTH - RECTANGLE_PADDING)
                        && (j >= RECTANGLE_PADDING && j < CUT_HEIGHT - RECTANGLE_PADDING)) {
                    data[i][j] = 1;
                    fill = true;
                } else {
                    data[i][j] = 0;
                    fill = false;
                }
                //凸出区域
                double d3 = Math.pow(i - x1, 2) + Math.pow(j - y1, 2);
                if (d3 < po) {
                    data[i][j] = 1;
                } else {
                    if (!fill) {
                        data[i][j] = 0;
                    }
                }
                //凹进区域
                double d4 = Math.pow(i - x2, 2) + Math.pow(j - y2, 2);
                if (d4 < po) {
                    data[i][j] = 0;
                }
            }
        }
        //边界阴影
        for (int i = 0; i < CUT_WIDTH; i++) {
            for (int j = 0; j < CUT_HEIGHT; j++) {
                //四个正方形边角处理
                for (int k = 1; k <= SLIDER_IMG_OUT_PADDING; k++) {
                    //左上、右上
                    if (i >= RECTANGLE_PADDING - k && i < RECTANGLE_PADDING
                            && ((j >= RECTANGLE_PADDING - k && j < RECTANGLE_PADDING)
                            || (j >= CUT_HEIGHT - RECTANGLE_PADDING - k && j < CUT_HEIGHT - RECTANGLE_PADDING +1))) {
                        data[i][j] = 2;
                    }

                    //左下、右下
                    if (i >= CUT_WIDTH - RECTANGLE_PADDING + k - 1 && i < CUT_WIDTH - RECTANGLE_PADDING + 1) {
                        for (int n = 1; n <= SLIDER_IMG_OUT_PADDING; n++) {
                            if (((j >= RECTANGLE_PADDING - n && j < RECTANGLE_PADDING)
                                    || (j >= CUT_HEIGHT - RECTANGLE_PADDING - n && j <= CUT_HEIGHT - RECTANGLE_PADDING ))) {
                                data[i][j] = 2;
                            }
                        }
                    }
                }

                if (data[i][j] == 1 && j - SLIDER_IMG_OUT_PADDING > 0 && data[i][j - SLIDER_IMG_OUT_PADDING] == 0) {
                    data[i][j - SLIDER_IMG_OUT_PADDING] = 2;
                }
                if (data[i][j] == 1 && j + SLIDER_IMG_OUT_PADDING > 0 && j + SLIDER_IMG_OUT_PADDING < CUT_HEIGHT && data[i][j + SLIDER_IMG_OUT_PADDING] == 0) {
                    data[i][j + SLIDER_IMG_OUT_PADDING] = 2;
                }
                if (data[i][j] == 1 && i - SLIDER_IMG_OUT_PADDING > 0 && data[i - SLIDER_IMG_OUT_PADDING][j] == 0) {
                    data[i - SLIDER_IMG_OUT_PADDING][j] = 2;
                }
                if (data[i][j] == 1 && i + SLIDER_IMG_OUT_PADDING > 0 && i + SLIDER_IMG_OUT_PADDING < CUT_WIDTH && data[i + SLIDER_IMG_OUT_PADDING][j] == 0) {
                    data[i + SLIDER_IMG_OUT_PADDING][j] = 2;
                }
            }
        }
        return data;
    }

    /**
     * 裁剪区块
     * 根据生成的滑块形状，对原图和裁剪块进行变色处理
     * @param oriImage    原图
     * @param targetImage 裁剪图
     * @param blockImage  滑块
     * @param x           裁剪点x
     * @param y           裁剪点y
     */
    private static void cutImgByTemplate(BufferedImage oriImage, BufferedImage targetImage, int[][] blockImage, int x, int y) {
        for (int i = 0; i < CUT_WIDTH; i++) {
            for (int j = 0; j < CUT_HEIGHT; j++) {
                int _x = x + i;
                int _y = y + j;
                int rgbFlg = blockImage[i][j];
                int rgb_ori = oriImage.getRGB(_x, _y);
                // 原图中对应位置变色处理
                if (rgbFlg == 1) {
                    //抠图上复制对应颜色值
                    targetImage.setRGB(i,j, rgb_ori);
                    //原图对应位置颜色变化
                    oriImage.setRGB(_x, _y,  Color.LIGHT_GRAY.getRGB());
                } else if (rgbFlg == 2) {
                    targetImage.setRGB(i, j, Color.WHITE.getRGB());
                    oriImage.setRGB(_x, _y, Color.GRAY.getRGB());
                }else if(rgbFlg == 0){
                    //int alpha = 0;
                    targetImage.setRGB(i, j, rgb_ori & 0x00ffffff);
                }
            }

        }
    }

    /**
     * 将IMG输出为文件
     * @param image
     * @param file
     * @throws Exception
     */
    public static void writeImg(BufferedImage image, String file) throws Exception {
        byte[] imagedata = null;
        ByteArrayOutputStream bao=new ByteArrayOutputStream();
        ImageIO.write(image,"png",bao);
        imagedata = bao.toByteArray();
        FileOutputStream out = new FileOutputStream(new File(file));
        out.write(imagedata);
        out.close();
    }

    /**
     * 将图片转换为BASE64
     * @param image
     * @return
     * @throws IOException
     */
    public static String getImageBASE64(BufferedImage image) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image,"png",out);
        //转成byte数组
        byte[] bytes = out.toByteArray();
        BASE64Encoder encoder = new BASE64Encoder();
        //生成BASE64编码
        return encoder.encode(bytes);
    }


    /**
     * 将BASE64字符串转换为图片
     * @param base64String
     * @return
     */
    public static BufferedImage base64StringToImage(String base64String) {
        try {
            BASE64Decoder decoder=new BASE64Decoder();
            byte[] bytes1 = decoder.decodeBuffer(base64String);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes1);
            return ImageIO.read(bais);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}