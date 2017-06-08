package com.lensdev.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.lensdev.domain.Kafkaevent;

import com.lensdev.kafka.LensKafkaProducer;
import com.lensdev.repository.KafkaeventRepository;
import com.lensdev.repository.search.KafkaeventSearchRepository;
import com.lensdev.web.rest.util.HeaderUtil;
import com.lensdev.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Kafkaevent.
 */
@RestController
@RequestMapping("/api")
public class KafkaeventResource {

    private final Logger log = LoggerFactory.getLogger(KafkaeventResource.class);

    private static final String ENTITY_NAME = "kafkaevent";

    private final KafkaeventRepository kafkaeventRepository;

    private final KafkaeventSearchRepository kafkaeventSearchRepository;

    @Autowired
    LensKafkaProducer lensKafkaProducer;

    public KafkaeventResource(KafkaeventRepository kafkaeventRepository, KafkaeventSearchRepository kafkaeventSearchRepository) {
        this.kafkaeventRepository = kafkaeventRepository;
        this.kafkaeventSearchRepository = kafkaeventSearchRepository;
    }

    /**
     * POST  /kafkaevents : Create a new kafkaevent.
     *
     * @param kafkaevent the kafkaevent to create
     * @return the ResponseEntity with status 201 (Created) and with body the new kafkaevent, or with status 400 (Bad Request) if the kafkaevent has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/kafkaevents")
    @Timed
    public ResponseEntity<Kafkaevent> createKafkaevent(@RequestBody Kafkaevent kafkaevent) throws URISyntaxException {
        log.debug("REST request to save Kafkaevent : {}", kafkaevent);
        if (kafkaevent.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new kafkaevent cannot already have an ID")).body(null);
        }

        //Kafkaevent result = kafkaeventRepository.save(kafkaevent);
        //kafkaeventSearchRepository.save(result);
        Kafkaevent result = lensKafkaProducer.publish(kafkaevent);

        return ResponseEntity.created(new URI("/api/kafkaevents/" + result.getId()))
            //.headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getKafkakey().toString()))
            .body(result);
    }

    /**
     * PUT  /kafkaevents : Updates an existing kafkaevent.
     *
     * @param kafkaevent the kafkaevent to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated kafkaevent,
     * or with status 400 (Bad Request) if the kafkaevent is not valid,
     * or with status 500 (Internal Server Error) if the kafkaevent couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/kafkaevents")
    @Timed
    public ResponseEntity<Kafkaevent> updateKafkaevent(@RequestBody Kafkaevent kafkaevent) throws URISyntaxException {
        log.debug("REST request to update Kafkaevent : {}", kafkaevent);
        if (kafkaevent.getId() == null) {
            return createKafkaevent(kafkaevent);
        }
        Kafkaevent result = kafkaeventRepository.save(kafkaevent);
        kafkaeventSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, kafkaevent.getId().toString()))
            .body(result);
    }

    /**
     * GET  /kafkaevents : get all the kafkaevents.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of kafkaevents in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/kafkaevents")
    @Timed
    public ResponseEntity<List<Kafkaevent>> getAllKafkaevents(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Kafkaevents");
        Page<Kafkaevent> page = kafkaeventRepository.findAll(pageable);
        //Page<Kafkaevent> page = kafkaeventSearchRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/kafkaevents");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /kafkaevents/:id : get the "id" kafkaevent.
     *
     * @param id the id of the kafkaevent to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the kafkaevent, or with status 404 (Not Found)
     */
    @GetMapping("/kafkaevents/{id}")
    @Timed
    public ResponseEntity<Kafkaevent> getKafkaevent(@PathVariable Long id) {
        log.debug("REST request to get Kafkaevent : {}", id);
        Kafkaevent kafkaevent = kafkaeventRepository.findOne(id);
        //Kafkaevent kafkaevent = kafkaeventSearchRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kafkaevent));
    }

    /**
     * DELETE  /kafkaevents/:id : delete the "id" kafkaevent.
     *
     * @param id the id of the kafkaevent to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/kafkaevents/{id}")
    @Timed
    public ResponseEntity<Void> deleteKafkaevent(@PathVariable Long id) {
        log.debug("REST request to delete Kafkaevent : {}", id);
        kafkaeventRepository.delete(id);
        kafkaeventSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/kafkaevents?query=:query : search for the kafkaevent corresponding
     * to the query.
     *
     * @param query the query of the kafkaevent search
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/kafkaevents")
    @Timed
    public ResponseEntity<List<Kafkaevent>> searchKafkaevents(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Kafkaevents for query {}", query);
        Page<Kafkaevent> page = kafkaeventSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/kafkaevents");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
