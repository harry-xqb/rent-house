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

    public static String HOUSE_ID = "houseId";

    public static String TITLE = "title";

    public static String PRICE = "price";

    public static String AREA = "area";

    public static String CREATE_TIME = "createTime";

    public static String LAST_UPDATE_TIME = "lastUpdateTime";

    public static String CITY_EN_NAME = "cityEnName";

    public static String REGION_EN_NAME = "regionEnName";

    public static String DIRECTION = "direction";

    public static String DISTANCE_TO_SUBWAY = "distanceToSubway";

    public static String SUBWAY_LINE_NAME = "subwayLineName";

    public static String SUBWAY_STATION_NAME = "subwayStationName";

    public static String TAGS = "tags";

    public static String STREET = "street";

    public static String DISTRICT = "district";

    public static String DESCRIPTION = "description";

    public static String LAYOUT_DESC = "layoutDesc";

    public static String TRAFFIC = "traffic";

    public static String ROUND_SERVICE = "roundService";

    public static String RENT_WAY = "rentWay";

    public static String SUGGESTS = "suggests";

    public static String AGG_DISTRICT_HOUSE = "aggDistrict";

    public static final String AGG_REGION_HOUSE = "aggRegionHouse";

    public static final String LOCATION = "location";
}
