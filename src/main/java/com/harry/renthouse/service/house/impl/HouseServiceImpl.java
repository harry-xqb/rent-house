package com.harry.renthouse.service.house.impl;

import com.harry.renthouse.base.*;
import com.harry.renthouse.entity.*;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.repository.*;
import com.harry.renthouse.service.ServiceMultiResult;
import com.harry.renthouse.service.house.AddressService;
import com.harry.renthouse.service.house.HouseService;
import com.harry.renthouse.service.house.QiniuService;
import com.harry.renthouse.service.search.HouseElasticSearchService;
import com.harry.renthouse.web.dto.*;
import com.harry.renthouse.web.form.*;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
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
    private QiniuService qiniuService;

    @Resource
    private SupportAddressRepository supportAddressRepository;

    @Resource
    private AddressService addressService;

    @Value("${qiniu.cdnPrefix}")
    private String cdnPrefix;

    @Resource
    private HouseElasticSearchService houseElasticSearchService;


    @Transactional
    @Override
    public HouseDTO addHouse(HouseForm houseForm) {
        // 房屋详情数据填充与校验
        HouseDetail houseDetail = generateHouseDetail(houseForm);
        // 新增房屋信息
        House house = modelMapper.map(houseForm, House.class);
        house.setAdminId(AuthenticatedUserUtil.getUserId());
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
        houseDTO.setTags(houseForm.getTags());
        return houseDTO;
    }

    @Override
    @Transactional
    public HouseDTO editHouse(HouseForm houseForm) {
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
        // 获取照片信息
        List<HousePicture> housePictures = housePictureRepository.saveAll(generateHousePicture(houseForm, houseId));
        List<HousePictureDTO> housePicturesDTO = housePictures.stream().map(picture -> modelMapper.map(picture, HousePictureDTO.class)).collect(Collectors.toList());
        // 获取标签信息
        List<HouseTag> houseTagList = houseTagRepository.findAllByHouseId(houseId);
        List<String> tagNameList = houseTagList.stream().map(HouseTag::getName).collect(Collectors.toList());
        // 填充房屋信息
        houseDTO.setHouseDetail(houseDetailDTO);
        houseDTO.setHousePictureList(housePicturesDTO);
        houseDTO.setTags(tagNameList);
        houseDTO.setCover(cdnPrefix + houseDTO.getCover());

        // 简历索引
        if(house.getStatus() == HouseStatusEnum.AUDIT_PASSED.getValue()){
            houseElasticSearchService.save(houseId);
        }
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
            // 如果城市不为空则将城市加入模糊查询
            if(StringUtils.isNotBlank(searchForm.getCity())){
                predicates.add(criteriaBuilder.like(root.get("city"), "%" + searchForm.getCity() + "%"));
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
        Sort sort = Sort.by(Sort.Direction.valueOf(searchForm.getDirection()), searchForm.getOrderBy());
        int page = searchForm.getPage() - 1;
        Pageable pageable = PageRequest.of(page, searchForm.getPageSize(), sort);
        Page<House> houses = houseRepository.findAll(querySpec, pageable);
        List<HouseDTO> houseDTOList = convertToHouseDTOList(houses);
        return new ServiceMultiResult<>((int)houses.getTotalElements(), houseDTOList);
    }

    @Override
    public HouseCompleteInfoDTO findCompleteHouse(Long houseId) {
        // 查找房屋信息
        House house = houseRepository.findById(houseId).orElseThrow(() -> new BusinessException(ApiResponseEnum.HOUSE_NOT_FOUND_ERROR));
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
        houseDTO.setTags(tagStringList);
        houseDTO.setHouseDetail(houseDetailDTO);
        houseDTO.setHousePictureList(housePictureDTO);
        // 设置地铁信息
        Map<SupportAddress.AddressLevel, SupportAddressDTO> cityAndRegion = addressService.findCityAndRegion(houseDTO.getCityEnName(), houseDTO.getRegionEnName());
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
        houseCompleteInfoDTO.setCity(cityAndRegion.get(SupportAddress.AddressLevel.CITY));
        houseCompleteInfoDTO.setRegion(cityAndRegion.get(SupportAddress.AddressLevel.REGION));
        houseCompleteInfoDTO.setSubway(subwayDTO);
        houseCompleteInfoDTO.setSubwayStation(subwayStationDTO);
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
    }

    @Override
    @Transactional
    public void deleteTag(TagForm tagForm) {
        houseRepository.findById(tagForm.getHouseId()).orElseThrow(() -> new BusinessException(ApiResponseEnum.HOUSE_NOT_FOUND_ERROR));
        HouseTag houseTag = houseTagRepository.findByNameAndHouseId(tagForm.getName(), tagForm.getHouseId())
                .orElseThrow(() -> new BusinessException(ApiResponseEnum.TAG_NOT_EXIST));
        houseTagRepository.delete(houseTag);
    }

    @Override
    @Transactional
    public void deletePicture(Long pictureId) {
        HousePicture housePicture = housePictureRepository.findById(pictureId).orElseThrow(() -> new BusinessException(ApiResponseEnum.PICTURE_NOT_EXIST));
        try {
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
        }
    }

    @Override
    @Transactional
    public void updateCover(Long coverId, Long houseId) {
        HousePicture housePicture = housePictureRepository.findById(coverId).orElseThrow(() -> new BusinessException(ApiResponseEnum.PICTURE_NOT_EXIST));
        House house = houseRepository.findById(houseId).orElseThrow(() -> new BusinessException(ApiResponseEnum.HOUSE_NOT_FOUND_ERROR));
        houseRepository.updateCover(house.getId(), housePicture.getPath());
    }

    @Override
    @Transactional
    public void updateStatus(Long houseId, HouseOperationEnum houseOperationEnum) {
        House house = houseRepository.findById(houseId).orElseThrow(() -> new BusinessException(ApiResponseEnum.HOUSE_NOT_FOUND_ERROR));
        int status = house.getStatus();
        if(status == houseOperationEnum.getCode()){
            throw new BusinessException(ApiResponseEnum.HOUSE_STATUS_NOT_CHANGE);
        }
        if(status == HouseStatusEnum.DELETED.getValue()){
            throw new BusinessException(ApiResponseEnum.HOUSE_STATUS_CHANGE_ERROR_DELETED);
        }
        if(status == HouseStatusEnum.RENTED.getValue()){
            throw new BusinessException(ApiResponseEnum.HOUSE_STATUS_CHANGE_ERROR_RENTED);
        }
        houseRepository.updateStatus(houseId, houseOperationEnum.getCode());
        // 如果房源更新为审核通过则建立
        if(houseOperationEnum.getCode() == HouseOperationEnum.PASS.getCode()){
            houseElasticSearchService.save(houseId);
        }else{
            houseElasticSearchService.delete(houseId);
        }
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
        // 如果是按关键字搜索
        if(StringUtils.isNotBlank(searchHouseForm.getKeyword())){
            ServiceMultiResult<Long> houseIdResult = houseElasticSearchService.search(searchHouseForm);
            if(houseIdResult.getTotal() == 0){
                return new ServiceMultiResult<>(0, Collections.emptyList());
            }
            return new ServiceMultiResult<>(houseIdResult.getTotal(), wrapperHouseResult(houseIdResult.getList()));
        }

        return simpleSearch(searchHouseForm);
    }

    @Override
    public ServiceMultiResult<HouseDTO> mapHouseSearch(MapSearchForm mapSearchForm) {
        SearchHouseForm searchHouseForm = new SearchHouseForm();
        searchHouseForm.setPage(mapSearchForm.getPage());
        searchHouseForm.setPageSize(mapSearchForm.getPageSize());
        searchHouseForm.setCityEnName(mapSearchForm.getCityEnName());
        searchHouseForm.setOrderBy(mapSearchForm.getOrderBy());
        searchHouseForm.setSortDirection(mapSearchForm.getOrderDirection());
        ServiceMultiResult<Long> result = houseElasticSearchService.search(searchHouseForm);
        return new ServiceMultiResult<>(result.getTotal(), wrapperHouseResult(result.getList()));
    }

    @Override
    public ServiceMultiResult<HouseDTO> mapBoundSearch(MapBoundSearchForm mapBoundSearchForm) {
        ServiceMultiResult<Long> houseIdResult = houseElasticSearchService.mapBoundSearch(mapBoundSearchForm);
        return new ServiceMultiResult<>(houseIdResult.getTotal(), wrapperHouseResult(houseIdResult.getList()));
    }

    /**
     * 通过id集合获取房源信息
     * @param houseIdList id集合列表
     * @return 房源dto列表
     */
    private List<HouseDTO> wrapperHouseResult(List<Long> houseIdList){
        List<House> houseList = houseRepository.findAllById(houseIdList);
        List<HouseDTO> houseDTOList = houseList.stream().map(item -> modelMapper.map(item, HouseDTO.class))
                .collect(Collectors.toList());
        Map<Long, HouseDTO> houseDTOMap = houseDTOList.stream().collect(Collectors.toMap(HouseDTO::getId, house -> house));
        wrapHouseDTOList(houseDTOList);
        List<HouseDTO> result = new ArrayList<>();
        houseIdList.forEach(id -> result.add(houseDTOMap.get(id)));
        return result;
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
                supportAddressRepository
                        .findByEnNameAndLevel(searchHouseForm.getRegionEnName(), SupportAddress.AddressLevel.REGION.getValue())
                        .orElseThrow(() -> new BusinessException(ApiResponseEnum.ADDRESS_REGION_NOT_FOUND));
                predicates.add(criteriaBuilder.equal(root.get("regionEnName"), searchHouseForm.getRegionEnName()));
            }
            // 如果地铁线路不为空，搜索地铁线路
            if(searchHouseForm.getSubwayLineId() != null){
                Subway subway = subwayRepository.findById(searchHouseForm.getSubwayLineId()).orElseThrow(() -> new BusinessException(ApiResponseEnum.SUBWAY_LINE_ERROR));
                predicates.add(criteriaBuilder.equal(root.get("subwayId"), searchHouseForm.getSubwayLineId()));
                // 如果地铁站不为空，搜索地铁站
                if(searchHouseForm.getSubwayStationId() != null){
                    SubwayStation subwayStation = subwayStationRepository.findById(searchHouseForm.getSubwayStationId()).orElseThrow(() -> new BusinessException(ApiResponseEnum.SUBWAY_STATION_ERROR));
                    if(subwayStation.getSubwayId().longValue() != subway.getId().longValue()){
                        throw new BusinessException(ApiResponseEnum.SUBWAY_AND_STATION_MATCH_ERROR);
                    }
                    predicates.add(criteriaBuilder.equal(root.get("subwayStationId"), searchHouseForm.getSubwayStationId()));
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
                List<Long> houseIdList = houseTagRepository.findAllByNameIn(searchHouseForm.getTags()).stream().map(HouseTag::getHouseId).collect(Collectors.toList());
             /*   Join<House, HouseTag> joinTag = root.join("houseId", JoinType.RIGHT);
                predicates.add(joinTag.get("name").in(searchHouseForm.getTags()));*/
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
        List<HouseDTO> houseDTOList = convertToHouseDTOList(houses);
        wrapHouseDTOList(houseDTOList);
        return new ServiceMultiResult<>((int) houses.getTotalElements(), houseDTOList);
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
    }

    /**
     * 转换为houseDto列表
     * @param houses 分页查询结果
     */
    private List<HouseDTO> convertToHouseDTOList(Page<House> houses){
        // 查询出房屋列表
        return houses.getContent().stream().map(house -> {
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
}
