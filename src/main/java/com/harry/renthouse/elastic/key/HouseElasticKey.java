package com.harry.renthouse.elastic.key;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

/**
 * @author Harry Xu
 * @date 2020/5/21 14:54
 */
public class HouseElasticKey {

    public static final String HOUSE_ID = "houseId";

    public static final String TITLE = "title";

    public static final String PRICE = "price";

    public static final String AREA = "area";

    public static final String CREATE_TIME = "createTime";

    public static final String LAST_UPDATE_TIME = "lastUpdateTime";

    public static final String CITY_EN_NAME = "cityEnName";

    public static final String REGION_EN_NAME = "regionEnName";

    public static  final String DIRECTION = "direction";

    public static final String DISTANCE_TO_SUBWAY = "distanceToSubway";

    public static final String SUBWAY_LINE_NAME = "subwayLineName";

    public static final String SUBWAY_LINE_ID = "subwayLineId";

    public static final String SUBWAY_STATION_NAME = "subwayStationName";

    public static final String SUBWAY_STATION_ID = "subwayStationId";

    public static final String TAGS = "tags";

    public static final String STREET = "street";

    public static final String DISTRICT = "district";

    public static final String DESCRIPTION = "description";

    public static final String LAYOUT_DESC = "layoutDesc";

    public static final String TRAFFIC = "traffic";

    public static final String ROUND_SERVICE = "roundService";

    public static final String RENT_WAY = "rentWay";

    public static final String SUGGESTS = "suggests";

    public static final String AGG_DISTRICT_HOUSE = "aggDistrict";

    public static final String AGG_REGION_HOUSE = "aggRegionHouse";

    public static final String LOCATION = "location";
}
