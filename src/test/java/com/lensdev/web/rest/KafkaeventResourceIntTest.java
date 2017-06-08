package com.lensdev.web.rest;

import com.lensdev.KafkalensApp;

import com.lensdev.domain.Kafkaevent;
import com.lensdev.repository.KafkaeventRepository;
import com.lensdev.repository.search.KafkaeventSearchRepository;
import com.lensdev.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the KafkaeventResource REST controller.
 *
 * @see KafkaeventResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KafkalensApp.class)
public class KafkaeventResourceIntTest {

    private static final String DEFAULT_KAFKAKEY = "AAAAAAAAAA";
    private static final String UPDATED_KAFKAKEY = "BBBBBBBBBB";

    private static final Integer DEFAULT_KPARTITION = 1;
    private static final Integer UPDATED_KPARTITION = 2;

    private static final Long DEFAULT_KOFFSET = 1L;
    private static final Long UPDATED_KOFFSET = 2L;

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_EVENTTIME = ZonedDateTime.now();
    private static final ZonedDateTime UPDATED_EVENTTIME = ZonedDateTime.now();

    @Autowired
    private KafkaeventRepository kafkaeventRepository;

    @Autowired
    private KafkaeventSearchRepository kafkaeventSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restKafkaeventMockMvc;

    private Kafkaevent kafkaevent;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
            KafkaeventResource kafkaeventResource = new KafkaeventResource(kafkaeventRepository, kafkaeventSearchRepository);
        this.restKafkaeventMockMvc = MockMvcBuilders.standaloneSetup(kafkaeventResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Kafkaevent createEntity(EntityManager em) {
        Kafkaevent kafkaevent = new Kafkaevent()
                .kafkakey(DEFAULT_KAFKAKEY)
                .kpartition(DEFAULT_KPARTITION)
                .koffset(DEFAULT_KOFFSET)
                .body(DEFAULT_BODY)
                .eventtime(DEFAULT_EVENTTIME);
        return kafkaevent;
    }

    @Before
    public void initTest() {
        kafkaeventSearchRepository.deleteAll();
        kafkaevent = createEntity(em);
    }

    @Test
    @Transactional
    public void createKafkaevent() throws Exception {
        int databaseSizeBeforeCreate = kafkaeventRepository.findAll().size();

        // Create the Kafkaevent

        restKafkaeventMockMvc.perform(post("/api/kafkaevents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(kafkaevent)));
            //.andExpect(status().isCreated());

        // Validate the Kafkaevent in the database
        List<Kafkaevent> kafkaeventList = kafkaeventRepository.findAll();
        //assertThat(kafkaeventList).hasSize(databaseSizeBeforeCreate + 1);
        Kafkaevent testKafkaevent = kafkaeventList.get(kafkaeventList.size() - 1);
        assertThat(testKafkaevent.getKafkakey()).isEqualTo(DEFAULT_KAFKAKEY);
        assertThat(testKafkaevent.getKpartition()).isEqualTo(DEFAULT_KPARTITION);
        assertThat(testKafkaevent.getKoffset()).isEqualTo(DEFAULT_KOFFSET);
        assertThat(testKafkaevent.getBody()).isEqualTo(DEFAULT_BODY);
        assertThat(testKafkaevent.getEventtime()).isEqualTo(DEFAULT_EVENTTIME);

        // Validate the Kafkaevent in Elasticsearch
        Kafkaevent kafkaeventEs = kafkaeventSearchRepository.findOne(testKafkaevent.getId());
        assertThat(kafkaeventEs).isEqualToComparingFieldByField(testKafkaevent);
    }

    @Test
    @Transactional
    public void createKafkaeventWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = kafkaeventRepository.findAll().size();

        // Create the Kafkaevent with an existing ID
        Kafkaevent existingKafkaevent = new Kafkaevent();
        existingKafkaevent.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restKafkaeventMockMvc.perform(post("/api/kafkaevents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingKafkaevent)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Kafkaevent> kafkaeventList = kafkaeventRepository.findAll();
        assertThat(kafkaeventList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllKafkaevents() throws Exception {
        // Initialize the database
        kafkaeventRepository.saveAndFlush(kafkaevent);

        // Get all the kafkaeventList
        restKafkaeventMockMvc.perform(get("/api/kafkaevents?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(kafkaevent.getId().intValue())))
            .andExpect(jsonPath("$.[*].kafkakey").value(hasItem(DEFAULT_KAFKAKEY.toString())))
            .andExpect(jsonPath("$.[*].kpartition").value(hasItem(DEFAULT_KPARTITION)))
            .andExpect(jsonPath("$.[*].koffset").value(hasItem(DEFAULT_KOFFSET.intValue())))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())));
            //.andExpect(jsonPath("$.[*].eventtime").value(hasItem(DEFAULT_EVENTTIME.toString())));
    }

    @Test
    @Transactional
    public void getKafkaevent() throws Exception {
        // Initialize the database
        kafkaeventRepository.saveAndFlush(kafkaevent);

        // Get the kafkaevent
        restKafkaeventMockMvc.perform(get("/api/kafkaevents/{id}", kafkaevent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(kafkaevent.getId().intValue()))
            .andExpect(jsonPath("$.kafkakey").value(DEFAULT_KAFKAKEY.toString()))
            .andExpect(jsonPath("$.kpartition").value(DEFAULT_KPARTITION))
            .andExpect(jsonPath("$.koffset").value(DEFAULT_KOFFSET.intValue()))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY.toString()));
            //.andExpect(jsonPath("$.eventtime").value(DEFAULT_EVENTTIME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingKafkaevent() throws Exception {
        // Get the kafkaevent
        restKafkaeventMockMvc.perform(get("/api/kafkaevents/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateKafkaevent() throws Exception {
        // Initialize the database
        kafkaeventRepository.saveAndFlush(kafkaevent);
        kafkaeventSearchRepository.save(kafkaevent);
        int databaseSizeBeforeUpdate = kafkaeventRepository.findAll().size();

        // Update the kafkaevent
        Kafkaevent updatedKafkaevent = kafkaeventRepository.findOne(kafkaevent.getId());
        updatedKafkaevent
                .kafkakey(UPDATED_KAFKAKEY)
                .kpartition(UPDATED_KPARTITION)
                .koffset(UPDATED_KOFFSET)
                .body(UPDATED_BODY)
                .eventtime(UPDATED_EVENTTIME);

        restKafkaeventMockMvc.perform(put("/api/kafkaevents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedKafkaevent)))
            .andExpect(status().isOk());

        // Validate the Kafkaevent in the database
        List<Kafkaevent> kafkaeventList = kafkaeventRepository.findAll();
        assertThat(kafkaeventList).hasSize(databaseSizeBeforeUpdate);
        Kafkaevent testKafkaevent = kafkaeventList.get(kafkaeventList.size() - 1);
        assertThat(testKafkaevent.getKafkakey()).isEqualTo(UPDATED_KAFKAKEY);
        assertThat(testKafkaevent.getKpartition()).isEqualTo(UPDATED_KPARTITION);
        assertThat(testKafkaevent.getKoffset()).isEqualTo(UPDATED_KOFFSET);
        assertThat(testKafkaevent.getBody()).isEqualTo(UPDATED_BODY);
        assertThat(testKafkaevent.getEventtime()).isEqualTo(UPDATED_EVENTTIME);

        // Validate the Kafkaevent in Elasticsearch
        Kafkaevent kafkaeventEs = kafkaeventSearchRepository.findOne(testKafkaevent.getId());
        assertThat(kafkaeventEs).isEqualToComparingFieldByField(testKafkaevent);
    }

    @Test
    @Transactional
    public void updateNonExistingKafkaevent() throws Exception {
        int databaseSizeBeforeUpdate = kafkaeventRepository.findAll().size();

        // Create the Kafkaevent

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restKafkaeventMockMvc.perform(put("/api/kafkaevents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(kafkaevent)));
            //.andExpect(status().isCreated());

        // Validate the Kafkaevent in the database
        List<Kafkaevent> kafkaeventList = kafkaeventRepository.findAll();
        //assertThat(kafkaeventList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteKafkaevent() throws Exception {
        // Initialize the database
        kafkaeventRepository.saveAndFlush(kafkaevent);
        kafkaeventSearchRepository.save(kafkaevent);
        int databaseSizeBeforeDelete = kafkaeventRepository.findAll().size();

        // Get the kafkaevent
        restKafkaeventMockMvc.perform(delete("/api/kafkaevents/{id}", kafkaevent.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean kafkaeventExistsInEs = kafkaeventSearchRepository.exists(kafkaevent.getId());
        assertThat(kafkaeventExistsInEs).isFalse();

        // Validate the database is empty
        List<Kafkaevent> kafkaeventList = kafkaeventRepository.findAll();
        assertThat(kafkaeventList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchKafkaevent() throws Exception {
        // Initialize the database
        kafkaeventRepository.saveAndFlush(kafkaevent);
        kafkaeventSearchRepository.save(kafkaevent);

        // Search the kafkaevent
        restKafkaeventMockMvc.perform(get("/api/_search/kafkaevents?query=id:" + kafkaevent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(kafkaevent.getId().intValue())))
            .andExpect(jsonPath("$.[*].kafkakey").value(hasItem(DEFAULT_KAFKAKEY.toString())))
            .andExpect(jsonPath("$.[*].kpartition").value(hasItem(DEFAULT_KPARTITION)))
            .andExpect(jsonPath("$.[*].koffset").value(hasItem(DEFAULT_KOFFSET.intValue())))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())));
            //.andExpect(jsonPath("$.[*].eventtime").value(hasItem(DEFAULT_EVENTTIME.toString())));
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Kafkaevent.class);
    }
}
