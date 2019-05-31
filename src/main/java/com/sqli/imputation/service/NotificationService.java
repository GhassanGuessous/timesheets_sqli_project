package com.sqli.imputation.service;

import com.sqli.imputation.domain.Notification;
import com.sqli.imputation.service.dto.NotificationDTO;
import com.sqli.imputation.service.dto.NotificationGapVariationDTO;
import com.sqli.imputation.service.dto.StatisticsDTO;
import com.sqli.imputation.service.dto.TeamYearDTO;

import java.util.List;
import java.util.Optional;

public interface NotificationService {

    Notification createFromDTO(NotificationDTO dto);
    Notification save(Notification notification);
    Optional<Notification> find(NotificationDTO dto);

    List<Notification> getTeamNotifications(TeamYearDTO teamYearDTO);

    List<StatisticsDTO> getStatistics(TeamYearDTO teamYearDTO);

    List<NotificationGapVariationDTO> getNotificationGapVariation(TeamYearDTO teamYearDTO);
}
