package com.sqli.imputation.service;

import com.sqli.imputation.domain.Imputation;

import com.sqli.imputation.service.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing Imputation.
 */
public interface ImputationService {

    /**
     * Save a imputation.
     *
     * @param imputation the entity to save
     * @return the persisted entity
     */
    Imputation save(Imputation imputation);

    /**
     * Get all the imputations.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Imputation> findAll(Pageable pageable);


    /**
     * Get the "id" imputation.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Imputation> findOne(Long id);

    /**
     * Delete the "id" imputation.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Get the App imputation.
     *
     * @param appRequestDTO the app imputation request
     * @return the entity
     */
    List<Imputation> getAppImputation(AppRequestDTO appRequestDTO);

    Object[] getTbpImputation(TbpRequestBodyDTO tbpRequestBodyDTO);

    Optional<Imputation> getPpmcImputation(MultipartFile file, String agresso);

    Object[] compareAppAndTbp(AppTbpRequestBodyDTO appTbpRequest);

    Object[] compareAppAndTbpAdvanced(AppTbpRequestBodyDTO appTbpRequest);

    List<ImputationComparatorDTO> compareAppPpmc(MultipartFile file, AppRequestDTO appRequestDTO);

    List<ImputationComparatorAdvancedDTO> compareAppPpmcAdvanced(MultipartFile file, AppRequestDTO appRequestDTO);

    void sendNotifications(List<NotificationDTO> notifications);

    Optional<Imputation> findByTeam(ImputationRequestDTO imputationRequestDTO);

    List<ImputationComparatorDTO> getComparisonFromDB(AppRequestDTO appRequestDTO, String imputationType);

    List<ImputationComparatorAdvancedDTO> getAdvancedComparisonFromDB(AppRequestDTO appRequestDTO, String imputationType);
}
