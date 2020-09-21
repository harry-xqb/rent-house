package com.harry.renthouse.service.house.impl;

import com.harry.renthouse.base.*;
import com.harry.renthouse.elastic.entity.BaiduMapLocation;
import com.harry.renthouse.elastic.entity.HouseElastic;
import com.harry.renthouse.entity.*;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.repository.*;
import com.harry.renthouse.service.ServiceMultiResult;
import com.harry.renthouse.service.cache.RedisHouseService;
import com.harry.renthouse.service.cache.RedisStarService;
import com.harry.renthouse.service.house.AddressService;
import com.harry.renthouse.service.house.HouseService;
import com.harry.renthouse.service.search.HouseElasticSearchService;
import com.harry.renthouse.util.AuthenticatedUserUtil;
import com.harry.renthouse.web.dto.*;
import com.harry.renthouse.web.form.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 房源service实现
 * @author Harry Xu
 * @date 2020/5/9 15:12
 */
@Service
@Slf4j
public class HouseServiceImpl implements HouseService {

    @Resource
    private HouseRepository houseRepository;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private SubwayRepository subwayRepository;

    @Resource
    private SubwayStationRepository subwayStationRepository;

    @Resource
    private HousePictureRepository housePictureRepository;

    @Resource
    private HouseTagRepository houseTagRepository;

    @Resource
    private HouseDetailRepository houseDetailRepository;

    @Resource
    private SupportAddressRepository supportAddressRepository;

    @Resource
    private AddressService addressService;

    @Value("${qiniu.cdnPrefix}")
    private String cdnPrefix;

    @Resource
    private HouseElasticSearchService houseElasticSearchService;

    @Resource
    private HouseSubscribeRepository houseSubscribeRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private RedisHouseService redisHouseService;

    @Resource
    private RedisStarService redisStarService;

    @Transactional
    @Override
    public HouseDTO addHouse(HouseForm houseForm){
        checkCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());
        // 判断城市
        // 房屋详情数据填充与校验
        HouseDetail houseDetail = generateHouseDetail(houseForm);
        // 新增房屋信息
        House house = modelMapper.map(houseForm, House.class);
        house.setAdminId(AuthenticatedUserUtil.getUserId());
        // 默认审核通过
        house.setStatus(HouseStatusEnum.AUDIT_PASSED.getValue());
        house = houseRepository.save(house);
        //新增房屋详情信息
        houseDetail.setHouseId(house.getId());
        houseDetail = houseDetailRepository.save(houseDetail);
        HouseDetailDTO houseDetailDTO = modelMapper.map(houseDetail, HouseDetailDTO.class);
        // 新增房屋图片信息
        List<HousePicture> housePictures = housePictureRepository.saveAll(generateHousePicture(houseForm, house.getId()));
        List<HousePictureDTO> housePictureDTOList = housePictures.stream().map(picture -> modelMapper.map(picture, HousePictureDTO.class)).collect(Collectors.toList());
        // 新增房屋标签信息
        List<HouseTag> houseTagList = generateHouseTag(houseForm, house.getId());
        houseTagRepository.saveAll(houseTagList);
        // 设置返回结果
        HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
        houseDTO.setCover(cdnPrefix + houseDTO.getCover());
        houseDTO.setHouseDetail(houseDetailDTO);
        houseDTO.setHousePictureList(housePictureDTOList);
        houseDTO.setTags(new HashSet<>(houseForm.getTags()));
        // 缓存房屋信息
        redisHouseService.addHouseDTO(houseDTO);
        return houseDTO;
    }
    @Transactional
    public HouseDTO editHouse(HouseForm houseForm){
        checkCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());
        Long houseId = houseForm.getId();
        // 查看房源id是否存在
        House house = houseRepository.findByIdAndAdminId(houseId, AuthenticatedUserUtil.getUserId()).orElseThrow(() -> new BusinessException(ApiResponseEnum.HOUSE_NOT_FOUND_ERROR));
        // 查看房屋详情是否存在
        HouseDetail houseDetail = houseDetailRepository.findByHouseId(house.getId()).orElseThrow(() -> new BusinessException(ApiResponseEnum.HOUSE_DETAIL_NOT_FOUND_ERROR));
        // 更新房屋信息
        modelMapper.map(houseForm, house);
        HouseDTO houseDTO = modelMapper.map(houseRepository.save(house), HouseDTO.class);
        // 更新房屋详情
        HouseDetail updateHouseDetail = generateHouseDetail(houseForm);
        updateHouseDetail.setId(houseDetail.getId());
        updateHouseDetail.setHouseId(houseId);
        updateHouseDetail = houseDetailRepository.save(updateHouseDetail);
        HouseDetailDTO houseDetailDTO = modelMapper.map(updateHouseDetail, HouseDetailDTO.class);
        // 移除所有照片
        housePictureRepository.deleteAllByHouseId(houseId);
        housePictureRepository.flush();
        // 获取照片信息
        List<HousePicture> housePictures = housePictureRepository.saveAll(generateHousePicture(houseForm, houseId));
        List<HousePictureDTO> housePicturesDTO = housePictures.stream().map(picture -> modelMapper.map(picture, HousePictureDTO.class)).collect(Collectors.toList());
        // 移除所有标签
        houseTagRepository.deleteAllByHouseId(houseId);
        houseTagRepository.flush();
        // 保存所有标签
        // 新增房屋标签信息
        List<HouseTag> houseTagList = generateHouseTag(houseForm, house.getId());
        houseTagRepository.saveAll(houseTagList);
        // 填充房屋信息
        houseDTO.setHouseDetail(houseDetailDTO);
        houseDTO.setHousePictureList(housePicturesDTO);
        houseDTO.setTags(new HashSet<>(houseForm.getTags()));
        houseDTO.setCover(cdnPrefix + houseDTO.getCover());

        // 缓存房屋信息
        redisHouseService.addHouseDTO(houseDTO);
        return houseDTO;
    }

    @Override
    public ServiceMultiResult<HouseDTO> adminSearch(AdminHouseSearchForm searchForm) {
        // 条件查询
        Specification<House> querySpec = (Specification<House>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 管理员id必须为当前认证用户
            predicates.add(criteriaBuilder.equal(root.get("adminId"), AuthenticatedUserUtil.getUserId()));
            // 搜索的房源状态必须为未删除
            predicates.add(criteriaBuilder.notEqual(root.get("status"), HouseStatusEnum.DELETED.getValue()));
            // 如果房源状态不为空则加入条件搜索
            if(searchForm.getStatus() != null){
                HouseStatusEnum status = HouseStatusEnum.ofOptionNumber(searchForm.getStatus())
                        .orElseThrow(() -> new BusinessException(ApiResponseEnum.HOUSE_STATUS_NOT_FOUND));
                predicates.add(criteriaBuilder.equal(root.get("status"), status.getValue()));
            }
            // 如果城市不为空则将城市加入模糊查询
            if(StringUtils.isNotBlank(searchForm.getCityEnName())){
                predicates.add(criteriaBuilder.equal(root.get("cityEnName"), searchForm.getCityEnName()));
            }
            // 如果创建时间起始不为空加入搜索条件
            if(searchForm.getCreateTimeMin() != null){
                LocalDateTime minDate = searchForm.getCreateTimeMin().atStartOfDay();
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), Date.from(minDate.atZone(ZoneId.systemDefault()).toInstant())));
            }
            // 如果创建时间结束不为空加入搜索条件
            if(searchForm.getCreateTimeMax() != null){
                LocalDateTime maxDate = searchForm.getCreateTimeMax().atStartOfDay().plusDays(1);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), Date.from(maxDate.atZone(ZoneId.systemDefault()).toInstant())));
            }
            // 如果标题不为空加入模糊搜索条件
            if(StringUtils.isNotBlank(searchForm.getTitle())){
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + searchForm.getTitle() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        // 分页条件
        Sort sort = Sort.by(Sort.Direction.fromOptionalString(searchForm.getDirection()).orElse(Sort.Direction.ASC), searchForm.getOrderBy());
        int page = searchForm.getPage() - 1;
        Pageable pageable = PageRequest.of(page, searchForm.getPageSize(), sort);
        Page<House> houses = houseRepository.findAll(querySpec, pageable);
        List<HouseDTO> houseDTOList = convertToHouseDTOList(houses.getContent());
        // 设置房屋被收藏次数
        for (HouseDTO houseDTO : houseDTOList) {
            int number = (int) redisStarService.getHouseStarCount(houseDTO.getId());
            houseDTO.setStarNumber(number);
        }
        return new ServiceMultiResult<>((int)houses.getTotalElements(), houseDTOList);
    }

    @Override
    public HouseCompleteInfoDTO findCompleteHouse(Long houseId) {
        // 查找缓存中是否存在数据
        HouseDTO dto = redisHouseService.getHouseDTOById(houseId).orElseGet(() -> {
            House house = houseRepository.findById(houseId).orElseThrow(() -> new BusinessException(ApiResponseEnum.HOUSE_NOT_FOUND_ERROR));
            HouseDTO houseDTO = wrapHouseDTO(house);
            // 缓存
            redisHouseService.addHouseDTO(houseDTO);
            return houseDTO;
        });
        // 查找房屋信息
        return wrapHouseCompleteDTO(dto);
    }

    @Override
    public HouseCompleteInfoDTO findAgentEditCompleteHouse(Long houseId) {
        Long agentId = AuthenticatedUserUtil.getUserId();
        HouseDTO dto = redisHouseService.getHouseDTOById(houseId).map(item -> {
            if (agentId.longValue() != item.getAdminId()) {
                throw new BusinessException(ApiResponseEnum.HOUSE_NOT_FOUND_ERROR);
            }
            return item;
        }).orElseGet(() -> {
            House house = houseRepository.findByIdAndAdminId(houseId, agentId).orElseThrow(() -> new BusinessException(ApiResponseEnum.HOUSE_NOT_FOUND_ERROR));
            HouseDTO houseDTO = wrapHouseDTO(house);
            // 缓存
            redisHouseService.addHouseDTO(houseDTO);
            return houseDTO;
        });
        return wrapHouseCompleteDTO(dto);
    }

    private HouseDTO wrapHouseDTO(House house){
        Long houseId = house.getId();
        // 查找房屋详情
        HouseDetail houseDetail = houseDetailRepository.findByHouseId(houseId).orElseThrow(() -> new BusinessException(ApiResponseEnum.HOUSE_DETAIL_NOT_FOUND_ERROR));
        // 查找房屋标签
        List<HouseTag> tags = houseTagRepository.findAllByHouseId(houseId);
        // 查找房屋照片
        List<HousePicture> housePictureList = housePictureRepository.findAllByHouseId(houseId);
        // 组装成DTO对象
        HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
        HouseDetailDTO houseDetailDTO = modelMapper.map(houseDetail, HouseDetailDTO.class);
        List<String> tagStringList = tags.stream().map(HouseTag::getName).collect(Collectors.toList());
        List<HousePictureDTO> housePictureDTO = housePictureList.stream().map(picture -> modelMapper.map(picture, HousePictureDTO.class)).collect(Collectors.toList());
        // 设置组装houseDTO
        houseDTO.setCover(cdnPrefix + houseDTO.getCover());
        houseDTO.setTags(new HashSet<>(tagStringList));
        houseDTO.setHouseDetail(houseDetailDTO);
        houseDTO.setHousePictureList(housePictureDTO);
        return houseDTO;
    }

    private HouseCompleteInfoDTO wrapHouseCompleteDTO(HouseDTO houseDTO){
        // 设置地铁信息
        SupportAddressDTO city = addressService.findCityByName(houseDTO.getCityEnName())
                .orElseThrow(() -> new BusinessException(ApiResponseEnum.ADDRESS_CITY_NOT_FOUND));
        SupportAddressDTO region = addressService.findRegionByCityNameAndName(houseDTO.getCityEnName(), houseDTO.getRegionEnName())
                .orElseThrow(() -> new BusinessException(ApiResponseEnum.ADDRESS_REGION_NOT_FOUND));
        // 获取地铁线路信息
        Long subwayLineId = houseDTO.getHouseDetail().getSubwayLineId();
        String subWayName = houseDTO.getHouseDetail().getSubwayLineName();
        SubwayDTO subwayDTO = new SubwayDTO();
        subwayDTO.setId(subwayLineId);
        subwayDTO.setName(subWayName);
        // 获取地铁站信息
        Long subwayStationId = houseDTO.getHouseDetail().getSubwayStationId();
        String subwayStationName = houseDTO.getHouseDetail().getSubwayStationName();
        SubwayStationDTO subwayStationDTO = new SubwayStationDTO();
        subwayStationDTO.setId(subwayStationId);
        subwayStationDTO.setName(subwayStationName);
        // 拼装返回信息
        HouseCompleteInfoDTO houseCompleteInfoDTO = new HouseCompleteInfoDTO();
        houseCompleteInfoDTO.setHouse(houseDTO);
        houseCompleteInfoDTO.setCity(city);
        houseCompleteInfoDTO.setRegion(region);
        houseCompleteInfoDTO.setSubway(subwayDTO);
        houseCompleteInfoDTO.setSubwayStation(subwayStationDTO);

        // 计算房源被收藏次数
        long number = redisStarService.getHouseStarCount(houseDTO.getId());
        houseDTO.setStarNumber((int) number);
        return houseCompleteInfoDTO;
    }

    @Override
    @Transactional
    public void addTag(TagForm tagForm) {
        House house = houseRepository.findById(tagForm.getHouseId()).orElseThrow(() -> new BusinessException(ApiResponseEnum.HOUSE_NOT_FOUND_ERROR));
        Optional<HouseTag> houseTagOptional = houseTagRepository.findByNameAndHouseId(tagForm.getName(), tagForm.getHouseId());
        houseTagOptional.ifPresent(tag -> {
            throw new BusinessException(ApiResponseEnum.TAG_ALREADY_EXIST);
        });
        HouseTag houseTag = new HouseTag();
        houseTag.setHouseId(house.getId());
        houseTag.setName(tagForm.getName());
        houseTagRepository.save(houseTag);

        // 更新标签缓存
        redisHouseService.addHouseTag(house.getId(), houseTag.getName());
    }

    @Override
    @Transactional
    public void deleteTag(TagForm tagForm) {
        houseRepository.findById(tagForm.getHouseId()).orElseThrow(() -> new BusinessException(ApiResponseEnum.HOUSE_NOT_FOUND_ERROR));
        HouseTag houseTag = houseTagRepository.findByNameAndHouseId(tagForm.getName(), tagForm.getHouseId())
                .orElseThrow(() -> new BusinessException(ApiResponseEnum.TAG_NOT_EXIST));
        houseTagRepository.delete(houseTag);

        // 更新标签缓存
        redisHouseService.addHouseTag(tagForm.getHouseId(), houseTag.getName());
    }

    @Override
    @Transactional
    public void deletePicture(Long pictureId) {
        HousePicture housePicture = housePictureRepository.findById(pictureId).orElseThrow(() -> new BusinessException(ApiResponseEnum.PICTURE_NOT_EXIST));
        housePictureRepository.delete(housePicture);

        // 更新图片缓存
        redisHouseService.deleteHousePicture(housePicture.getHouseId(), pictureId);
        /*try {
            // TODO 七牛云中相同图片只存一份，如果删除的话会导致其他引用改图片的房屋照片也被删除
            Response response = qiniuService.deleteFile(housePicture.getPath());
            if(response.isOK()){
                housePictureRepository.delete(housePicture);
            }else{
                log.error("删除七牛云图片失败:{}", response.error);
                throw new BusinessException(ApiResponseEnum.PICTURE_DELETE_FAIL);
            }
        } catch (QiniuException e) {
            e.printStackTrace();
            throw new BusinessException(ApiResponseEnum.PICTURE_DELETE_FAIL);
        }*/
    }

    @Override
    @Transactional
    public void updateCover(Long coverId, Long houseId) {
        HousePicture housePicture = housePictureRepository.findById(coverId).orElseThrow(() -> new BusinessException(ApiResponseEnum.PICTURE_NOT_EXIST));
        House house = houseRepository.findById(houseId).orElseThrow(() -> new BusinessException(ApiResponseEnum.HOUSE_NOT_FOUND_ERROR));
        houseRepository.updateCover(house.getId(), housePicture.getPath());

        // 更新房源信息缓存
        House mapHouse = new House();
        modelMapper.map(house, mapHouse);
        mapHouse.setCover(cdnPrefix + house.getCover());
        redisHouseService.updateHouse(mapHouse);
    }

    @Override
    @Transactional
    public void updateStatus(Long houseId, HouseOperationEnum houseOperationEnum) {
        House house = houseRepository.findById(houseId).orElseThrow(() -> new BusinessException(ApiResponseEnum.HOUSE_NOT_FOUND_ERROR));
        // 如果当前房屋的管理员当前用户或者当前用户是超级管理员才允许修改
        User user = AuthenticatedUserUtil.getUserInfo();
        boolean superAdmin = user.getAuthorities().stream().anyMatch(item -> UserRoleEnum.SUPER_ADMIN.
                getValue().equalsIgnoreCase(item.getAuthority().replace("ROLE_", "")
        ));
        if(!superAdmin && house.getAdminId().longValue() != user.getId().longValue()){
            throw new BusinessException(ApiResponseEnum.NO_PRIORITY_ERROR);
        }
        int status = house.getStatus();
        if(status == houseOperationEnum.getCode()){
            throw new BusinessException(ApiResponseEnum.HOUSE_STATUS_NOT_CHANGE);
        }
        if(status == HouseStatusEnum.DELETED.getValue()){
            throw new BusinessException(ApiResponseEnum.HOUSE_STATUS_CHANGE_ERROR_DELETED);
        }
     /*   if(status == HouseStatusEnum.RENTED.getValue()){
            throw new BusinessException(ApiResponseEnum.HOUSE_STATUS_CHANGE_ERROR_RENTED);
        }*/


        houseRepository.updateStatus(houseId, houseOperationEnum.getCode());
        // 如果房源更新为审核通过则建立
        if(houseOperationEnum.getCode() == HouseOperationEnum.PASS.getCode()){
            houseElasticSearchService.save(houseId);
        }else{
            houseElasticSearchService.delete(houseId);
        }
        // 更新房源信息缓存
        House mapHouse = new House();
        modelMapper.map(house, mapHouse);
        mapHouse.setCover(cdnPrefix + house.getCover());
        redisHouseService.updateHouse(mapHouse);
    }

    @Override
    public ServiceMultiResult<HouseDTO> search(SearchHouseForm searchHouseForm) {
        // 检查城市是否正确
        if(searchHouseForm.getCityEnName() == null){
            throw new BusinessException(ApiResponseEnum.ADDRESS_CITY_NOT_FOUND);
        }
        // 价格区间监测是否正确
        if(searchHouseForm.getPriceMin() != null && searchHouseForm.getPriceMax() != null){
            if(searchHouseForm.getPriceMax() < searchHouseForm.getPriceMin()){
                throw new BusinessException(ApiResponseEnum.HOUSE_PRICE_RAGE_ERROR);
            }
        }
        // 面积区间检测是否正确
        if(searchHouseForm.getAreaMin() != null && searchHouseForm.getAreaMax() != null){
            if(searchHouseForm.getAreaMax() < searchHouseForm.getAreaMin()){
                throw new BusinessException(ApiResponseEnum.HOUSE_AREA_RANGE_ERROR);
            }
        }
        // 如果是按关键字搜索 或者按距离搜索， 则采用elasticsearch
        if(StringUtils.isNotBlank(searchHouseForm.getKeyword()) || searchHouseForm.getDistanceSearch() != null){
            ServiceMultiResult<HouseElastic> houseResult = houseElasticSearchService.search(searchHouseForm);
            List<Long> houseIdList = houseResult.getList().stream().map(HouseElastic::getHouseId).collect(Collectors.toList());
            if(houseResult.getTotal() == 0){
                return new ServiceMultiResult<>(0, Collections.emptyList());
            }
            return new ServiceMultiResult<>(houseResult.getTotal(), wrapperHouseResult(houseIdList));
        }

        return simpleSearch(searchHouseForm);
    }

    @Override
    public ServiceMultiResult<HouseDTO> mapHouseSearch(MapSearchForm mapSearchForm) {
        // 检查城市是否正确
        if(mapSearchForm.getCityEnName() == null){
            throw new BusinessException(ApiResponseEnum.ADDRESS_CITY_NOT_FOUND);
        }
        SearchHouseForm searchHouseForm = new SearchHouseForm();
        modelMapper.map(mapSearchForm, searchHouseForm);
        ServiceMultiResult<HouseElastic> houseElasticServiceMultiResult = houseElasticSearchService.search(searchHouseForm);
        // id与位置坐标映射
        Map<Long, BaiduMapLocation> idLocationMap = houseElasticServiceMultiResult.getList().stream().
                collect(Collectors.toMap(HouseElastic::getHouseId, HouseElastic::getLocation));
        List<Long> houseIdList = houseElasticServiceMultiResult.getList().stream().map(HouseElastic::getHouseId).collect(Collectors.toList());
        List<HouseDTO> houseDTOList = wrapperHouseResult(houseIdList);
        // 设置地理坐标
        houseDTOList.forEach(house -> {
            house.setLocation(idLocationMap.get(house.getId()));
        });
        return new ServiceMultiResult<>(houseElasticServiceMultiResult.getTotal(), houseDTOList);
    }

    @Override
    public void addSubscribeOrder(SubscribeHouseForm subscribeHouseForm) {
        Long userId = AuthenticatedUserUtil.getUserId();
        // 判断当前用户是否已预约该房源
        houseSubscribeRepository.findByUserIdAndHouseId(userId, subscribeHouseForm.getHouseId()).ifPresent(item -> {
            if(item.getStatus() == HouseSubscribeStatusEnum.ORDERED.getValue()){
                throw new BusinessException(ApiResponseEnum.HOUSE_SUBSCRIBE_ALREADY_ORDER);
            }
            if(item.getStatus() == HouseSubscribeStatusEnum.FINISH.getValue()){
                throw new BusinessException(ApiResponseEnum.HOUSE_SUBSCRIBE_ALREADY_FINISH);
            }
        });
        // 查找房屋的管理员
        House house = houseRepository.findById(subscribeHouseForm.getHouseId()).orElseThrow(() -> new BusinessException(ApiResponseEnum.HOUSE_NOT_FOUND_ERROR));
        HouseSubscribe houseSubscribe = new HouseSubscribe();
        houseSubscribe.setAdminId(house.getAdminId());
        houseSubscribe.setOrderTime(subscribeHouseForm.getTime());
        houseSubscribe.setDescription(subscribeHouseForm.getDescription());
        houseSubscribe.setHouseId(house.getId());
        houseSubscribe.setTelephone(subscribeHouseForm.getPhone());
        houseSubscribe.setStatus(HouseSubscribeStatusEnum.WAIT.getValue());
        houseSubscribe.setUserId(userId);
        houseSubscribeRepository.save(houseSubscribe);

    }

    @Override
    public Integer getHouseSubscribeStatus(Long houseId) {
        Long userId = AuthenticatedUserUtil.getUserId();
        return houseSubscribeRepository.findByUserIdAndHouseId(userId, houseId).map(HouseSubscribe::getStatus).orElse(0);
    }

    @Override
    public ServiceMultiResult<HouseSubscribeInfoDTO> listUserHouseSubscribes(ListHouseSubscribesForm subscribesForm) {
        Function<ListHouseSubscribeParams, Page<HouseSubscribe>> func = param ->
                houseSubscribeRepository.findByUserIdAndStatus(param.getUserId(), param.getStatus(), param.getPageable());
        return listHouseSubscribes(subscribesForm, func);
    }

    @Override
    @Transactional
    public void cancelHouseSubscribe(Long subscribeId) {
        long userId = AuthenticatedUserUtil.getUserId();
        HouseSubscribe houseSubscribe = houseSubscribeRepository.findById(subscribeId).orElseThrow(() -> new BusinessException(ApiResponseEnum.HOUSE_SUBSCRIBE_NOT_FOUND));
        if(houseSubscribe.getUserId() != userId && houseSubscribe.getAdminId() != userId){
            throw new BusinessException(ApiResponseEnum.HOUSE_SUBSCRIBE_NOT_FOUND);
        }
        if(houseSubscribe.getStatus() == HouseSubscribeStatusEnum.FINISH.getValue()){
            throw new BusinessException(ApiResponseEnum.HOUSE_SUBSCRIBE_CANCEL_FINISH_ERROR);
        }
        houseSubscribeRepository.deleteById(subscribeId);
        //TODO 发送短信通知管理员或者用户，当前预约取消
    }

    @Override
    public ServiceMultiResult<HouseSubscribeInfoDTO> listAdminHouseSubscribes(ListHouseSubscribesForm subscribesForm) {
        Function<ListHouseSubscribeParams, Page<HouseSubscribe>> func = param ->
                houseSubscribeRepository.findByAdminIdAndStatus(param.getUserId(), param.getStatus(), param.getPageable());
        return listHouseSubscribes(subscribesForm, func);
    }

    @Override
    public void adminUpdateHouseSubscribeStatus(Long subscribeId, int status) {
        // 获取当前用户id（房东id）
        Long userId = AuthenticatedUserUtil.getUserId();
        // 通过用户id与约看id查找 约看信息
        HouseSubscribe houseSubscribe = houseSubscribeRepository.findById(subscribeId).orElseThrow(()
                -> new BusinessException(ApiResponseEnum.HOUSE_SUBSCRIBE_NOT_FOUND));
        if(!houseSubscribe.getAdminId().equals(userId)){
            throw new BusinessException(ApiResponseEnum.NO_PRIORITY_ERROR);
        }
        // 如果目标状态待看房并且当前状态为待确认
        if(houseSubscribe.getStatus() == HouseSubscribeStatusEnum.WAIT.getValue() && status == HouseSubscribeStatusEnum.ORDERED.getValue()){
            houseSubscribe.setStatus(HouseSubscribeStatusEnum.ORDERED.getValue());
            houseSubscribeRepository.save(houseSubscribe);
            return;
        }
        // 如果目标状态为已完成并且当前状态为待看房
        if(houseSubscribe.getStatus() == HouseSubscribeStatusEnum.ORDERED.getValue() && status == HouseSubscribeStatusEnum.FINISH.getValue()){
            houseSubscribe.setStatus(HouseSubscribeStatusEnum.FINISH.getValue());
            houseSubscribeRepository.save(houseSubscribe);
            return;
        }
        // 状态不匹配抛出异常
        throw new BusinessException(ApiResponseEnum.HOUSE_SUBSCRIBE_STATUS_ERROR);
    }

    @Override
    public void starHouse(Long houseId) {
        Long userId = AuthenticatedUserUtil.getUserId();
       /* HouseStar houseStar = new HouseStar();
        houseStar.setHouseId(houseId);
        houseStar.setUserId(userId);
        houseStarRepository.save(houseStar);*/
        // 收藏数据直接存入redis
        redisStarService.star(userId, houseId);
    }

    @Override
    public ServiceMultiResult<HouseStarDTO> userStarHouseList(ListHouseStarForm houseStarForm) {
        // 获取当前用户id（房东id）
        Long userId = AuthenticatedUserUtil.getUserId();
        // 所有预约信息
        /*Sort sort = Sort.by(Sort.Direction.fromOptionalString(houseStarForm.getSortDirection()).orElse(Sort.Direction.DESC),
                houseStarForm.getOrderBy());*/
        Pageable pageable = PageRequest.of(houseStarForm.getPage() - 1, houseStarForm.getPageSize());
        // 从缓存中查找
        Page<HouseStar> houseStarPage = redisStarService.findAllByUserId(userId, pageable, Sort.Direction.fromOptionalString(houseStarForm.getSortDirection()).orElse(Sort.Direction.DESC));
       // Page<HouseStar> houseStarPage = houseStarRepository.findAllByUserId(userId, pageable);

        List<HouseStar> houseStarList = houseStarPage.getContent();
        List<Long> houseList = houseStarList.stream().map(HouseStar::getHouseId).collect(Collectors.toList());
        List<HouseDTO> houseDTOList = wrapperHouseResult(houseList);
        Map<Long, HouseDTO> houseIdDTOMap = houseDTOList.stream().collect(Collectors.toMap(HouseDTO::getId, houseDTO -> houseDTO));
        List<HouseStarDTO> result = houseStarList.stream().map(item -> {
            HouseStarDTO houseStarDTO = new HouseStarDTO();
            houseStarDTO.setId(item.getId());
            houseStarDTO.setHouse(houseIdDTOMap.get(item.getHouseId()));
            houseStarDTO.setCreateTime(item.getCreateTime());
            return houseStarDTO;
        }).collect(Collectors.toList());

        return new ServiceMultiResult<>((int)houseStarPage.getTotalElements(), result);
    }

    @Override
    @Transactional
    public void deleteStarInfo(Long houseId) {
        Long userId = AuthenticatedUserUtil.getUserId();
        redisStarService.unStar(userId, houseId);
      /*  boolean isStar = houseStarRepository.existsByHouseIdAndUserId(houseId, userId);
        if(isStar){
            houseStarRepository.deleteByHouseIdAndUserId(houseId, userId);
        }else{
            throw new BusinessException(ApiResponseEnum.HOUSE_UN_STAR_NOT_FOUND_ERROR);
        }*/
    }
    @Override
    public List<HouseDTO> findAllByIds(List<Long> houseIdList) {
        return wrapperHouseResult(houseIdList);
    }

    @Override
    public boolean isStarHouse(long houseId, long userId) {
        //return houseStarRepository.existsByHouseIdAndUserId(houseId, userId);
        return redisStarService.isStar(userId, houseId);
    }

    @Override
    public boolean isReserveHouse(long houseId, long userId) {
        return houseSubscribeRepository.existsByHouseIdAndUserId(houseId, userId);
    }

    private ServiceMultiResult<HouseSubscribeInfoDTO> listHouseSubscribes(ListHouseSubscribesForm subscribesForm,
                                                     Function<ListHouseSubscribeParams, Page<HouseSubscribe>> func) {
        HouseSubscribeStatusEnum.of(subscribesForm.getStatus())
                .orElseThrow(() -> new BusinessException(ApiResponseEnum.HOUSE_SUBSCRIBE_STATUS_ERROR));
        Long userId = AuthenticatedUserUtil.getUserId();
        Sort sort = Sort.by(Sort.Direction.fromOptionalString(subscribesForm.getSortDirection()).orElse(Sort.Direction.DESC),
                subscribesForm.getOrderBy());
        Pageable pageable = PageRequest.of(subscribesForm.getPage() - 1, subscribesForm.getPageSize(),
                sort);

        // 所有预约信息
        Page<HouseSubscribe> page = func.apply(new ListHouseSubscribeParams(userId, subscribesForm.getStatus(), pageable));

        // 获取房屋信息
        Set<Long> houseIdSet = page.getContent().stream().map(HouseSubscribe::getHouseId).collect(Collectors.toSet());
        Map<Long, HouseDTO> houseMap = wrapperHouseResult(new ArrayList<>(houseIdSet)).stream().collect(Collectors.toMap(
                HouseDTO::getId, house -> house,
                (key1, key2) -> key1
        ));

        // 获取用户信息
        Set<Long> adminSet = page.getContent().stream().map(HouseSubscribe::getAdminId).collect(Collectors.toSet());
        Set<Long> userSet = page.getContent().stream().map(HouseSubscribe::getUserId).collect(Collectors.toSet());
        userSet.addAll(adminSet);
        Map<Long, UserDTO> userMap = userRepository.findAllById(userSet).stream().map(user -> modelMapper.map(user, UserDTO.class)).collect(
                Collectors.toMap(
                        UserDTO::getId,
                        userDto -> userDto,
                        (key1, key2) -> key1
                ));

        List<HouseSubscribeInfoDTO> result = page.getContent().stream().map(item -> {
            HouseSubscribeInfoDTO houseSubscribeInfoDTO = new HouseSubscribeInfoDTO();
            houseSubscribeInfoDTO.setHouseSubscribe(item);
            houseSubscribeInfoDTO.setHouseDTO(houseMap.get(item.getHouseId()));
            houseSubscribeInfoDTO.setUser(userMap.get(item.getUserId()));
            houseSubscribeInfoDTO.setAgent(userMap.get(item.getAdminId()));
            return houseSubscribeInfoDTO;
        }).collect(Collectors.toList());

        return new ServiceMultiResult<>((int)page.getTotalElements(), result);
    }

    /**
     * 基本sql查询
     */
    private ServiceMultiResult<HouseDTO> simpleSearch(SearchHouseForm searchHouseForm){
        supportAddressRepository
                .findByEnNameAndLevel(searchHouseForm.getCityEnName(), SupportAddress.AddressLevel.CITY.getValue())
                .orElseThrow(() -> new BusinessException(ApiResponseEnum.ADDRESS_CITY_NOT_FOUND));
        Specification<House> specification = (Specification<House>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 搜索出所有审核通过的
            predicates.add(criteriaBuilder.equal(root.get("status"), HouseStatusEnum.AUDIT_PASSED.getValue()));
            // 搜索按城市英文呢简称搜索
            predicates.add(criteriaBuilder.equal(root.get("cityEnName"), searchHouseForm.getCityEnName()));
            // 如果区县不为空，则继续搜索区县
            if(StringUtils.isNotBlank(searchHouseForm.getRegionEnName())){
                // 查询区县是否存在
                supportAddressRepository.findByBelongToAndEnNameAndLevel(searchHouseForm.getCityEnName(),
                        searchHouseForm.getRegionEnName(), SupportAddress.AddressLevel.REGION.getValue())
                        .orElseThrow(() -> new BusinessException(ApiResponseEnum.ADDRESS_REGION_NOT_FOUND));
                predicates.add(criteriaBuilder.equal(root.get("regionEnName"), searchHouseForm.getRegionEnName()));
            }
            // 如果地铁线路不为空，搜索地铁线路
            if(searchHouseForm.getSubwayLineId() != null){
                // 连接详细信息表
                Subway subway = subwayRepository.findById(searchHouseForm.getSubwayLineId()).orElseThrow(() -> new BusinessException(ApiResponseEnum.SUBWAY_LINE_ERROR));

                // 如果地铁站不为空，搜索地铁站, 否则只搜索地铁线
                if(!CollectionUtils.isEmpty(searchHouseForm.getSubwayStationIdList())){
                    List<SubwayStation> list = subwayStationRepository.getAllByIdIn(searchHouseForm.getSubwayStationIdList());
                    if(list.size() != searchHouseForm.getSubwayStationIdList().size()){
                        throw new BusinessException(ApiResponseEnum.SUBWAY_STATION_ERROR);
                    }
                    if(list.stream().anyMatch(item -> item.getSubwayId().longValue() != subway.getId().longValue())){
                        throw new BusinessException(ApiResponseEnum.SUBWAY_AND_STATION_MATCH_ERROR);
                    }
                    List<Long> houseIdList = houseDetailRepository.findAllHouseIdBySubwayLineIdAndSubwayStationIdIn(searchHouseForm.getSubwayLineId(), searchHouseForm.getSubwayStationIdList());
                    predicates.add(root.get("id").in(houseIdList));
                }else{
                    List<Long> houseIdList = houseDetailRepository.findAllHouseIdBySubwayLineId(searchHouseForm.getSubwayLineId());
                    predicates.add(root.get("id").in(houseIdList));
                }
            }
            // 出租方式
            if(searchHouseForm.getRentWay() != null){
                RentWayEnum.fromValue(searchHouseForm.getRentWay()).ifPresent(item -> {
                    log.debug("获取到出租方式:{}", item.getValue());
                    List<Long> houseIdList = houseDetailRepository.findAllByRentWay(item.getValue()).stream().map(HouseDetail::getHouseId).collect(Collectors.toList());
                    predicates.add(root.get("id").in(houseIdList));
                });
            }
            // 价格区间
            if(searchHouseForm.getPriceMin() != null){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), searchHouseForm.getPriceMin()));
            }
            if(searchHouseForm.getPriceMax() != null){
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), searchHouseForm.getPriceMax()));
            }
            // 房源标签
            if(searchHouseForm.getTags() != null && !searchHouseForm.getTags().isEmpty()){
                List<Long> houseIdList = houseTagRepository.findALLHouseIdMatchTags(searchHouseForm.getTags(), searchHouseForm.getTags().size());
                predicates.add(root.get("id").in(houseIdList));
            }
            // 房屋朝向
            if(searchHouseForm.getDirection() != null){
                predicates.add(criteriaBuilder.equal(root.get("direction"), searchHouseForm.getDirection()));
            }
            // 如果按地铁距离排序不为空，则过滤掉距离地铁为-1的地铁站
            if(StringUtils.equals(searchHouseForm.getOrderBy(), HouseSortOrderByEnum.DISTANCE_TO_SUBWAY.getValue())){
                predicates.add(criteriaBuilder.greaterThan(root.get("distanceToSubway"), -1));
            }
            // 房屋面积区间
            if(searchHouseForm.getAreaMin() != null){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("area"), searchHouseForm.getAreaMin()));
            }
            if(searchHouseForm.getAreaMax() != null){
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("area"), searchHouseForm.getAreaMax()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Sort.Direction sortDirection = Sort.Direction.fromOptionalString(searchHouseForm.getSortDirection()).orElse(Sort.Direction.DESC);
        Sort sort = Sort.by(sortDirection, HouseSortOrderByEnum.from(searchHouseForm.getOrderBy()).orElse(HouseSortOrderByEnum.DEFAULT).getValue());
        Pageable pageable = PageRequest.of(searchHouseForm.getPage() - 1, searchHouseForm.getPageSize(), sort);
        Page<House> houses = houseRepository.findAll(specification, pageable);
        List<HouseDTO> houseDTOList = wrapperHouseResultByHouses(houses.getContent());
        return new ServiceMultiResult<>((int) houses.getTotalElements(), houseDTOList);
    }

    /**
     * 通过id集合获取房源信息
     * @param houseIdList id集合列表
     * @return 房源dto列表
     */
    private List<HouseDTO> wrapperHouseResult(List<Long> houseIdList){
        Function<List<Long>, List<House>> func = unCachedIdList ->  houseRepository.findAllById(unCachedIdList);
        return wrapperHouseResult(houseIdList, func);
    }

    /**
     * 通过id集合获取房源信息
     * @param houseList 房源信息集合
     * @return 房源dto列表
     */
    private List<HouseDTO> wrapperHouseResultByHouses(List<House> houseList){
        List<Long> houseIdList = houseList.stream().map(House::getId).collect(Collectors.toList());
        Function<List<Long>, List<House>> func = unCachedIdList ->  houseList.stream().filter(item -> unCachedIdList.contains(item.getId())).collect(Collectors.toList());
        return wrapperHouseResult(houseIdList, func);
    }

    private List<HouseDTO> wrapperHouseResult(List<Long> houseIdList, Function<List<Long>, List<House>> func){
        // 先从缓存中查找
        List<HouseDTO> houseRedisDTOS = redisHouseService.getByIds(houseIdList);
        if(houseIdList.size() == houseRedisDTOS.size()){
            return houseRedisDTOS;
        }
        // 换种命中的id
        List<Long> cachedIds = houseRedisDTOS.stream().map(HouseDTO::getId).collect(Collectors.toList());
        // 获取未命中的房源id
        List<Long> unCachedIds = houseIdList.stream().filter(houseId -> !cachedIds.contains(houseId)).collect(Collectors.toList());
        // 如果未传原始房屋列表，则从数据库中查出所需房源信息， 否则直接过滤出未缓存的房源信息
        // 执行function函数，获取对应的未缓存结果
        List<House> unCachedHouse= func.apply(unCachedIds);

        List<HouseDTO> unCachedHouseDTOList = convertToHouseDTOList(unCachedHouse);
        // 包裹房源DTO
        wrapHouseDTOList(unCachedHouseDTOList);
        // 合并缓存与未缓存数据
        List<HouseDTO> result = mergeCachedAndUnCachedHouse(houseIdList, houseRedisDTOS, unCachedHouseDTOList);
        // 缓存未缓存过的房源DTO
        redisHouseService.addHouseDTOList(unCachedHouseDTOList);
        return result;
    }

    /**
     * 包裹房屋标签与房屋详情
     * @param houseDTOList 房屋dto列表
     */
    private void wrapHouseDTOList(List<HouseDTO> houseDTOList){
        Map<Long, HouseDTO> houseDTOMap = new HashMap<>();
        List<Long> houseIdList = new ArrayList<>();
        houseDTOList.forEach(houseDTO -> {
            houseDTOMap.put(houseDTO.getId(), houseDTO);
            houseIdList.add(houseDTO.getId());
        });
        List<HouseTag> houseTagList = houseTagRepository.findAllByHouseIdIn(houseIdList);
        // 房屋设置标签
        houseTagList.forEach(houseTag -> {
            HouseDTO houseDTO = houseDTOMap.get(houseTag.getHouseId());
            houseDTO.getTags().add(houseTag.getName());
        });
        // 房屋设置详情
        List<HouseDetail> houseDetailList = houseDetailRepository.findAllByHouseIdIn(houseIdList);
        houseDetailList.forEach(houseDetail -> {
            HouseDetailDTO detailDTO = modelMapper.map(houseDetail, HouseDetailDTO.class);
            houseDTOMap.get(detailDTO.getHouseId()).setHouseDetail(detailDTO);
        });

        // 房屋设置图片列表
        List<HousePicture> housePictureList = housePictureRepository.findAllByHouseIdIn(houseIdList);
        housePictureList.forEach(housePicture -> {
            HousePictureDTO pictureDTO = modelMapper.map(housePicture, HousePictureDTO.class);
            HouseDTO houseDTO = houseDTOMap.get(pictureDTO.getHouseId());
            houseDTO.getHousePictureList().add(pictureDTO);
        });
    }

    /**
     * 转换为houseDto列表
     * @param houses 分页查询结果
     */
    private List<HouseDTO> convertToHouseDTOList(List<House> houses){
        // 查询出房屋列表
        return houses.stream().map(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(cdnPrefix + houseDTO.getCover());
            return houseDTO;
        }).collect(Collectors.toList());
    }

    /**
     * 注入房屋详情信息
     */
    private HouseDetail generateHouseDetail(HouseForm houseForm){
        HouseDetail houseDetail = new HouseDetail();
        if(houseForm.getSubwayLineId() != null && houseForm.getSubwayStationId() != null){
            Subway subway = subwayRepository.findById(houseForm.getSubwayLineId()).orElseThrow(() -> new BusinessException(ApiResponseEnum.SUBWAY_LINE_ERROR));
            SubwayStation subwayStation = subwayStationRepository.findById(houseForm.getSubwayStationId()).orElseThrow(() -> new BusinessException(ApiResponseEnum.SUBWAY_STATION_ERROR));
            if(subway.getId().longValue() != subwayStation.getSubwayId().longValue()){
                throw new BusinessException(ApiResponseEnum.SUBWAY_AND_STATION_MATCH_ERROR);
            }
            houseDetail.setSubwayLineId(subway.getId());
            houseDetail.setSubwayLineName(subway.getName());
            houseDetail.setSubwayStationId(subwayStation.getId());
            houseDetail.setSubwayStationName(subwayStation.getName());
        }
        houseDetail.setAddress(houseForm.getAddress());
        houseDetail.setDescription(houseForm.getDescription());
        houseDetail.setLayoutDesc(houseForm.getLayoutDesc());
        houseDetail.setRentWay(houseForm.getRentWay());
        houseDetail.setTraffic(houseForm.getTraffic());
        houseDetail.setRoundService(houseForm.getRoundService());
        return houseDetail;
    }

    /**
     * 生成房屋照片列表
     */
    private List<HousePicture> generateHousePicture(HouseForm houseForm, Long houseId){
        List<HousePicture> housePictures = new ArrayList<>();
        if(CollectionUtils.isEmpty(houseForm.getPictures())){
            return housePictures;
        }
        List<PictureForm> photos = houseForm.getPictures();
        housePictures = photos.stream().map(item -> {
            HousePicture housePicture = new HousePicture();
            housePicture.setCdnPrefix(cdnPrefix);
            housePicture.setHouseId(houseId);
            housePicture.setPath(item.getPath());
            housePicture.setHeight(item.getHeight());
            housePicture.setWidth(item.getWidth());
            return housePicture;
        }).collect(Collectors.toList());
        return housePictures;
    }

    /**
     * 生成房屋标签集合
     */
    private List<HouseTag> generateHouseTag(HouseForm houseForm, Long houseId){
        if(CollectionUtils.isEmpty(houseForm.getTags())){
            return Collections.emptyList();
        }
        return houseForm.getTags().stream().map(tag -> {
            HouseTag houseTag = new HouseTag();
            houseTag.setName(tag);
            houseTag.setHouseId(houseId);
            return houseTag;
        }).collect(Collectors.toList());
    }

    private void checkCityAndRegion(String cityEnName, String regionEnName){
        addressService.findCityByName(cityEnName)
                .orElseThrow(() -> new BusinessException(ApiResponseEnum.ADDRESS_CITY_NOT_FOUND));
        addressService.findRegionByCityNameAndName(cityEnName, regionEnName)
                .orElseThrow(() -> new BusinessException(ApiResponseEnum.ADDRESS_REGION_NOT_FOUND));
    }

    /**
     * 按id顺序合并已缓存和未缓存的DTO
     * @param houseIdOrders 房源id顺序
     * @param cached 已缓存dto数据
     * @param unCached 未缓存dto数据
     * @return 合并后的dto集合
     */
    private List<HouseDTO> mergeCachedAndUnCachedHouse(List<Long> houseIdOrders,
                                                       List<HouseDTO> cached, List<HouseDTO> unCached){
        Map<Long, HouseDTO> cachedHouseDTOMap = cached.stream().collect(Collectors.toMap(HouseDTO::getId, houseDTO -> houseDTO, (k1, k2) -> k2));
        Map<Long, HouseDTO> houseDTOMap = unCached.stream().collect(Collectors.toMap(HouseDTO::getId, house -> house));
        // 合并缓存与未缓存房源集合
        Map<Long, HouseDTO> map = new HashMap<>();
        map.putAll(houseDTOMap);
        map.putAll(cachedHouseDTOMap);
        // 返回最终结果
        List<HouseDTO> result = new ArrayList<>();
        houseIdOrders.forEach(id -> result.add(map.get(id)));
        return result;
    }

    private


    @AllArgsConstructor
    @Data
    static class ListHouseSubscribeParams{

        private Long userId;

        private int status;

        private Pageable pageable;
    }
}