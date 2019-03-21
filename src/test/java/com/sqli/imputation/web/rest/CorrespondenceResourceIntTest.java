package com.sqli.imputation.web.rest;

import com.sqli.imputation.ImputationSqliApp;

import com.sqli.imputation.domain.Correspondence;
import com.sqli.imputation.repository.CorrespondenceRepository;
import com.sqli.imputation.service.CorrespondenceService;
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
 * Test class for the CorrespondenceResource REST controller.
 *
 * @see CorrespondenceResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ImputationSqliApp.class)
public class CorrespondenceResourceIntTest {

    private static final String DEFAULT_ID_APP = "AAAAAAAAAA";
    private static final String UPDATED_ID_APP = "BBBBBBBBBB";

    private static final String DEFAULT_ID_PPMC = "AAAAAAAAAA";
    private static final String UPDATED_ID_PPMC = "BBBBBBBBBB";

    private static final String DEFAULT_ID_TBP = "AAAAAAAAAA";
    private static final String UPDATED_ID_TBP = "BBBBBBBBBB";

    @Autowired
    private CorrespondenceRepository correspondenceRepository;

    @Autowired
    private CorrespondenceService correspondenceService;

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

    private MockMvc restCorrespondenceMockMvc;

    private Correspondence correspondence;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CorrespondenceResource correspondenceResource = new CorrespondenceResource(correspondenceService);
        this.restCorrespondenceMockMvc = MockMvcBuilders.standaloneSetup(correspondenceResource)
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
    public static Correspondence createEntity(EntityManager em) {
        Correspondence correspondence = new Correspondence()
            .idAPP(DEFAULT_ID_APP)
            .idPPMC(DEFAULT_ID_PPMC)
            .idTBP(DEFAULT_ID_TBP);
        return correspondence;
    }

    @Before
    public void initTest() {
        correspondence = createEntity(em);
    }

    @Test
    @Transactional
    public void createCorrespondence() throws Exception {
        int databaseSizeBeforeCreate = correspondenceRepository.findAll().size();

        // Create the Correspondence
        restCorrespondenceMockMvc.perform(post("/api/correspondences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(correspondence)))
            .andExpect(status().isCreated());

        // Validate the Correspondence in the database
        List<Correspondence> correspondenceList = correspondenceRepository.findAll();
        assertThat(correspondenceList).hasSize(databaseSizeBeforeCreate + 1);
        Correspondence testCorrespondence = correspondenceList.get(correspondenceList.size() - 1);
        assertThat(testCorrespondence.getIdAPP()).isEqualTo(DEFAULT_ID_APP);
        assertThat(testCorrespondence.getIdPPMC()).isEqualTo(DEFAULT_ID_PPMC);
        assertThat(testCorrespondence.getIdTBP()).isEqualTo(DEFAULT_ID_TBP);
    }

    @Test
    @Transactional
    public void createCorrespondenceWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = correspondenceRepository.findAll().size();

        // Create the Correspondence with an existing ID
        correspondence.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCorrespondenceMockMvc.perform(post("/api/correspondences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(correspondence)))
            .andExpect(status().isBadRequest());

        // Validate the Correspondence in the database
        List<Correspondence> correspondenceList = correspondenceRepository.findAll();
        assertThat(correspondenceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllCorrespondences() throws Exception {
        // Initialize the database
        correspondenceRepository.saveAndFlush(correspondence);

        // Get all the correspondenceList
        restCorrespondenceMockMvc.perform(get("/api/correspondences?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(correspondence.getId().intValue())))
            .andExpect(jsonPath("$.[*].idAPP").value(hasItem(DEFAULT_ID_APP.toString())))
            .andExpect(jsonPath("$.[*].idPPMC").value(hasItem(DEFAULT_ID_PPMC.toString())))
            .andExpect(jsonPath("$.[*].idTBP").value(hasItem(DEFAULT_ID_TBP.toString())));
    }
    
    @Test
    @Transactional
    public void getCorrespondence() throws Exception {
        // Initialize the database
        correspondenceRepository.saveAndFlush(correspondence);

        // Get the correspondence
        restCorrespondenceMockMvc.perform(get("/api/correspondences/{id}", correspondence.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(correspondence.getId().intValue()))
            .andExpect(jsonPath("$.idAPP").value(DEFAULT_ID_APP.toString()))
            .andExpect(jsonPath("$.idPPMC").value(DEFAULT_ID_PPMC.toString()))
            .andExpect(jsonPath("$.idTBP").value(DEFAULT_ID_TBP.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCorrespondence() throws Exception {
        // Get the correspondence
        restCorrespondenceMockMvc.perform(get("/api/correspondences/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCorrespondence() throws Exception {
        // Initialize the database
        correspondenceService.save(correspondence);

        int databaseSizeBeforeUpdate = correspondenceRepository.findAll().size();

        // Update the correspondence
        Correspondence updatedCorrespondence = correspondenceRepository.findById(correspondence.getId()).get();
        // Disconnect from session so that the updates on updatedCorrespondence are not directly saved in db
        em.detach(updatedCorrespondence);
        updatedCorrespondence
            .idAPP(UPDATED_ID_APP)
            .idPPMC(UPDATED_ID_PPMC)
            .idTBP(UPDATED_ID_TBP);

        restCorrespondenceMockMvc.perform(put("/api/correspondences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCorrespondence)))
            .andExpect(status().isOk());

        // Validate the Correspondence in the database
        List<Correspondence> correspondenceList = correspondenceRepository.findAll();
        assertThat(correspondenceList).hasSize(databaseSizeBeforeUpdate);
        Correspondence testCorrespondence = correspondenceList.get(correspondenceList.size() - 1);
        assertThat(testCorrespondence.getIdAPP()).isEqualTo(UPDATED_ID_APP);
        assertThat(testCorrespondence.getIdPPMC()).isEqualTo(UPDATED_ID_PPMC);
        assertThat(testCorrespondence.getIdTBP()).isEqualTo(UPDATED_ID_TBP);
    }

    @Test
    @Transactional
    public void updateNonExistingCorrespondence() throws Exception {
        int databaseSizeBeforeUpdate = correspondenceRepository.findAll().size();

        // Create the Correspondence

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCorrespondenceMockMvc.perform(put("/api/correspondences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(correspondence)))
            .andExpect(status().isBadRequest());

        // Validate the Correspondence in the database
        List<Correspondence> correspondenceList = correspondenceRepository.findAll();
        assertThat(correspondenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteCorrespondence() throws Exception {
        // Initialize the database
        correspondenceService.save(correspondence);

        int databaseSizeBeforeDelete = correspondenceRepository.findAll().size();

        // Delete the correspondence
        restCorrespondenceMockMvc.perform(delete("/api/correspondences/{id}", correspondence.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Correspondence> correspondenceList = correspondenceRepository.findAll();
        assertThat(correspondenceList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Correspondence.class);
        Correspondence correspondence1 = new Correspondence();
        correspondence1.setId(1L);
        Correspondence correspondence2 = new Correspondence();
        correspondence2.setId(correspondence1.getId());
        assertThat(correspondence1).isEqualTo(correspondence2);
        correspondence2.setId(2L);
        assertThat(correspondence1).isNotEqualTo(correspondence2);
        correspondence1.setId(null);
        assertThat(correspondence1).isNotEqualTo(correspondence2);
    }
}
