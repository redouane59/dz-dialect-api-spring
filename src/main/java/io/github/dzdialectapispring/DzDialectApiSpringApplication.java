package io.github.dzdialectapispring;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DzDialectApiSpringApplication {

  public static void main(String[] args) throws IOException, URISyntaxException {
    ClassLoader classLoader = DzDialectApiSpringApplication.class.getClassLoader();

    File            file           = new File(classLoader.getResource("dz-dialect-api-firebase-adminsdk-gcmh0-0bc72f2007.json").toURI());
    FileInputStream serviceAccount = new FileInputStream(file.getAbsolutePath());

    FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build();

    FirebaseApp.initializeApp(options);

    SpringApplication.run(DzDialectApiSpringApplication.class, args);
  }

/*  @Bean
  CommandLineRunner verbsInit(VerbService verbService) {
    System.out.println("verbs initialization...");
    return args -> {
      Set<String> files = new HashSet<>(ResourceList.getResources(Pattern.compile(".*verbs.*json")))
          .stream().filter(o -> !o.contains(".verb_config.json")).collect(Collectors.toSet());
      for (String fileName : files) {
        Verb verb = OBJECT_MAPPER.readValue(new File(fileName), Verb.class);
        verbService.insert(verb);
      }
    };
  }*/


/*  @Bean
  CommandLineRunner pronounsInit(PronounService pronounService) {
    System.out.println("pronouns initialization...");
    return args -> {
      List<AbstractPronoun> pronouns = List.of(OBJECT_MAPPER.readValue(new File("./src/main/resources/static/other/personal_pronouns.json"),
                                                                       AbstractPronoun[].class));
      pronounService.insert(pronouns);
    };
  }*/

}
