package com.sqli.imputation.web.rest;

import com.sqli.imputation.ImputationSqliApp;

import com.sqli.imputation.domain.DeliveryCoordinator;
import com.sqli.imputation.repository.DeliveryCoordinatorRepository;
import com.sqli.imputation.service.DeliveryCoordinatorService;
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
 * Test class for the DeliveryCoordinatorResource REST controller.
 *
 * @see DeliveryCoordinatorResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ImputationSqliApp.class)
public class DeliveryCoordinatorResourceIntTest {

    @Autowired
    private DeliveryCoordinatorRepository deliveryCoordinatorRepository;

    @Autowired
    private DeliveryCoordinatorService deliveryCoordinatorService;

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

    private MockMvc restDeliveryCoordinatorMockMvc;

    private DeliveryCoordinator deliveryCoordinator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DeliveryCoordinatorResource deliveryCoordinatorResource = new DeliveryCoordinatorResource(deliveryCoordinatorService);
        this.restDeliveryCoordinatorMockMvc = MockMvcBuilders.standaloneSetup(deliveryCoordinatorResource)
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
    public static DeliveryCoordinator createEntity(EntityManager em) {
        DeliveryCoordinator deliveryCoordinator = new DeliveryCoordinator();
        return deliveryCoordinator;
    }

    @Before
    public void initTest() {
        deliveryCoordinator = createEntity(em);
    }

    @Test
    @Transactional
    public void createDeliveryCoordinator() throws Exception {
        int databaseSizeBeforeCreate = deliveryCoordinatorRepository.findAll().size();

        // Create the DeliveryCoordinator
        restDeliveryCoordinatorMockMvc.perform(post("/api/delivery-coordinators")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deliveryCoordinator)))
            .andExpect(status().isCreated());

        // Validate the DeliveryCoordinator in the database
        List<DeliveryCoordinator> deliveryCoordinatorList = deliveryCoordinatorRepository.findAll();
        assertThat(deliveryCoordinatorList).hasSize(databaseSizeBeforeCreate + 1);
        DeliveryCoordinator testDeliveryCoordinator = deliveryCoordinatorList.get(deliveryCoordinatorList.size() - 1);
    }

    @Test
    @Transactional
    public void createDeliveryCoordinatorWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = deliveryCoordinatorRepository.findAll().size();

        // Create the DeliveryCoordinator with an existing ID
        deliveryCoordinator.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDeliveryCoordinatorMockMvc.perform(post("/api/delivery-coordinators")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deliveryCoordinator)))
            .andExpect(status().isBadRequest());

        // Validate the DeliveryCoordinator in the database
        List<DeliveryCoordinator> deliveryCoordinatorList = deliveryCoordinatorRepository.findAll();
        assertThat(deliveryCoordinatorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllDeliveryCoordinators() throws Exception {
        // Initialize the database
        deliveryCoordinatorRepository.saveAndFlush(deliveryCoordinator);

        // Get all the deliveryCoordinatorList
        restDeliveryCoordinatorMockMvc.perform(get("/api/delivery-coordinators?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(deliveryCoordinator.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getDeliveryCoordinator() throws Exception {
        // Initialize the database
        deliveryCoordinatorRepository.saveAndFlush(deliveryCoordinator);

        // Get the deliveryCoordinator
        restDeliveryCoordinatorMockMvc.perform(get("/api/delivery-coordinators/{id}", deliveryCoordinator.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(deliveryCoordinator.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingDeliveryCoordinator() throws Exception {
        // Get the deliveryCoordinator
        restDeliveryCoordinatorMockMvc.perform(get("/api/delivery-coordinators/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDeliveryCoordinator() throws Exception {
        // Initialize the database
        deliveryCoordinatorService.save(deliveryCoordinator);

        int databaseSizeBeforeUpdate = deliveryCoordinatorRepository.findAll().size();

        // Update the deliveryCoordinator
        DeliveryCoordinator updatedDeliveryCoordinator = deliveryCoordinatorRepository.findById(deliveryCoordinator.getId()).get();
        // Disconnect from session so that the updates on updatedDeliveryCoordinator are not directly saved in db
        em.detach(updatedDeliveryCoordinator);

        restDeliveryCoordinatorMockMvc.perform(put("/api/delivery-coordinators")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedDeliveryCoordinator)))
            .andExpect(status().isOk());

        // Validate the DeliveryCoordinator in the database
        List<DeliveryCoordinator> deliveryCoordinatorList = deliveryCoordinatorRepository.findAll();
        assertThat(deliveryCoordinatorList).hasSize(databaseSizeBeforeUpdate);
        DeliveryCoordinator testDeliveryCoordinator = deliveryCoordinatorList.get(deliveryCoordinatorList.size() - 1);
    }

    @Test
    @Transactional
    public void updateNonExistingDeliveryCoordinator() throws Exception {
        int databaseSizeBeforeUpdate = deliveryCoordinatorRepository.findAll().size();

        // Create the DeliveryCoordinator

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeliveryCoordinatorMockMvc.perform(put("/api/delivery-coordinators")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deliveryCoordinator)))
            .andExpect(status().isBadRequest());

        // Validate the DeliveryCoordinator in the database
        List<DeliveryCoordinator> deliveryCoordinatorList = deliveryCoordinatorRepository.findAll();
        assertThat(deliveryCoordinatorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteDeliveryCoordinator() throws Exception {
        // Initialize the database
        deliveryCoordinatorService.save(deliveryCoordinator);

        int databaseSizeBeforeDelete = deliveryCoordinatorRepository.findAll().size();

        // Delete the deliveryCoordinator
        restDeliveryCoordinatorMockMvc.perform(delete("/api/delivery-coordinators/{id}", deliveryCoordinator.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<DeliveryCoordinator> deliveryCoordinatorList = deliveryCoordinatorRepository.findAll();
        assertThat(deliveryCoordinatorList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DeliveryCoordinator.class);
        DeliveryCoordinator deliveryCoordinator1 = new DeliveryCoordinator();
        deliveryCoordinator1.setId(1L);
        DeliveryCoordinator deliveryCoordinator2 = new DeliveryCoordinator();
        deliveryCoordinator2.setId(deliveryCoordinator1.getId());
        assertThat(deliveryCoordinator1).isEqualTo(deliveryCoordinator2);
        deliveryCoordinator2.setId(2L);
        assertThat(deliveryCoordinator1).isNotEqualTo(deliveryCoordinator2);
        deliveryCoordinator1.setId(null);
        assertThat(deliveryCoordinator1).isNotEqualTo(deliveryCoordinator2);
    }
}
