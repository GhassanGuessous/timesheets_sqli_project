package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.domain.Notification;
import com.sqli.imputation.repository.NotificationRepository;
import com.sqli.imputation.service.NotificationService;
import com.sqli.imputation.service.dto.NotificationDTO;
import com.sqli.imputation.service.dto.NotificationGapVariationDTO;
import com.sqli.imputation.service.dto.StatisticsDTO;
import com.sqli.imputation.service.dto.TeamYearDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DefaultNotificationService implements NotificationService {

    private static final String VS = " vs ";
    public static final String APP_VS_TBP = "APP vs TBP";
    public static final String APP_VS_PPMC = "APP vs PPMC";
    public static final int INITIAL_FREQUENCE = 1;

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public Notification createFromDTO(NotificationDTO dto) {
        Notification notification = new Notification();
        notification.setCollaborator(dto.getCollaborator());
        notification.setMonth(dto.getMonth());
        notification.setYear(dto.getYear());
        notification.setType(getType(dto));
        notification.setSendingDate(new Date());
        return notification;
    }

    private String getType(NotificationDTO dto) {
        return dto.getAppGap().getImputationType() + VS + dto.getComparedGap().getImputationType();
    }

    @Override
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public Optional<Notification> find(NotificationDTO dto) {
        return notificationRepository.findByCollaboratorAndMonthAndYearAndType(
            dto.getCollaborator(), dto.getMonth(), dto.getYear(), getType(dto)
        );
    }

    @Override
    public List<Notification> getTeamNotifications(TeamYearDTO teamYearDTO) {
        if (teamYearDTO.getTeam() == null) return notificationRepository.findByYear(teamYearDTO.getYear());
        return notificationRepository.findByYearAndCollaborator_Team(teamYearDTO.getYear(), teamYearDTO.getTeam());
    }

    @Override
    public List<StatisticsDTO> getStatistics(TeamYearDTO teamYearDTO) {
        List<Notification> notifications = getTeamNotifications(teamYearDTO);
        List<StatisticsDTO> statisticsDTOS = new ArrayList<>();
        notifications.forEach(notification -> {
            if (isStatisticExistForCollab(statisticsDTOS, notification.getCollaborator())) {
                editStatistic(statisticsDTOS, notification);
            } else {
                addStatistic(statisticsDTOS, notification);
            }
        });
        return statisticsDTOS;
    }

    @Override
    public List<NotificationGapVariationDTO> getNotificationGapVariation(TeamYearDTO teamYearDTO) {
        List<Notification> notifications = getTeamNotifications(teamYearDTO);
        return getGapVariation(notifications);
    }

    private void editStatistic(List<StatisticsDTO> statisticsDTOS, Notification notification) {
        StatisticsDTO statisticsDTO = findStatisticByCollab(statisticsDTOS, notification.getCollaborator());
        if (isAppPpmcType(notification.getType())) statisticsDTO.setApp_vs_ppmc(statisticsDTO.getApp_vs_ppmc() + 1);
        if (isAppTbpType(notification.getType())) statisticsDTO.setApp_vs_tbp(statisticsDTO.getApp_vs_tbp() + 1);
        replaceStatistic(statisticsDTOS, statisticsDTO);
    }

    private void replaceStatistic(List<StatisticsDTO> statisticsDTOS, StatisticsDTO statisticsDTO) {
        int index = statisticsDTOS.indexOf(statisticsDTO);
        statisticsDTOS.set(index, statisticsDTO);
    }

    private void addStatistic(List<StatisticsDTO> statisticsDTOS, Notification notification) {
        StatisticsDTO statisticsDTO = new StatisticsDTO();
        statisticsDTO.setCollaborator(notification.getCollaborator().getFirstname() + " " + notification.getCollaborator().getLastname());
        if (isAppPpmcType(notification.getType())) statisticsDTO.setApp_vs_ppmc(1);
        if (isAppTbpType(notification.getType())) statisticsDTO.setApp_vs_tbp(1);
        statisticsDTOS.add(statisticsDTO);
    }

    private boolean isAppTbpType(String type) {
        return type.equals(APP_VS_TBP);
    }

    private boolean isAppPpmcType(String type) {
        return type.equals(APP_VS_PPMC);
    }

    private boolean isStatisticExistForCollab(List<StatisticsDTO> statisticsDTOS, Collaborator collaborator) {
        return statisticsDTOS.stream().anyMatch(dto -> dto.getCollaborator().equals(collaborator.getFirstname() + " " + collaborator.getLastname()));
    }

    private StatisticsDTO findStatisticByCollab(List<StatisticsDTO> statisticsDTOS, Collaborator collaborator) {
        return statisticsDTOS.stream().filter(dto -> dto.getCollaborator().equals(collaborator.getFirstname() + " " + collaborator.getLastname())).findFirst().get();
    }

    private List<NotificationGapVariationDTO> getGapVariation(List<Notification> notifications) {
        List<NotificationGapVariationDTO> gapVariationDTOS = new ArrayList<>();
        notifications.forEach(notification -> {
            if (isGabVariationIsExistForMonth(gapVariationDTOS, notification.getMonth())) {
                editGapVariationOfMonth(gapVariationDTOS, notification);
            } else {
                addGapVariationOfMonth(gapVariationDTOS, notification);
            }
        });
        return sortGapVariationDTOS(gapVariationDTOS);
    }

    private List<NotificationGapVariationDTO> sortGapVariationDTOS(List<NotificationGapVariationDTO> gapVariationDTOS) {
        return gapVariationDTOS.stream().sorted(Comparator.comparing(NotificationGapVariationDTO::getMonth)).collect(Collectors.toList());
    }

    private void editGapVariationOfMonth(List<NotificationGapVariationDTO> gapVariationDTOS, Notification notification) {
        NotificationGapVariationDTO gapVariationDTO = findGabVariationDtoByMonth(gapVariationDTOS, notification.getMonth());
        gapVariationDTO.incrementFrecuency();
    }

    private NotificationGapVariationDTO findGabVariationDtoByMonth(List<NotificationGapVariationDTO> gapVariationDTOS, int month) {
        return gapVariationDTOS.stream().filter(dto -> dto.getMonth() == month).findFirst().get();
    }

    private void addGapVariationOfMonth(List<NotificationGapVariationDTO> gapVariationDTOS, Notification notification) {
        gapVariationDTOS.add(new NotificationGapVariationDTO(notification.getYear(), notification.getMonth(), INITIAL_FREQUENCE));
    }

    private boolean isGabVariationIsExistForMonth(List<NotificationGapVariationDTO> gapVariationDTOS, int month) {
        return gapVariationDTOS.stream().anyMatch(dto -> dto.getMonth() == month);
    }
}
