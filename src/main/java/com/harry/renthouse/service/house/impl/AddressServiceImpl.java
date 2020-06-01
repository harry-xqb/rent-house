package com.harry.renthouse.service.house.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.elastic.entity.BaiduMapLocation;
import com.harry.renthouse.entity.Subway;
import com.harry.renthouse.entity.SubwayStation;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.property.BaiduMapProperty;
import com.harry.renthouse.web.dto.SubwayDTO;
import com.harry.renthouse.web.dto.SubwayStationDTO;
import com.harry.renthouse.web.dto.SupportAddressDTO;
import com.harry.renthouse.entity.SupportAddress;
import com.harry.renthouse.repository.SubwayRepository;
import com.harry.renthouse.repository.SubwayStationRepository;
import com.harry.renthouse.repository.SupportAddressRepository;
import com.harry.renthouse.service.ServiceMultiResult;
import com.harry.renthouse.service.house.AddressService;
import com.sun.org.apache.bcel.internal.generic.NEW;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Harry Xu
 * @date 2020/5/8 17:24
 */
@Service
@Slf4j
public class AddressServiceImpl implements AddressService {

    @Resource
    private SupportAddressRepository supportAddressRepository;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private SubwayRepository subwayRepository;

    @Resource
    private SubwayStationRepository subwayStationRepository;

    @Resource
    private BaiduMapProperty baiduMapProperty;

    @Resource
    private Gson gson;
    /**
     * 获取所有城市
     * @return
     */
    @Override
    public ServiceMultiResult findAllCities() {
        Optional<List<SupportAddress>> addressList = Optional.ofNullable(supportAddressRepository.findAllByLevel(SupportAddress.AddressLevel.CITY.getValue()));
        List<SupportAddressDTO> list = addressList.orElse(Collections.emptyList()).stream()
                .map(address -> modelMapper.map(address, SupportAddressDTO.class))
                .collect(Collectors.toList());
        return new ServiceMultiResult<>(list.size(), list);
    }

    @Override
    public ServiceMultiResult<SupportAddressDTO> findAreaByBelongToAndLevel(String belongTo, String level) {
        SupportAddress.AddressLevel levelEnum = SupportAddress.AddressLevel.of(level);
        List<SupportAddressDTO> list = Optional.ofNullable(supportAddressRepository.findAllByBelongToAndLevel(belongTo, levelEnum.getValue()))
                .orElse(Collections.emptyList())
                .stream().map(address -> modelMapper.map(address, SupportAddressDTO.class))
                .collect(Collectors.toList());
        ;
        return new ServiceMultiResult<>(list.size(), list);
    }

    @Override
    public ServiceMultiResult<SupportAddressDTO> findAreaInEnName(List<String> enNameList) {
        List<SupportAddressDTO> list = Optional.ofNullable(supportAddressRepository.findAllByEnNameIn(enNameList))
                .orElse(Collections.emptyList())
                .stream().map(item -> modelMapper.map(item, SupportAddressDTO.class))
                .collect(Collectors.toList());
        return new ServiceMultiResult<>(list.size(), list);
    }

    @Override
    public ServiceMultiResult<SubwayDTO> findAllSubwayByCityEnName(String cityEnName) {
        List<SubwayDTO> subWayDtoList = Optional.ofNullable(subwayRepository.findAllByCityEnName(cityEnName))
                .orElse(Collections.emptyList()).stream().map(subway -> modelMapper.map(subway, SubwayDTO.class)).collect(Collectors.toList());
        return new ServiceMultiResult<>(subWayDtoList.size(), subWayDtoList);
    }

    @Override
    public ServiceMultiResult<SubwayStationDTO> findAllSubwayStationBySubwayId(Long subwayId) {
        List<SubwayStationDTO> subwayStationDTOList = Optional.ofNullable(subwayStationRepository
                .getAllBySubwayId(subwayId)).orElse(Collections.emptyList()).stream()
                .map(subwayStation -> modelMapper.map(subwayStation, SubwayStationDTO.class)).collect(Collectors.toList());
        return new ServiceMultiResult<>(subwayStationDTOList.size(), subwayStationDTOList);
    }

    @Override
    public Map<SupportAddress.AddressLevel, SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName) {
        SupportAddress city = supportAddressRepository.findByEnNameAndLevel(cityEnName, SupportAddress.AddressLevel.CITY.getValue())
                .orElseThrow(() -> new BusinessException(ApiResponseEnum.ADDRESS_CITY_NOT_FOUND));
        SupportAddress region = supportAddressRepository.findByEnNameAndLevel(regionEnName, SupportAddress.AddressLevel.REGION.getValue())
                .orElseThrow(() -> new BusinessException(ApiResponseEnum.ADDRESS_REGION_NOT_FOUND));
        Map<SupportAddress.AddressLevel, SupportAddressDTO> map = new HashMap();
        map.put(SupportAddress.AddressLevel.CITY, modelMapper.map(city, SupportAddressDTO.class));
        map.put(SupportAddress.AddressLevel.REGION, modelMapper.map(region, SupportAddressDTO.class));
        return map;
    }

    @Override
    public SubwayStationDTO findSubwayStation(Long subwayStationId) {
        SubwayStation subwayStation = subwayStationRepository.findById(subwayStationId).orElseThrow(() -> new BusinessException(ApiResponseEnum.SUBWAY_STATION_ERROR));
        return modelMapper.map(subwayStation, SubwayStationDTO.class);
    }

    @Override
    public SubwayDTO findSubway(Long subwayId) {
        Subway subway = subwayRepository.findById(subwayId).orElseThrow(() -> new BusinessException(ApiResponseEnum.SUBWAY_LINE_ERROR));
        return modelMapper.map(subway, SubwayDTO.class);
    }

    @Override
    public Optional<SupportAddressDTO> findCity(String cityEnName) {
        return supportAddressRepository.findByEnNameAndLevel(cityEnName, SupportAddress.AddressLevel.CITY.getValue())
                .map(item -> modelMapper.map(item, SupportAddressDTO.class));
    }

    @Override
    public Optional<BaiduMapLocation> getBaiduMapLocation(String cityCnName, String address) {
        String accessKey = baiduMapProperty.getAccessKey();
        String url = baiduMapProperty.getUrl();
        String encodeCity = "";
        String encodeAddress = "";
        try {
            encodeCity = URLEncoder.encode(cityCnName, "UTF-8");
            encodeAddress= URLEncoder.encode(address, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("编码房屋地址失败", e);
            return Optional.empty();
        }
        HttpClient httpClient = HttpClients.createDefault();
        StringBuilder sb = new StringBuilder(url);
        sb.append("address=").append(encodeAddress).append("&")
                .append("city=").append(encodeCity).append("&")
                .append("ak=").append(accessKey);
        log.debug(sb.toString());
        HttpGet httpGet = new HttpGet(sb.toString());
        try {
            HttpResponse response = httpClient.execute(httpGet);
            if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
                log.warn("获取房屋经纬度失败");
                return Optional.empty();
            }
            JsonObject result = gson.fromJson(EntityUtils.toString(response.getEntity(), "UTF-8"), JsonObject.class);
            if(result.get("status").getAsInt() != 0){
                log.warn("获取房屋经纬度响应状态失败:{}", result.get("message"));
                return Optional.empty();
            }
            BaiduMapLocation baiduMapLocation = new BaiduMapLocation();
            JsonObject location = result.get("result").getAsJsonObject().get("location").getAsJsonObject();
            baiduMapLocation.setLon(location.get("lng").getAsDouble());
            baiduMapLocation.setLat(location.get("lat").getAsDouble());
            return Optional.of(baiduMapLocation);
        } catch (IOException e) {
            log.error("http client io 异常", e);
        }
        return Optional.empty();
    }
}
