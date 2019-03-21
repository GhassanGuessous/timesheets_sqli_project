package com.sqli.imputation.web.rest;

import com.sqli.imputation.ImputationSqliApp;

import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.repository.CollaboratorRepository;
import com.sqli.imputation.service.CollaboratorService;
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
 * Test class for the CollaboratorResource REST controller.
 *
 * @see CollaboratorResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ImputationSqliApp.class)
public class CollaboratorResourceIntTest {

    private static final String DEFAULT_FIRSTNAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRSTNAME = "BBBBBBBBBB";

    private static final String DEFAULT_LASTNAME = "AAAAAAAAAA";
    private static final String UPDATED_LASTNAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    @Autowired
    private CollaboratorRepository collaboratorRepository;

    @Autowired
    private CollaboratorService collaboratorService;

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

    private MockMvc restCollaboratorMockMvc;

    private Collaborator collaborator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CollaboratorResource collaboratorResource = new CollaboratorResource(collaboratorService);
        this.restCollaboratorMockMvc = MockMvcBuilders.standaloneSetup(collaboratorResource)
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
    public static Collaborator createEntity(EntityManager em) {
        Collaborator collaborator = new Collaborator()
            .firstname(DEFAULT_FIRSTNAME)
            .lastname(DEFAULT_LASTNAME)
            .email(DEFAULT_EMAIL);
        return collaborator;
    }

    @Before
    public void initTest() {
        collaborator = createEntity(em);
    }

    @Test
    @Transactional
    public void createCollaborator() throws Exception {
        int databaseSizeBeforeCreate = collaboratorRepository.findAll().size();

        // Create the Collaborator
        restCollaboratorMockMvc.perform(post("/api/collaborators")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(collaborator)))
            .andExpect(status().isCreated());

        // Validate the Collaborator in the database
        List<Collaborator> collaboratorList = collaboratorRepository.findAll();
        assertThat(collaboratorList).hasSize(databaseSizeBeforeCreate + 1);
        Collaborator testCollaborator = collaboratorList.get(collaboratorList.size() - 1);
        assertThat(testCollaborator.getFirstname()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(testCollaborator.getLastname()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(testCollaborator.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    public void createCollaboratorWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = collaboratorRepository.findAll().size();

        // Create the Collaborator with an existing ID
        collaborator.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCollaboratorMockMvc.perform(post("/api/collaborators")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(collaborator)))
            .andExpect(status().isBadRequest());

        // Validate the Collaborator in the database
        List<Collaborator> collaboratorList = collaboratorRepository.findAll();
        assertThat(collaboratorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllCollaborators() throws Exception {
        // Initialize the database
        collaboratorRepository.saveAndFlush(collaborator);

        // Get all the collaboratorList
        restCollaboratorMockMvc.perform(get("/api/collaborators?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(collaborator.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstname").value(hasItem(DEFAULT_FIRSTNAME.toString())))
            .andExpect(jsonPath("$.[*].lastname").value(hasItem(DEFAULT_LASTNAME.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())));
    }
    
    @Test
    @Transactional
    public void getCollaborator() throws Exception {
        // Initialize the database
        collaboratorRepository.saveAndFlush(collaborator);

        // Get the collaborator
        restCollaboratorMockMvc.perform(get("/api/collaborators/{id}", collaborator.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(collaborator.getId().intValue()))
            .andExpect(jsonPath("$.firstname").value(DEFAULT_FIRSTNAME.toString()))
            .andExpect(jsonPath("$.lastname").value(DEFAULT_LASTNAME.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCollaborator() throws Exception {
        // Get the collaborator
        restCollaboratorMockMvc.perform(get("/api/collaborators/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCollaborator() throws Exception {
        // Initialize the database
        collaboratorService.save(collaborator);

        int databaseSizeBeforeUpdate = collaboratorRepository.findAll().size();

        // Update the collaborator
        Collaborator updatedCollaborator = collaboratorRepository.findById(collaborator.getId()).get();
        // Disconnect from session so that the updates on updatedCollaborator are not directly saved in db
        em.detach(updatedCollaborator);
        updatedCollaborator
            .firstname(UPDATED_FIRSTNAME)
            .lastname(UPDATED_LASTNAME)
            .email(UPDATED_EMAIL);

        restCollaboratorMockMvc.perform(put("/api/collaborators")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCollaborator)))
            .andExpect(status().isOk());

        // Validate the Collaborator in the database
        List<Collaborator> collaboratorList = collaboratorRepository.findAll();
        assertThat(collaboratorList).hasSize(databaseSizeBeforeUpdate);
        Collaborator testCollaborator = collaboratorList.get(collaboratorList.size() - 1);
        assertThat(testCollaborator.getFirstname()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testCollaborator.getLastname()).isEqualTo(UPDATED_LASTNAME);
        assertThat(testCollaborator.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void updateNonExistingCollaborator() throws Exception {
        int databaseSizeBeforeUpdate = collaboratorRepository.findAll().size();

        // Create the Collaborator

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCollaboratorMockMvc.perform(put("/api/collaborators")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(collaborator)))
            .andExpect(status().isBadRequest());

        // Validate the Collaborator in the database
        List<Collaborator> collaboratorList = collaboratorRepository.findAll();
        assertThat(collaboratorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteCollaborator() throws Exception {
        // Initialize the database
        collaboratorService.save(collaborator);

        int databaseSizeBeforeDelete = collaboratorRepository.findAll().size();

        // Delete the collaborator
        restCollaboratorMockMvc.perform(delete("/api/collaborators/{id}", collaborator.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Collaborator> collaboratorList = collaboratorRepository.findAll();
        assertThat(collaboratorList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Collaborator.class);
        Collaborator collaborator1 = new Collaborator();
        collaborator1.setId(1L);
        Collaborator collaborator2 = new Collaborator();
        collaborator2.setId(collaborator1.getId());
        assertThat(collaborator1).isEqualTo(collaborator2);
        collaborator2.setId(2L);
        assertThat(collaborator1).isNotEqualTo(collaborator2);
        collaborator1.setId(null);
        assertThat(collaborator1).isNotEqualTo(collaborator2);
    }
}
