package com.harry.renthouse.elastic.repository;

import com.harry.renthouse.RentHouseApplicationTests;
import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.elastic.entity.HouseElastic;
import com.harry.renthouse.elastic.entity.Item;
import com.harry.renthouse.entity.House;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.repository.HouseRepository;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Harry Xu
 * @date 2020/5/19 18:39
 */
 class HouseElasticRepositoryTest extends RentHouseApplicationTests {

    @Autowired
    private ElasticsearchTemplate elasticsearchRestTemplate;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private HouseElasticRepository houseElasticRepository;

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void addIndex(){
        elasticsearchRestTemplate.createIndex(Item.class);
        elasticsearchRestTemplate.putMapping(Item.class);
    }

    @Test
    public void deleteIndexTest(){
        elasticsearchRestTemplate.deleteIndex(Item.class);
    }

    @Test
    public void create(){
        List<Item> list = new ArrayList<>();
        list.add(new Item(2L, "坚果手机R1", " 手机", "锤子", 3699.00, "http://image.leyou.com/123.jpg"));
        list.add(new Item(3L, "华为META10", " 手机", "华为", 4499.00, "http://image.leyou.com/3.jpg"));
        // 接收对象集合，实现批量新增
        this.itemRepository.saveAll(list);
    }

    /**
     * 查询全部
     */
    @Test
    public void find() {
        Optional<Item> item = this.itemRepository.findById(2L);
        System.out.println("item.get() = " + item.get());
    }


    /**
     * 查询并排序
     */
    @Test
    public void findAllSort() {
        Iterable<Item> items = this.itemRepository.findAll(Sort.by("price").descending());
        items.forEach(System.out::println);
    }

    @Test
    public void HouseInsertTest() {
        House house = houseRepository.findById(25L).orElseThrow(() -> new BusinessException(ApiResponseEnum.HOUSE_NOT_FOUND_ERROR));
        HouseElastic houseElastic = modelMapper.map(house, HouseElastic.class);
        houseElasticRepository.save(houseElastic);
    }

    @Test
    public void addHouseIndex(){
        elasticsearchRestTemplate.createIndex(HouseElastic.class);
        elasticsearchRestTemplate.putMapping(HouseElastic.class);
    }
}