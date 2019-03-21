package com.sqli.imputation.web.rest;

import com.sqli.imputation.ImputationSqliApp;

import com.sqli.imputation.domain.ImputationType;
import com.sqli.imputation.repository.ImputationTypeRepository;
import com.sqli.imputation.service.ImputationTypeService;
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
 * Test class for the ImputationTypeResource REST controller.
 *
 * @see ImputationTypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ImputationSqliApp.class)
public class ImputationTypeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private ImputationTypeRepository imputationTypeRepository;

    @Autowired
    private ImputationTypeService imputationTypeService;

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

    private MockMvc restImputationTypeMockMvc;

    private ImputationType imputationType;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ImputationTypeResource imputationTypeResource = new ImputationTypeResource(imputationTypeService);
        this.restImputationTypeMockMvc = MockMvcBuilders.standaloneSetup(imputationTypeResource)
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
    public static ImputationType createEntity(EntityManager em) {
        ImputationType imputationType = new ImputationType()
            .name(DEFAULT_NAME);
        return imputationType;
    }

    @Before
    public void initTest() {
        imputationType = createEntity(em);
    }

    @Test
    @Transactional
    public void createImputationType() throws Exception {
        int databaseSizeBeforeCreate = imputationTypeRepository.findAll().size();

        // Create the ImputationType
        restImputationTypeMockMvc.perform(post("/api/imputation-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imputationType)))
            .andExpect(status().isCreated());

        // Validate the ImputationType in the database
        List<ImputationType> imputationTypeList = imputationTypeRepository.findAll();
        assertThat(imputationTypeList).hasSize(databaseSizeBeforeCreate + 1);
        ImputationType testImputationType = imputationTypeList.get(imputationTypeList.size() - 1);
        assertThat(testImputationType.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createImputationTypeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = imputationTypeRepository.findAll().size();

        // Create the ImputationType with an existing ID
        imputationType.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restImputationTypeMockMvc.perform(post("/api/imputation-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imputationType)))
            .andExpect(status().isBadRequest());

        // Validate the ImputationType in the database
        List<ImputationType> imputationTypeList = imputationTypeRepository.findAll();
        assertThat(imputationTypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllImputationTypes() throws Exception {
        // Initialize the database
        imputationTypeRepository.saveAndFlush(imputationType);

        // Get all the imputationTypeList
        restImputationTypeMockMvc.perform(get("/api/imputation-types?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(imputationType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
    
    @Test
    @Transactional
    public void getImputationType() throws Exception {
        // Initialize the database
        imputationTypeRepository.saveAndFlush(imputationType);

        // Get the imputationType
        restImputationTypeMockMvc.perform(get("/api/imputation-types/{id}", imputationType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(imputationType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingImputationType() throws Exception {
        // Get the imputationType
        restImputationTypeMockMvc.perform(get("/api/imputation-types/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateImputationType() throws Exception {
        // Initialize the database
        imputationTypeService.save(imputationType);

        int databaseSizeBeforeUpdate = imputationTypeRepository.findAll().size();

        // Update the imputationType
        ImputationType updatedImputationType = imputationTypeRepository.findById(imputationType.getId()).get();
        // Disconnect from session so that the updates on updatedImputationType are not directly saved in db
        em.detach(updatedImputationType);
        updatedImputationType
            .name(UPDATED_NAME);

        restImputationTypeMockMvc.perform(put("/api/imputation-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedImputationType)))
            .andExpect(status().isOk());

        // Validate the ImputationType in the database
        List<ImputationType> imputationTypeList = imputationTypeRepository.findAll();
        assertThat(imputationTypeList).hasSize(databaseSizeBeforeUpdate);
        ImputationType testImputationType = imputationTypeList.get(imputationTypeList.size() - 1);
        assertThat(testImputationType.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingImputationType() throws Exception {
        int databaseSizeBeforeUpdate = imputationTypeRepository.findAll().size();

        // Create the ImputationType

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImputationTypeMockMvc.perform(put("/api/imputation-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imputationType)))
            .andExpect(status().isBadRequest());

        // Validate the ImputationType in the database
        List<ImputationType> imputationTypeList = imputationTypeRepository.findAll();
        assertThat(imputationTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteImputationType() throws Exception {
        // Initialize the database
        imputationTypeService.save(imputationType);

        int databaseSizeBeforeDelete = imputationTypeRepository.findAll().size();

        // Delete the imputationType
        restImputationTypeMockMvc.perform(delete("/api/imputation-types/{id}", imputationType.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ImputationType> imputationTypeList = imputationTypeRepository.findAll();
        assertThat(imputationTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ImputationType.class);
        ImputationType imputationType1 = new ImputationType();
        imputationType1.setId(1L);
        ImputationType imputationType2 = new ImputationType();
        imputationType2.setId(imputationType1.getId());
        assertThat(imputationType1).isEqualTo(imputationType2);
        imputationType2.setId(2L);
        assertThat(imputationType1).isNotEqualTo(imputationType2);
        imputationType1.setId(null);
        assertThat(imputationType1).isNotEqualTo(imputationType2);
    }
}
