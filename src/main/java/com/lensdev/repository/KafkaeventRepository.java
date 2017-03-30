package com.lensdev.repository;

import com.lensdev.domain.Kafkaevent;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Kafkaevent entity.
 */
@SuppressWarnings("unused")
public interface KafkaeventRepository extends JpaRepository<Kafkaevent,Long> {

    List<Kafkaevent> findByKafkakey(String key);

}
