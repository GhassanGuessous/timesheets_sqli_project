package com.sqli.imputation.web.rest;

import com.sqli.imputation.ImputationSqliApp;

import com.sqli.imputation.domain.CollaboratorMonthlyImputation;
import com.sqli.imputation.repository.CollaboratorMonthlyImputationRepository;
import com.sqli.imputation.service.CollaboratorMonthlyImputationService;
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
 * Test class for the CollaboratorMonthlyImputationResource REST controller.
 *
 * @see CollaboratorMonthlyImputationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ImputationSqliApp.class)
public class CollaboratorMonthlyImputationResourceIntTest {

    private static final Double DEFAULT_TOTAL = 1D;
    private static final Double UPDATED_TOTAL = 2D;

    @Autowired
    private CollaboratorMonthlyImputationRepository collaboratorMonthlyImputationRepository;

    @Autowired
    private CollaboratorMonthlyImputationService collaboratorMonthlyImputationService;

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

    private MockMvc restCollaboratorMonthlyImputationMockMvc;

    private CollaboratorMonthlyImputation collaboratorMonthlyImputation;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CollaboratorMonthlyImputationResource collaboratorMonthlyImputationResource = new CollaboratorMonthlyImputationResource(collaboratorMonthlyImputationService);
        this.restCollaboratorMonthlyImputationMockMvc = MockMvcBuilders.standaloneSetup(collaboratorMonthlyImputationResource)
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
    public static CollaboratorMonthlyImputation createEntity(EntityManager em) {
        CollaboratorMonthlyImputation collaboratorMonthlyImputation = new CollaboratorMonthlyImputation()
            .total(DEFAULT_TOTAL);
        return collaboratorMonthlyImputation;
    }

    @Before
    public void initTest() {
        collaboratorMonthlyImputation = createEntity(em);
    }

    @Test
    @Transactional
    public void createCollaboratorMonthlyImputation() throws Exception {
        int databaseSizeBeforeCreate = collaboratorMonthlyImputationRepository.findAll().size();

        // Create the CollaboratorMonthlyImputation
        restCollaboratorMonthlyImputationMockMvc.perform(post("/api/collaborator-monthly-imputations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(collaboratorMonthlyImputation)))
            .andExpect(status().isCreated());

        // Validate the CollaboratorMonthlyImputation in the database
        List<CollaboratorMonthlyImputation> collaboratorMonthlyImputationList = collaboratorMonthlyImputationRepository.findAll();
        assertThat(collaboratorMonthlyImputationList).hasSize(databaseSizeBeforeCreate + 1);
        CollaboratorMonthlyImputation testCollaboratorMonthlyImputation = collaboratorMonthlyImputationList.get(collaboratorMonthlyImputationList.size() - 1);
        assertThat(testCollaboratorMonthlyImputation.getTotal()).isEqualTo(DEFAULT_TOTAL);
    }

    @Test
    @Transactional
    public void createCollaboratorMonthlyImputationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = collaboratorMonthlyImputationRepository.findAll().size();

        // Create the CollaboratorMonthlyImputation with an existing ID
        collaboratorMonthlyImputation.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCollaboratorMonthlyImputationMockMvc.perform(post("/api/collaborator-monthly-imputations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(collaboratorMonthlyImputation)))
            .andExpect(status().isBadRequest());

        // Validate the CollaboratorMonthlyImputation in the database
        List<CollaboratorMonthlyImputation> collaboratorMonthlyImputationList = collaboratorMonthlyImputationRepository.findAll();
        assertThat(collaboratorMonthlyImputationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllCollaboratorMonthlyImputations() throws Exception {
        // Initialize the database
        collaboratorMonthlyImputationRepository.saveAndFlush(collaboratorMonthlyImputation);

        // Get all the collaboratorMonthlyImputationList
        restCollaboratorMonthlyImputationMockMvc.perform(get("/api/collaborator-monthly-imputations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(collaboratorMonthlyImputation.getId().intValue())))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL)));
    }
    
    @Test
    @Transactional
    public void getCollaboratorMonthlyImputation() throws Exception {
        // Initialize the database
        collaboratorMonthlyImputationRepository.saveAndFlush(collaboratorMonthlyImputation);

        // Get the collaboratorMonthlyImputation
        restCollaboratorMonthlyImputationMockMvc.perform(get("/api/collaborator-monthly-imputations/{id}", collaboratorMonthlyImputation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(collaboratorMonthlyImputation.getId().intValue()))
            .andExpect(jsonPath("$.total").value(DEFAULT_TOTAL));
    }

    @Test
    @Transactional
    public void getNonExistingCollaboratorMonthlyImputation() throws Exception {
        // Get the collaboratorMonthlyImputation
        restCollaboratorMonthlyImputationMockMvc.perform(get("/api/collaborator-monthly-imputations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCollaboratorMonthlyImputation() throws Exception {
        // Initialize the database
        collaboratorMonthlyImputationService.save(collaboratorMonthlyImputation);

        int databaseSizeBeforeUpdate = collaboratorMonthlyImputationRepository.findAll().size();

        // Update the collaboratorMonthlyImputation
        CollaboratorMonthlyImputation updatedCollaboratorMonthlyImputation = collaboratorMonthlyImputationRepository.findById(collaboratorMonthlyImputation.getId()).get();
        // Disconnect from session so that the updates on updatedCollaboratorMonthlyImputation are not directly saved in db
        em.detach(updatedCollaboratorMonthlyImputation);
        updatedCollaboratorMonthlyImputation
            .total(UPDATED_TOTAL);

        restCollaboratorMonthlyImputationMockMvc.perform(put("/api/collaborator-monthly-imputations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCollaboratorMonthlyImputation)))
            .andExpect(status().isOk());

        // Validate the CollaboratorMonthlyImputation in the database
        List<CollaboratorMonthlyImputation> collaboratorMonthlyImputationList = collaboratorMonthlyImputationRepository.findAll();
        assertThat(collaboratorMonthlyImputationList).hasSize(databaseSizeBeforeUpdate);
        CollaboratorMonthlyImputation testCollaboratorMonthlyImputation = collaboratorMonthlyImputationList.get(collaboratorMonthlyImputationList.size() - 1);
        assertThat(testCollaboratorMonthlyImputation.getTotal()).isEqualTo(UPDATED_TOTAL);
    }

    @Test
    @Transactional
    public void updateNonExistingCollaboratorMonthlyImputation() throws Exception {
        int databaseSizeBeforeUpdate = collaboratorMonthlyImputationRepository.findAll().size();

        // Create the CollaboratorMonthlyImputation

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCollaboratorMonthlyImputationMockMvc.perform(put("/api/collaborator-monthly-imputations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(collaboratorMonthlyImputation)))
            .andExpect(status().isBadRequest());

        // Validate the CollaboratorMonthlyImputation in the database
        List<CollaboratorMonthlyImputation> collaboratorMonthlyImputationList = collaboratorMonthlyImputationRepository.findAll();
        assertThat(collaboratorMonthlyImputationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteCollaboratorMonthlyImputation() throws Exception {
        // Initialize the database
        collaboratorMonthlyImputationService.save(collaboratorMonthlyImputation);

        int databaseSizeBeforeDelete = collaboratorMonthlyImputationRepository.findAll().size();

        // Delete the collaboratorMonthlyImputation
        restCollaboratorMonthlyImputationMockMvc.perform(delete("/api/collaborator-monthly-imputations/{id}", collaboratorMonthlyImputation.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<CollaboratorMonthlyImputation> collaboratorMonthlyImputationList = collaboratorMonthlyImputationRepository.findAll();
        assertThat(collaboratorMonthlyImputationList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CollaboratorMonthlyImputation.class);
        CollaboratorMonthlyImputation collaboratorMonthlyImputation1 = new CollaboratorMonthlyImputation();
        collaboratorMonthlyImputation1.setId(1L);
        CollaboratorMonthlyImputation collaboratorMonthlyImputation2 = new CollaboratorMonthlyImputation();
        collaboratorMonthlyImputation2.setId(collaboratorMonthlyImputation1.getId());
        assertThat(collaboratorMonthlyImputation1).isEqualTo(collaboratorMonthlyImputation2);
        collaboratorMonthlyImputation2.setId(2L);
        assertThat(collaboratorMonthlyImputation1).isNotEqualTo(collaboratorMonthlyImputation2);
        collaboratorMonthlyImputation1.setId(null);
        assertThat(collaboratorMonthlyImputation1).isNotEqualTo(collaboratorMonthlyImputation2);
    }
}
