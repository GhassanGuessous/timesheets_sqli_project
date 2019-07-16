package com.sqli.imputation;

import com.sqli.imputation.config.ApplicationProperties;
import com.sqli.imputation.config.DefaultProfileUtil;

import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.domain.Correspondence;
import com.sqli.imputation.domain.Team;
import com.sqli.imputation.repository.CollaboratorRepository;
import com.sqli.imputation.repository.CorrespondenceRepository;
import com.sqli.imputation.repository.TeamRepository;
import com.sqli.imputation.service.JiraResourceService;
import com.sqli.imputation.service.dto.AppTbpRequestBodyDTO;
import com.sqli.imputation.service.impl.DefaultDbPopulatorService;
import io.github.jhipster.config.JHipsterConstants;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties({LiquibaseProperties.class, ApplicationProperties.class})
public class ImputationSqliApp {

    @Autowired
    DefaultDbPopulatorService defaultDbPopulator;
    @Autowired
    CollaboratorRepository collaboratorRepository;
    @Autowired
    CorrespondenceRepository correspondenceRepository;
    @Autowired
    TeamRepository teamRepository;
    private static final Logger log = LoggerFactory.getLogger(ImputationSqliApp.class);


    private final Environment env;

    public ImputationSqliApp(Environment env) {
        this.env = env;
    }

    /**
     * Initializes ImputationSqli.
     * <p>
     * Spring profiles can be configured with a program argument --spring.profiles.active=your-active-profile
     * <p>
     * You can find more information on how profiles work with JHipster on <a href="https://www.jhipster.tech/profiles/">https://www.jhipster.tech/profiles/</a>.
     */
    @PostConstruct
    public void initApplication() {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)) {
            log.error("You have misconfigured your application! It should not run " +
                "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_CLOUD)) {
            log.error("You have misconfigured your application! It should not " +
                "run with both the 'dev' and 'cloud' profiles at the same time.");
        }
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ImputationSqliApp.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "/";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\t{}://localhost:{}{}\n\t" +
                "External: \t{}://{}:{}{}\n\t" +
                "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol,
            serverPort,
            contextPath,
            protocol,
            hostAddress,
            serverPort,
            contextPath,
            env.getActiveProfiles());
    }

    @Bean
    public CommandLineRunner run() throws Exception {
        return args -> {
//            defaultDbPopulator.populate();

            // delete collabs with team is not nespresso
//            List<Collaborator> collaborators = collaboratorRepository.findAll();
//            collaborators.forEach(collaborator -> {
//                    if (collaborator.getTeam() == null || !collaborator.getTeam().getName().toLowerCase().contains("nes")) {
//                        deleteCorrespondence(collaborator);
//                        collaboratorRepository.delete(collaborator);
//                }
//            });
            //delete teams
//            List<Team>  teams =teamRepository.findAll();
//            teams.forEach(team -> {
//                if (!team.getName().toLowerCase().contains("nes")) {
//                    teamRepository.delete(team);
//                }
//            });

//             collabs with team is not nespresso
//            List<Collaborator> collaborators = collaboratorRepository.findAll();
//            collaborators.forEach(collaborator -> {
//                if (collaborator.getTeam() != null) {
//                    if (!collaborator.getTeam().getName().contains("NES")) {
//                        collaborator.setTeam(null);
//                        collaboratorRepository.save(collaborator);
//                    }
//                }
//            });
        };
    }

    private void deleteCorrespondence(Collaborator collaborator) {
        Correspondence correspondence= correspondenceRepository.findByCollaboratorId(collaborator.getId());
        correspondenceRepository.delete(correspondence);
    }
}
