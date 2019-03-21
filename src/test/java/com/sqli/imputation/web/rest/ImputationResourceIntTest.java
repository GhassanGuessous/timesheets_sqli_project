package com.sqli.imputation.web.rest;

import com.sqli.imputation.ImputationSqliApp;

import com.sqli.imputation.domain.Imputation;
import com.sqli.imputation.repository.ImputationRepository;
import com.sqli.imputation.service.ImputationService;
import com.sqli.imputation.web.rest.errors.ExceptionTranslator;

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
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;


import static com.sqli.imputation.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ImputationResource REST controller.
 *
 * @see ImputationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ImputationSqliApp.class)
public class ImputationResourceIntTest {

    private static final Integer DEFAULT_MONTH = 1;
    private static final Integer UPDATED_MONTH = 2;

    private static final Integer DEFAULT_YEAR = 1;
    private static final Integer UPDATED_YEAR = 2;

    @Autowired
    private ImputationRepository imputationRepository;

    @Autowired
    private ImputationService imputationService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restImputationMockMvc;

    private Imputation imputation;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ImputationResource imputationResource = new ImputationResource(imputationService);
        this.restImputationMockMvc = MockMvcBuilders.standaloneSetup(imputationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Imputation createEntity(EntityManager em) {
        Imputation imputation = new Imputation()
            .month(DEFAULT_MONTH)
            .year(DEFAULT_YEAR);
        return imputation;
    }

    @Before
    public void initTest() {
        imputation = createEntity(em);
    }

    @Test
    @Transactional
    public void createImputation() throws Exception {
        int databaseSizeBeforeCreate = imputationRepository.findAll().size();

        // Create the Imputation
        restImputationMockMvc.perform(post("/api/imputations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imputation)))
            .andExpect(status().isCreated());

        // Validate the Imputation in the database
        List<Imputation> imputationList = imputationRepository.findAll();
        assertThat(imputationList).hasSize(databaseSizeBeforeCreate + 1);
        Imputation testImputation = imputationList.get(imputationList.size() - 1);
        assertThat(testImputation.getMonth()).isEqualTo(DEFAULT_MONTH);
        assertThat(testImputation.getYear()).isEqualTo(DEFAULT_YEAR);
    }

    @Test
    @Transactional
    public void createImputationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = imputationRepository.findAll().size();

        // Create the Imputation with an existing ID
        imputation.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restImputationMockMvc.perform(post("/api/imputations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imputation)))
            .andExpect(status().isBadRequest());

        // Validate the Imputation in the database
        List<Imputation> imputationList = imputationRepository.findAll();
        assertThat(imputationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllImputations() throws Exception {
        // Initialize the database
        imputationRepository.saveAndFlush(imputation);

        // Get all the imputationList
        restImputationMockMvc.perform(get("/api/imputations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(imputation.getId().intValue())))
            .andExpect(jsonPath("$.[*].month").value(hasItem(DEFAULT_MONTH)))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR)));
    }
    
    @Test
    @Transactional
    public void getImputation() throws Exception {
        // Initialize the database
        imputationRepository.saveAndFlush(imputation);

        // Get the imputation
        restImputationMockMvc.perform(get("/api/imputations/{id}", imputation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(imputation.getId().intValue()))
            .andExpect(jsonPath("$.month").value(DEFAULT_MONTH))
            .andExpect(jsonPath("$.year").value(DEFAULT_YEAR));
    }

    @Test
    @Transactional
    public void getNonExistingImputation() throws Exception {
        // Get the imputation
        restImputationMockMvc.perform(get("/api/imputations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateImputation() throws Exception {
        // Initialize the database
        imputationService.save(imputation);

        int databaseSizeBeforeUpdate = imputationRepository.findAll().size();

        // Update the imputation
        Imputation updatedImputation = imputationRepository.findById(imputation.getId()).get();
        // Disconnect from session so that the updates on updatedImputation are not directly saved in db
        em.detach(updatedImputation);
        updatedImputation
            .month(UPDATED_MONTH)
            .year(UPDATED_YEAR);

        restImputationMockMvc.perform(put("/api/imputations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedImputation)))
            .andExpect(status().isOk());

        // Validate the Imputation in the database
        List<Imputation> imputationList = imputationRepository.findAll();
        assertThat(imputationList).hasSize(databaseSizeBeforeUpdate);
        Imputation testImputation = imputationList.get(imputationList.size() - 1);
        assertThat(testImputation.getMonth()).isEqualTo(UPDATED_MONTH);
        assertThat(testImputation.getYear()).isEqualTo(UPDATED_YEAR);
    }

    @Test
    @Transactional
    public void updateNonExistingImputation() throws Exception {
        int databaseSizeBeforeUpdate = imputationRepository.findAll().size();

        // Create the Imputation

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImputationMockMvc.perform(put("/api/imputations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imputation)))
            .andExpect(status().isBadRequest());

        // Validate the Imputation in the database
        List<Imputation> imputationList = imputationRepository.findAll();
        assertThat(imputationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteImputation() throws Exception {
        // Initialize the database
        imputationService.save(imputation);

        int databaseSizeBeforeDelete = imputationRepository.findAll().size();

        // Delete the imputation
        restImputationMockMvc.perform(delete("/api/imputations/{id}", imputation.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Imputation> imputationList = imputationRepository.findAll();
        assertThat(imputationList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Imputation.class);
        Imputation imputation1 = new Imputation();
        imputation1.setId(1L);
        Imputation imputation2 = new Imputation();
        imputation2.setId(imputation1.getId());
        assertThat(imputation1).isEqualTo(imputation2);
        imputation2.setId(2L);
        assertThat(imputation1).isNotEqualTo(imputation2);
        imputation1.setId(null);
        assertThat(imputation1).isNotEqualTo(imputation2);
    }
}
