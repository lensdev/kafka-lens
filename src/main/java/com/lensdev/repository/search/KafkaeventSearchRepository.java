package com.lensdev.repository.search;

import com.lensdev.domain.Kafkaevent;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Kafkaevent entity.
 */
public interface KafkaeventSearchRepository extends ElasticsearchRepository<Kafkaevent, Long> {
}
