package com.sqli.imputation.web.rest;

import com.sqli.imputation.ImputationSqliApp;

import com.sqli.imputation.domain.CollaboratorDailyImputation;
import com.sqli.imputation.repository.CollaboratorDailyImputationRepository;
import com.sqli.imputation.service.CollaboratorDailyImputationService;
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
 * Test class for the CollaboratorDailyImputationResource REST controller.
 *
 * @see CollaboratorDailyImputationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ImputationSqliApp.class)
public class CollaboratorDailyImputationResourceIntTest {

    private static final Integer DEFAULT_DAY = 1;
    private static final Integer UPDATED_DAY = 2;

    private static final Integer DEFAULT_CHARGE = 1;
    private static final Integer UPDATED_CHARGE = 2;

    @Autowired
    private CollaboratorDailyImputationRepository collaboratorDailyImputationRepository;

    @Autowired
    private CollaboratorDailyImputationService collaboratorDailyImputationService;

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

    private MockMvc restCollaboratorDailyImputationMockMvc;

    private CollaboratorDailyImputation collaboratorDailyImputation;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CollaboratorDailyImputationResource collaboratorDailyImputationResource = new CollaboratorDailyImputationResource(collaboratorDailyImputationService);
        this.restCollaboratorDailyImputationMockMvc = MockMvcBuilders.standaloneSetup(collaboratorDailyImputationResource)
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
    public static CollaboratorDailyImputation createEntity(EntityManager em) {
        CollaboratorDailyImputation collaboratorDailyImputation = new CollaboratorDailyImputation()
            .day(DEFAULT_DAY)
            .charge(DEFAULT_CHARGE);
        return collaboratorDailyImputation;
    }

    @Before
    public void initTest() {
        collaboratorDailyImputation = createEntity(em);
    }

    @Test
    @Transactional
    public void createCollaboratorDailyImputation() throws Exception {
        int databaseSizeBeforeCreate = collaboratorDailyImputationRepository.findAll().size();

        // Create the CollaboratorDailyImputation
        restCollaboratorDailyImputationMockMvc.perform(post("/api/collaborator-daily-imputations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(collaboratorDailyImputation)))
            .andExpect(status().isCreated());

        // Validate the CollaboratorDailyImputation in the database
        List<CollaboratorDailyImputation> collaboratorDailyImputationList = collaboratorDailyImputationRepository.findAll();
        assertThat(collaboratorDailyImputationList).hasSize(databaseSizeBeforeCreate + 1);
        CollaboratorDailyImputation testCollaboratorDailyImputation = collaboratorDailyImputationList.get(collaboratorDailyImputationList.size() - 1);
        assertThat(testCollaboratorDailyImputation.getDay()).isEqualTo(DEFAULT_DAY);
        assertThat(testCollaboratorDailyImputation.getCharge()).isEqualTo(DEFAULT_CHARGE);
    }

    @Test
    @Transactional
    public void createCollaboratorDailyImputationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = collaboratorDailyImputationRepository.findAll().size();

        // Create the CollaboratorDailyImputation with an existing ID
        collaboratorDailyImputation.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCollaboratorDailyImputationMockMvc.perform(post("/api/collaborator-daily-imputations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(collaboratorDailyImputation)))
            .andExpect(status().isBadRequest());

        // Validate the CollaboratorDailyImputation in the database
        List<CollaboratorDailyImputation> collaboratorDailyImputationList = collaboratorDailyImputationRepository.findAll();
        assertThat(collaboratorDailyImputationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllCollaboratorDailyImputations() throws Exception {
        // Initialize the database
        collaboratorDailyImputationRepository.saveAndFlush(collaboratorDailyImputation);

        // Get all the collaboratorDailyImputationList
        restCollaboratorDailyImputationMockMvc.perform(get("/api/collaborator-daily-imputations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(collaboratorDailyImputation.getId().intValue())))
            .andExpect(jsonPath("$.[*].day").value(hasItem(DEFAULT_DAY)))
            .andExpect(jsonPath("$.[*].charge").value(hasItem(DEFAULT_CHARGE)));
    }
    
    @Test
    @Transactional
    public void getCollaboratorDailyImputation() throws Exception {
        // Initialize the database
        collaboratorDailyImputationRepository.saveAndFlush(collaboratorDailyImputation);

        // Get the collaboratorDailyImputation
        restCollaboratorDailyImputationMockMvc.perform(get("/api/collaborator-daily-imputations/{id}", collaboratorDailyImputation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(collaboratorDailyImputation.getId().intValue()))
            .andExpect(jsonPath("$.day").value(DEFAULT_DAY))
            .andExpect(jsonPath("$.charge").value(DEFAULT_CHARGE));
    }

    @Test
    @Transactional
    public void getNonExistingCollaboratorDailyImputation() throws Exception {
        // Get the collaboratorDailyImputation
        restCollaboratorDailyImputationMockMvc.perform(get("/api/collaborator-daily-imputations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCollaboratorDailyImputation() throws Exception {
        // Initialize the database
        collaboratorDailyImputationService.save(collaboratorDailyImputation);

        int databaseSizeBeforeUpdate = collaboratorDailyImputationRepository.findAll().size();

        // Update the collaboratorDailyImputation
        CollaboratorDailyImputation updatedCollaboratorDailyImputation = collaboratorDailyImputationRepository.findById(collaboratorDailyImputation.getId()).get();
        // Disconnect from session so that the updates on updatedCollaboratorDailyImputation are not directly saved in db
        em.detach(updatedCollaboratorDailyImputation);
        updatedCollaboratorDailyImputation
            .day(UPDATED_DAY)
            .charge(UPDATED_CHARGE);

        restCollaboratorDailyImputationMockMvc.perform(put("/api/collaborator-daily-imputations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCollaboratorDailyImputation)))
            .andExpect(status().isOk());

        // Validate the CollaboratorDailyImputation in the database
        List<CollaboratorDailyImputation> collaboratorDailyImputationList = collaboratorDailyImputationRepository.findAll();
        assertThat(collaboratorDailyImputationList).hasSize(databaseSizeBeforeUpdate);
        CollaboratorDailyImputation testCollaboratorDailyImputation = collaboratorDailyImputationList.get(collaboratorDailyImputationList.size() - 1);
        assertThat(testCollaboratorDailyImputation.getDay()).isEqualTo(UPDATED_DAY);
        assertThat(testCollaboratorDailyImputation.getCharge()).isEqualTo(UPDATED_CHARGE);
    }

    @Test
    @Transactional
    public void updateNonExistingCollaboratorDailyImputation() throws Exception {
        int databaseSizeBeforeUpdate = collaboratorDailyImputationRepository.findAll().size();

        // Create the CollaboratorDailyImputation

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCollaboratorDailyImputationMockMvc.perform(put("/api/collaborator-daily-imputations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(collaboratorDailyImputation)))
            .andExpect(status().isBadRequest());

        // Validate the CollaboratorDailyImputation in the database
        List<CollaboratorDailyImputation> collaboratorDailyImputationList = collaboratorDailyImputationRepository.findAll();
        assertThat(collaboratorDailyImputationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteCollaboratorDailyImputation() throws Exception {
        // Initialize the database
        collaboratorDailyImputationService.save(collaboratorDailyImputation);

        int databaseSizeBeforeDelete = collaboratorDailyImputationRepository.findAll().size();

        // Delete the collaboratorDailyImputation
        restCollaboratorDailyImputationMockMvc.perform(delete("/api/collaborator-daily-imputations/{id}", collaboratorDailyImputation.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<CollaboratorDailyImputation> collaboratorDailyImputationList = collaboratorDailyImputationRepository.findAll();
        assertThat(collaboratorDailyImputationList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CollaboratorDailyImputation.class);
        CollaboratorDailyImputation collaboratorDailyImputation1 = new CollaboratorDailyImputation();
        collaboratorDailyImputation1.setId(1L);
        CollaboratorDailyImputation collaboratorDailyImputation2 = new CollaboratorDailyImputation();
        collaboratorDailyImputation2.setId(collaboratorDailyImputation1.getId());
        assertThat(collaboratorDailyImputation1).isEqualTo(collaboratorDailyImputation2);
        collaboratorDailyImputation2.setId(2L);
        assertThat(collaboratorDailyImputation1).isNotEqualTo(collaboratorDailyImputation2);
        collaboratorDailyImputation1.setId(null);
        assertThat(collaboratorDailyImputation1).isNotEqualTo(collaboratorDailyImputation2);
    }
}
