package com.harry.renthouse.service.search.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.base.HouseSortOrderByEnum;
import com.harry.renthouse.elastic.entity.BaiduMapLocation;
import com.harry.renthouse.elastic.entity.HouseElastic;
import com.harry.renthouse.elastic.entity.HouseKafkaMessage;
import com.harry.renthouse.elastic.entity.HouseSuggestion;
import com.harry.renthouse.elastic.key.HouseElasticKey;
import com.harry.renthouse.elastic.repository.HouseElasticRepository;
import com.harry.renthouse.entity.House;
import com.harry.renthouse.entity.HouseDetail;
import com.harry.renthouse.entity.HouseTag;
import com.harry.renthouse.entity.SupportAddress;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.repository.HouseDetailRepository;
import com.harry.renthouse.repository.HouseRepository;
import com.harry.renthouse.repository.HouseTagRepository;
import com.harry.renthouse.repository.SupportAddressRepository;
import com.harry.renthouse.service.ServiceMultiResult;
import com.harry.renthouse.service.house.AddressService;
import com.harry.renthouse.service.search.HouseElasticSearchService;
import com.harry.renthouse.web.dto.HouseBucketDTO;
import com.harry.renthouse.web.form.MapBoundSearchForm;
import com.harry.renthouse.web.form.MapSearchForm;
import com.harry.renthouse.web.form.SearchHouseForm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Harry Xu
 * @date 2020/5/20 13:35
 */
@Service
@Slf4j
public class HouseElasticSearchServiceImpl implements HouseElasticSearchService {

    private static final String HOUSE_INDEX_TOPIC = "HOUSE_INDEX_TOPIC";

    private static final String IK_SMART = "IK_SMART";

    private static final String INDEX_NAME = "rent-house";

    public static final int DEFAULT_SUGGEST_SIZE = 5;

    @Value("${qiniu.cdnPrefix}")
    private String cdnPrefix;


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

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private SupportAddressRepository supportAddressRepository;

    @Resource
    private AddressService addressService;

    @KafkaListener(topics = HOUSE_INDEX_TOPIC)
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
        // 设置推荐词
        updateSuggests(houseElastic);
        // 设置经纬度
        SupportAddress city = supportAddressRepository.findByEnNameAndLevel(houseElastic.getCityEnName(), SupportAddress.AddressLevel.CITY.getValue())
                .orElseThrow(() -> new BusinessException(ApiResponseEnum.ADDRESS_CITY_NOT_FOUND));
        SupportAddress region = supportAddressRepository.findByEnNameAndLevel(houseElastic.getRegionEnName(), SupportAddress.AddressLevel.REGION.getValue())
                .orElseThrow(() -> new BusinessException(ApiResponseEnum.ADDRESS_REGION_NOT_FOUND));
        String address = city.getCnName() + region.getCnName() + houseElastic.getAddress();
        BaiduMapLocation location = addressService.getBaiduMapLocation(city.getCnName(), address).orElse(null);
        houseElastic.setLocation(location);
        // 存储至elastic中
        houseElasticRepository.save(houseElastic);
        // 上传poi数据
        String lbsTitle = house.getStreet() + house.getDistrict();
        String lbsAddress = city.getCnName() + region.getCnName() + house.getStreet() + house.getDistrict();
        String cover = cdnPrefix + house.getCover();
        addressService.lbsUpload(houseElastic.getLocation(),
                lbsTitle, lbsAddress, houseId, houseElastic.getPrice(), houseElastic.getArea(), cover);
    }

    private void updateSuggests(HouseElastic houseElastic){
        // 对关键词进行分析
        // todo 需要对相关描述进行分词
        /*AnalyzeRequestBuilder analyzeRequestBuilder = new AnalyzeRequestBuilder(
                elasticsearchClient,
                AnalyzeAction.INSTANCE, INDEX_NAME,
                houseElastic.getTitle(), houseElastic.getLayoutDesc(),
                houseElastic.getRoundService(),
                houseElastic.getDescription());
        analyzeRequestBuilder.setAnalyzer(IK_SMART);
        // 处理分析结果
        AnalyzeResponse response = analyzeRequestBuilder.get();
        List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
        if(tokens == null){
            log.warn("无法对当前房源关键词进行分析:" + houseElastic);
            throw new BusinessException(ApiResponseEnum.ELASTIC_HOUSE_SUGGEST_CREATE_ERROR);
        }
        List<HouseSuggestion> suggestionList = tokens.stream().filter(token -> !StringUtils.equals("<NUM>", token.getType())
                && StringUtils.isNotBlank(token.getTerm()) && token.getTerm().length() > 2).map(item -> {
            HouseSuggestion houseSuggestion = new HouseSuggestion();
            houseSuggestion.setInput(item.getTerm());
            return houseSuggestion;
        }).collect(Collectors.toList());
        log.debug("包括对象------------------------");*/
        List<HouseSuggestion> suggestionList = new ArrayList<>();
        suggestionList.add(new HouseSuggestion(houseElastic.getTitle(), 30));
        suggestionList.add(new HouseSuggestion(houseElastic.getDistrict(), 20));
        suggestionList.add(new HouseSuggestion(houseElastic.getSubwayLineName(), 15));
        suggestionList.add(new HouseSuggestion(houseElastic.getSubwayStationName(), 15));
        houseElastic.setSuggests(suggestionList);
    }


    private void kafkaDelete(HouseKafkaMessage houseKafkaMessage){
        Long houseId = houseKafkaMessage.getId();
        houseElasticRepository.findById(houseId).orElseThrow(() -> new BusinessException(ApiResponseEnum.ELASTIC_HOUSE_NOT_FOUND));
        houseElasticRepository.deleteById(houseId);
        // 移除POI数据
        addressService.lbsRemove(houseId);
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
        if(StringUtils.isNotBlank(searchHouseForm.getKeyword())){
            boolQueryBuilder.must(QueryBuilders.multiMatchQuery(searchHouseForm.getKeyword(),
                    HouseElasticKey.TITLE,
                    HouseElasticKey.TRAFFIC,
                    HouseElasticKey.DISTRICT,
                    HouseElasticKey.ROUND_SERVICE,
                    HouseElasticKey.SUBWAY_LINE_NAME,
                    HouseElasticKey.SUBWAY_STATION_NAME
            ));
        }
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

    @Override
    public ServiceMultiResult<String> suggest(String prefix) {
        return suggest(prefix, DEFAULT_SUGGEST_SIZE);
    }

    @Override
    public ServiceMultiResult<String> suggest(String prefix, int size) {
        // 构建推荐查询
        CompletionSuggestionBuilder suggestionBuilder = SuggestBuilders.completionSuggestion(HouseElasticKey.SUGGESTS)
                .prefix(prefix).size(size);
        SuggestBuilder suggestBuilders = new SuggestBuilder();
        suggestBuilders.addSuggestion("autoComplete", suggestionBuilder);
        // 获取查询响应结果
        SearchResponse response = elasticsearchRestTemplate.suggest(suggestBuilders, HouseElastic.class);
        Suggest suggest = response.getSuggest();
        Suggest.Suggestion result = suggest.getSuggestion("autoComplete");

        // 构造推荐结果集
        Set<String> suggestSet = new HashSet<>();
        for (Object term : result.getEntries()) {
            if(term instanceof CompletionSuggestion.Entry){
                CompletionSuggestion.Entry item = (CompletionSuggestion.Entry) term;
                // 如果option不为空
                if(!CollectionUtils.isEmpty(item.getOptions())){
                    for (CompletionSuggestion.Entry.Option option : item.getOptions()) {
                        String tip = option.getText().string();
                        suggestSet.add(tip);
                        if(suggestSet.size() >= size){
                            break;
                        }
                    }
                }
            }
            if(suggestSet.size() >= size){
                break;
            }
        }
        List<String> list = Arrays.asList(suggestSet.toArray(new String[0]));
        return new ServiceMultiResult<>(list.size(), list);
    }

    @Override
    public int aggregateDistrictHouse(String cityEnName, String regionEnName, String district) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 过滤出当前小区
        boolQueryBuilder.filter( QueryBuilders.termQuery(HouseElasticKey.CITY_EN_NAME, cityEnName))
                .filter(QueryBuilders.termQuery(HouseElasticKey.REGION_EN_NAME, regionEnName))
                .filter(QueryBuilders.termQuery(HouseElasticKey.DISTRICT, district))
        ;
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
        /*// 添加聚合条件
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(HouseElasticKey.AGGS_DISTRICT_HOUSE)
        .field(HouseElasticKey.DISTRICT));
        // 过滤不包括任何字段
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));

        AggregatedPage<HouseElastic> houseAggPage = (AggregatedPage<HouseElastic>)houseElasticRepository.search(nativeSearchQueryBuilder.build());

        ParsedStringTerms houseTerm =(ParsedStringTerms) houseAggPage.getAggregation(HouseElasticKey.AGGS_DISTRICT_HOUSE);

        List<? extends Terms.Bucket> buckets = houseTerm.getBuckets();*/
        NativeSearchQuery query = nativeSearchQueryBuilder.build();
        log.debug(query.getQuery().toString());
        Page<HouseElastic> result = houseElasticRepository.search(query);
        return result.getSize();
    }

    @Override
    public ServiceMultiResult<HouseBucketDTO> mapAggregateRegionsHouse(String cityEnName) {
        // 过滤出当前城市数据
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.termQuery(HouseElasticKey.CITY_EN_NAME, cityEnName));
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);

        // 根据区县进行聚合
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(HouseElasticKey.AGG_REGION_HOUSE)
                .field(HouseElasticKey.REGION_EN_NAME));
        // 过滤不包括任何字段
//        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));

        NativeSearchQuery query = nativeSearchQueryBuilder.build();

        log.debug(query.getQuery().toString());
        log.debug(query.getAggregations().toString());
        Page<HouseElastic> response = houseElasticRepository.search(query);

        AggregatedPage<HouseElastic> aggResult = (AggregatedPage<HouseElastic>)response;

        ParsedStringTerms houseTerm =(ParsedStringTerms) aggResult.getAggregation(HouseElasticKey.AGG_REGION_HOUSE);

        List<? extends Terms.Bucket> termsBuckets = houseTerm.getBuckets();

        List<HouseBucketDTO> houseBucketDTOS = termsBuckets.stream().map(item -> new HouseBucketDTO(((Terms.Bucket) item).getKeyAsString(), ((Terms.Bucket) item).getDocCount()))
                .collect(Collectors.toList());

        return new ServiceMultiResult<>(aggResult.getSize(), houseBucketDTOS);
    }

    @Override
    public ServiceMultiResult<Long> mapBoundSearch(MapBoundSearchForm mapBoundSearchForm) {
        // 过滤城市
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.termQuery(HouseElasticKey.CITY_EN_NAME, mapBoundSearchForm.getCityEnName()));
        // 过滤视野范围
        boolQueryBuilder.filter(QueryBuilders.geoBoundingBoxQuery(HouseElasticKey.LOCATION)
            .setCorners(new GeoPoint(mapBoundSearchForm.getLeftTopLatitude(), mapBoundSearchForm.getLeftTopLongitude()),
                    new GeoPoint(mapBoundSearchForm.getRightBottomLatitude(), mapBoundSearchForm.getRightBottomLongitude()))
        );
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(boolQueryBuilder);
        queryBuilder.withSort(SortBuilders.fieldSort(HouseSortOrderByEnum
                .from(mapBoundSearchForm.getOrderBy())
                .orElse(HouseSortOrderByEnum.DEFAULT).getValue())
                .order(SortOrder.fromString(mapBoundSearchForm.getOrderDirection())));
        Pageable pageable = PageRequest.of(mapBoundSearchForm.getPage() - 1, mapBoundSearchForm.getPageSize());
        queryBuilder.withPageable(pageable);
        NativeSearchQuery query = queryBuilder.build();
        log.debug(query.getQuery().toString());
        Page<HouseElastic> result = houseElasticRepository.search(query);
        return new ServiceMultiResult<>(result.getSize(), result.getContent().stream().map(HouseElastic:: getHouseId).collect(Collectors.toList()));
    }


}
