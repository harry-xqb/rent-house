package com.harry.renthouse.service.cache.impl;

import com.harry.renthouse.config.RedisConfig;
import com.harry.renthouse.entity.House;
import com.harry.renthouse.service.cache.RedisHouseService;
import com.harry.renthouse.util.RedisUtil;
import com.harry.renthouse.web.dto.HouseBasic;
import com.harry.renthouse.web.dto.HouseDTO;
import com.harry.renthouse.web.dto.HouseDetailDTO;
import com.harry.renthouse.web.dto.HousePictureDTO;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 房屋信息缓存
 * @author Harry Xu
 * @date 2020/9/2 14:11
 */
@Service
@Slf4j
public class RedisHouseServiceImpl implements RedisHouseService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ModelMapper modelMapper;

    private static final String REDIS_HOUSE_ID_PREFIX = "house:";

    private static final String REDIS_HOUSE_DTO_HASH_HOUSE = "house";
    private static final String REDIS_HOUSE_DTO_HASH_TAGS = "tags";
    private static final String REDIS_HOUSE_DTO_HASH_DETAIL = "detail";
    private static final String REDIS_HOUSE_DTO_HASH_PICTURES = "pictures";

    @Override
    public void addBrowseHistory(Long userId, Long houseId) {

    }

    @Override
    public List<Long> getBrowseHistoryList(Long userId, Long houseId) {
        return null;
    }

    @Override
    public void updateHouse(House house) {
        String key = REDIS_HOUSE_ID_PREFIX + house.getId();
        if(redisUtil.hget(key, REDIS_HOUSE_DTO_HASH_HOUSE) != null){
            redisUtil.hset(key, REDIS_HOUSE_DTO_HASH_HOUSE, house);
        }
    }

    @Override
    public void addHouseDTO(HouseDTO houseDTO) {
        String key = REDIS_HOUSE_ID_PREFIX + houseDTO.getId();
        redisUtil.hmset(key, houseDTOConvertToMap(houseDTO));
    }

    @Override
    public void addHouseDTOList(List<HouseDTO> houseDTOList) {
        redisUtil.pipeLine(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                for (HouseDTO houseDTO : houseDTOList) {
                    String key = REDIS_HOUSE_ID_PREFIX + houseDTO.getId();
                    operations.opsForHash().putAll(key, houseDTOConvertToMap(houseDTO));
                }
                return null;
            }
        });
    }

    @Override
    public Optional<HouseDTO> getHouseDTOById(Long houseId) {
        String key = REDIS_HOUSE_ID_PREFIX + houseId;
        Map<Object, Object> dtoResult = redisUtil.hmget(key);
        return houseMapConvertToHouseDTO(dtoResult);
    }

    @Override
    public List<HouseDTO> getByIds(List<Long> houseIds) {
        try{
            List<Object> houseDTOList = redisUtil.pipeLine(new SessionCallback() {
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {
                    for (Long houseId : houseIds) {
                        operations.opsForHash().entries(REDIS_HOUSE_ID_PREFIX + houseId);
                    }
                    return null;
                }
            });
            if(CollectionUtils.isEmpty(houseDTOList)){
                return Collections.emptyList();
            }
            return houseDTOList.stream()
                    .map(item -> houseMapConvertToHouseDTO((Map<Object, Object>) item).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }catch (Exception e){
            log.error("房源列表缓存读取失败", e);
        }
        return Collections.emptyList();
    };

    @Override
    public void addHouseTag(Long houseId, String... tags) {
        Set<String> redisTags = getTags(houseId);
        if(redisTags != null){
            redisTags.addAll(Arrays.asList(tags));
            setTags(houseId, redisTags);
        }
    }

    @Override
    public void deleteHouseTag(Long houseId, String... tags) {
        Set<String> redisTags = getTags(houseId);
        if(redisTags != null){
            redisTags.removeAll(Arrays.asList(tags));
            setTags(houseId, redisTags);
        }
    }

    @Override
    public void deleteHousePicture(Long houseId, Long pictureId) {
        String key = REDIS_HOUSE_ID_PREFIX + houseId;
        List<HousePictureDTO> housePictureDTOS = (List<HousePictureDTO>) redisUtil.hget(REDIS_HOUSE_ID_PREFIX + houseId, REDIS_HOUSE_DTO_HASH_PICTURES);
        if(housePictureDTOS != null){
            List<HousePictureDTO> result = housePictureDTOS.stream().filter(item -> item.getId() == pictureId.longValue()).collect(Collectors.toList());
            redisUtil.hset(key, REDIS_HOUSE_DTO_HASH_PICTURES, result);
        }
    }

    private Set<String> getTags(Long houseId){
        String key = REDIS_HOUSE_ID_PREFIX + houseId;
        Set<String> redisTags = (Set<String>) redisUtil.hget(key, REDIS_HOUSE_DTO_HASH_TAGS);
        return redisTags;
    }

    private void setTags(Long houseId, Set<String> tags){
        redisUtil.hset(REDIS_HOUSE_ID_PREFIX + houseId, REDIS_HOUSE_DTO_HASH_TAGS, tags);
    }

    private Map<String, Object> houseDTOConvertToMap(HouseDTO houseDTO){
        House house = modelMapper.map(houseDTO, House.class);
        Set<String> tags = houseDTO.getTags();
        HouseDetailDTO houseDetail = houseDTO.getHouseDetail();
        List<HousePictureDTO> pictures = houseDTO.getHousePictureList();
        Map<String, Object> map = new HashMap<>();
        map.put(REDIS_HOUSE_DTO_HASH_HOUSE, house);
        map.put(REDIS_HOUSE_DTO_HASH_TAGS, tags);
        map.put(REDIS_HOUSE_DTO_HASH_DETAIL, houseDetail);
        map.put(REDIS_HOUSE_DTO_HASH_PICTURES, pictures);
        return  map;
    }

    private Optional<HouseDTO> houseMapConvertToHouseDTO(Map<Object, Object> map){
        try{
            if(map.size() == 0){
                return Optional.empty();
            }
            House house = (House) map.get(REDIS_HOUSE_DTO_HASH_HOUSE);
            if(house == null){
                return Optional.empty();
            }
            Set<String> tags = ( Set<String>) map.get(REDIS_HOUSE_DTO_HASH_TAGS);
            HouseDetailDTO houseDetail = (HouseDetailDTO) map.get(REDIS_HOUSE_DTO_HASH_DETAIL);
            List<HousePictureDTO> pictures = (List<HousePictureDTO>) map.get(REDIS_HOUSE_DTO_HASH_PICTURES);
            HouseDTO dto = modelMapper.map(house, HouseDTO.class);
            dto.setHousePictureList(pictures);
            dto.setTags(tags);
            dto.setHousePictureList(pictures);
            dto.setHouseDetail(houseDetail);
            return Optional.of(dto);
        }catch (Exception e){
            log.error("房源DTO缓存读取失败", e);
        }
        return Optional.empty();
    }
}
