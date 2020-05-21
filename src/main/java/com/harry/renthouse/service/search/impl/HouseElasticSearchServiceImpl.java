package com.harry.renthouse.service.search.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.base.HouseSortOrderByEnum;
import com.harry.renthouse.elastic.entity.HouseElastic;
import com.harry.renthouse.elastic.entity.HouseKafkaMessage;
import com.harry.renthouse.elastic.key.HouseElasticKey;
import com.harry.renthouse.elastic.repository.HouseElasticRepository;
import com.harry.renthouse.entity.House;
import com.harry.renthouse.entity.HouseDetail;
import com.harry.renthouse.entity.HouseTag;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.repository.HouseDetailRepository;
import com.harry.renthouse.repository.HouseRepository;
import com.harry.renthouse.repository.HouseTagRepository;
import com.harry.renthouse.service.ServiceMultiResult;
import com.harry.renthouse.service.search.HouseElasticSearchService;
import com.harry.renthouse.web.form.SearchHouseForm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.SortBy;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Harry Xu
 * @date 2020/5/20 13:35
 */
@Service
@Slf4j
public class HouseElasticSearchServiceImpl implements HouseElasticSearchService {

    private static final String HOUSE_INDEX_TOPIC = "HOUSE_INDEX_TOPIC";

    @Resource
    private HouseElasticRepository houseElasticRepository;

    @Resource
    private HouseRepository houseRepository;

    @Resource
    private HouseDetailRepository houseDetailRepository;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private HouseTagRepository houseTagRepository;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource
    private Gson gson;

    @KafkaListener(topics = "HOUSE_INDEX_TOPIC")
    public void handleMessage(String message){
        try{
            HouseKafkaMessage houseKafkaMessage = gson.fromJson(message, HouseKafkaMessage.class);
            switch (houseKafkaMessage.getOperation()){
                case HouseKafkaMessage.INDEX:
                    kafkaSave(houseKafkaMessage);
                    break;
                case HouseKafkaMessage.DELETE:
                    kafkaDelete(houseKafkaMessage);
                    break;
                default:
            }
        }catch (JsonSyntaxException e){
            log.error("解析消息体失败: {}", message, e);
        }
    }

    private void kafkaSave(HouseKafkaMessage houseKafkaMessage){
        Long houseId = houseKafkaMessage.getId();
        HouseElastic houseElastic = new HouseElastic();
        House house = houseRepository.findById(houseId).orElseThrow(() -> new BusinessException(ApiResponseEnum.HOUSE_NOT_FOUND_ERROR));
        // 映射房屋数据
        modelMapper.map(house, houseElastic);
        // 映射房屋详情
        HouseDetail houseDetail = houseDetailRepository.findByHouseId(houseId).orElseThrow(() -> new BusinessException(ApiResponseEnum.HOUSE_DETAIL_NOT_FOUND_ERROR));
        modelMapper.map(houseDetail, houseElastic);
        // 映射标签信息
        List<HouseTag> tags = houseTagRepository.findAllByHouseId(houseId);
        List<String> tagList = tags.stream().map(HouseTag::getName).collect(Collectors.toList());
        houseElastic.setTags(tagList);
        // 存储至elastic中
        houseElasticRepository.save(houseElastic);
    }

    private void kafkaDelete(HouseKafkaMessage houseKafkaMessage){
        Long houseId = houseKafkaMessage.getId();
        houseElasticRepository.findById(houseId).orElseThrow(() -> new BusinessException(ApiResponseEnum.ELASTIC_HOUSE_NOT_FOUND));
        houseElasticRepository.deleteById(houseId);
    }

    @Override
    public void save(Long houseId) {
        HouseKafkaMessage houseKafkaMessage = new HouseKafkaMessage(houseId, HouseKafkaMessage.INDEX, 0);
        kafkaTemplate.send(HOUSE_INDEX_TOPIC, gson.toJson(houseKafkaMessage));
    }

    @Override
    public void delete(Long houseId) {
        HouseKafkaMessage houseKafkaMessage = new HouseKafkaMessage(houseId, HouseKafkaMessage.DELETE, 0);
        kafkaTemplate.send(HOUSE_INDEX_TOPIC, gson.toJson(houseKafkaMessage));
    }

    @Override
    public ServiceMultiResult<Long> search(SearchHouseForm searchHouseForm) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.termQuery(HouseElasticKey.CITY_EN_NAME, searchHouseForm.getCityEnName()));
        // 查询区域
        if(StringUtils.isNotBlank(searchHouseForm.getRegionEnName())){
            boolQueryBuilder.filter(QueryBuilders.termQuery(HouseElasticKey.REGION_EN_NAME, searchHouseForm.getRegionEnName()));
        }
        // 查询关键字
        boolQueryBuilder.must(QueryBuilders.multiMatchQuery(searchHouseForm.getKeyword(),
                HouseElasticKey.TITLE,
                HouseElasticKey.TRAFFIC,
                HouseElasticKey.DISTRICT,
                HouseElasticKey.ROUND_SERVICE,
                HouseElasticKey.SUBWAY_LINE_NAME,
                HouseElasticKey.SUBWAY_STATION_NAME
                ));
        // 查询面积区间
        if(searchHouseForm.getAreaMin() != null || searchHouseForm.getAreaMax() != null){
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(HouseElasticKey.AREA);
            if(searchHouseForm.getAreaMin() > 0){
                rangeQueryBuilder.gte(searchHouseForm.getAreaMin());
            }
            if(searchHouseForm.getAreaMax() > 0){
                rangeQueryBuilder.lte(searchHouseForm.getAreaMax());
            }
        }
        // 查询价格区间
        if(searchHouseForm.getPriceMin() != null || searchHouseForm.getPriceMax() != null){
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(HouseElasticKey.PRICE);
            if(searchHouseForm.getPriceMin() != null && searchHouseForm.getPriceMin() > 0){
                rangeQueryBuilder.gte(searchHouseForm.getPriceMin());
            }
            if(searchHouseForm.getPriceMax() != null && searchHouseForm.getPriceMax() > 0){
                rangeQueryBuilder.lte(searchHouseForm.getPriceMax());
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }
        // 房屋朝向
        if(searchHouseForm.getDirection() != null && searchHouseForm.getDirection() > 0){
            boolQueryBuilder.filter(QueryBuilders.termQuery(HouseElasticKey.DIRECTION, searchHouseForm.getDirection()));
        }
        // 出租方式
        if(searchHouseForm.getRentWay() != null && searchHouseForm.getRentWay() >= 0){
            boolQueryBuilder.filter(QueryBuilders.termQuery(HouseElasticKey.RENT_WAY, searchHouseForm.getRentWay()));
        }
        queryBuilder.withQuery(boolQueryBuilder);
        queryBuilder.withSort(SortBuilders.fieldSort(HouseSortOrderByEnum
                .from(searchHouseForm.getOrderBy())
                .orElse(HouseSortOrderByEnum.DEFAULT).getValue())
                .order(SortOrder.fromString(searchHouseForm.getSortDirection())));
        Pageable pageable = PageRequest.of(searchHouseForm.getPage() - 1, searchHouseForm.getPageSize());
        queryBuilder.withPageable(pageable);
        Page<HouseElastic> page = houseElasticRepository.search(queryBuilder.build());
        int total = (int) page.getTotalElements();
        List<Long> result = page.getContent().stream().map(HouseElastic::getHouseId).collect(Collectors.toList());
        return new ServiceMultiResult<>(total, result);
    }
}
