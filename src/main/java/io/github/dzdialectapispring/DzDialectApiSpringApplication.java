package io.github.dzdialectapispring;

import static io.github.dzdialectapispring.other.Config.OBJECT_MAPPER;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.github.dzdialectapispring.adjective.Adjective;
import io.github.dzdialectapispring.adjective.AdjectiveService;
import io.github.dzdialectapispring.adverb.adjective.Adverb;
import io.github.dzdialectapispring.adverb.adjective.AdverbService;
import io.github.dzdialectapispring.generic.ResourceList;
import io.github.dzdialectapispring.pronoun.AbstractPronoun;
import io.github.dzdialectapispring.pronoun.PronounService;
import io.github.dzdialectapispring.question.AbstractQuestion;
import io.github.dzdialectapispring.question.QuestionService;
import io.github.dzdialectapispring.verb.Verb;
import io.github.dzdialectapispring.verb.VerbService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")}, info =
@Info(title = "DZDialect API", version = "1.0.0", description = "DZDialect API v1.0"))
@SpringBootApplication
@Slf4j
@Controller
public class DzDialectApiSpringApplication {

  public static void main(String[] args) throws IOException {
    LOGGER.debug("main()");
    String      credentialString = System.getenv("GOOGLE_CREDENTIALS");
    InputStream serviceAccount;
    if (credentialString != null) {
      LOGGER.debug("loading credentials from variable environment");
      serviceAccount = new ByteArrayInputStream(credentialString.getBytes(StandardCharsets.UTF_8));
    } else {
      LOGGER.debug("loading credentials from local file");
      File file = new File("../dz-dialect-api-443aafbaf7a9.json");
      serviceAccount = new FileInputStream(file.getAbsolutePath());
    }
    FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build();

    LOGGER.debug("firebase options OK");
    FirebaseApp.initializeApp(options);
    LOGGER.debug("app init OK");
    SpringApplication.run(DzDialectApiSpringApplication.class, args);
    LOGGER.debug("app run OK");
  }


  @RequestMapping("/")
  @ResponseBody
  String home() {
    return "Hello World!";
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

  //@Bean
  CommandLineRunner questionsInit(QuestionService questionService) {
    System.out.println("questions initialization...");
    return args -> {
      List<AbstractQuestion>
          questions =
          (List.of(OBJECT_MAPPER.readValue(new File("./src/main/resources/static/other/questions.json"), AbstractQuestion[].class)));
      questionService.insert(questions);
    };
  }

  //@Bean
  CommandLineRunner adjectivesInit(AdjectiveService adjectiveService) {
    System.out.println("adjective initialization...");
    return args -> {
      Set<String>
          files =
          new HashSet<>(ResourceList.getResources(Pattern.compile(".*adjectives.*json")));
      for (String fileName : files) {
        try {
          Adjective adjective = OBJECT_MAPPER.readValue(new File(fileName), Adjective.class);
          adjectiveService.insert(adjective);
        } catch (IOException e) {
          System.err.println("could not load adjective file " + fileName);
        }
      }
    };
  }

  // @Bean
  CommandLineRunner adverbInit(AdverbService adverbService) {
    System.out.println("adverb initialization...");
    return args -> {
      Set<String>
          files =
          new HashSet<>(ResourceList.getResources(Pattern.compile(".*adv.*json")));
      for (String fileName : files) {
        try {
          Adverb adverb = OBJECT_MAPPER.readValue(new File(fileName), Adverb.class);
          adverbService.insert(adverb);
        } catch (IOException e) {
          System.err.println("could not load adverb file " + fileName);
        }
      }
    };
  }

}
