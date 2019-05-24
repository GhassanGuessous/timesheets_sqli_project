package com.sqli.imputation.repository;

import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.domain.Notification;
import com.sqli.imputation.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByCollaboratorAndMonthAndYearAndType(
        Collaborator collaborator,
        int month,
        int year,
        String type
    );

    List<Notification> findByYearAndCollaborator_Team(int year, Team team);
}
