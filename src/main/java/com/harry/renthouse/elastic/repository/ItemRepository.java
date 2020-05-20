package com.harry.renthouse.elastic.repository;

import com.harry.renthouse.elastic.entity.HouseElastic;
import com.harry.renthouse.elastic.entity.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author Harry Xu
 * @date 2020/5/20 10:09
 */
public interface ItemRepository extends ElasticsearchRepository<Item, Long> {

}
