package io.github.dzdialectapispring;

import static io.github.dzdialectapispring.other.Config.OBJECT_MAPPER;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.github.dzdialectapispring.generic.ResourceList;
import io.github.dzdialectapispring.pronoun.AbstractPronoun;
import io.github.dzdialectapispring.pronoun.PronounService;
import io.github.dzdialectapispring.verb.Verb;
import io.github.dzdialectapispring.verb.VerbService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
@SpringBootApplication
@Slf4j
public class DzDialectApiSpringApplication {

  public static void main(String[] args) throws IOException {
    LOGGER.debug("main()");
    File            file           = new File("./src/main/resources/dz-dialect-api-firebase-adminsdk-gcmh0-0bc72f2007.json");
    FileInputStream serviceAccount = new FileInputStream(file.getAbsolutePath());
    LOGGER.debug("service account OK");
    FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build();
    LOGGER.debug("firebase options OK");
    FirebaseApp.initializeApp(options);
    LOGGER.debug("app init OK");
    SpringApplication.run(DzDialectApiSpringApplication.class, args);
    LOGGER.debug("app run OK");
  }

  // @Bean
  CommandLineRunner verbsInit(VerbService verbService) {
    System.out.println("verbs initialization...");
    return args -> {
      Verb[] verbConfigurations = new Verb[]{};
      try {
        verbConfigurations =
            OBJECT_MAPPER.readValue(new File("./src/main/resources/static/verbs/.verb_config.json"), Verb[].class);
      } catch (Exception e) {
        LOGGER.error("could not load verb configurations " + e.getMessage());
        e.printStackTrace();
      }
      Set<String> files = new HashSet<>(ResourceList.getResources(Pattern.compile(".*verbs.*json")))
          .stream().filter(o -> !o.contains(".verb_config.json")).collect(Collectors.toSet());
      for (String fileName : files) {
        Verb           verb              = OBJECT_MAPPER.readValue(new File(fileName), Verb.class);
        Optional<Verb> verbConfiguration = Arrays.stream(verbConfigurations).filter(o -> o.getId().equals(verb.getId())).findFirst();
        if (verbConfiguration.isPresent()) {
          verb.importConfig(verbConfiguration.get());
        } else {
          LOGGER.debug("no configuration found for verb " + verb.getId());
        }
        verbService.insert(verb);
      }
    };
  }


  // @Bean
  CommandLineRunner pronounsInit(PronounService pronounService) {
    System.out.println("pronouns initialization...");
    return args -> {
      List<AbstractPronoun> pronouns = List.of(OBJECT_MAPPER.readValue(new File("./src/main/resources/static/other/personal_pronouns.json"),
                                                                       AbstractPronoun[].class));
      pronounService.insert(pronouns);
    };
  }

}
