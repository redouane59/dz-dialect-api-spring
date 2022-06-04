package io.github.dzdialectapispring;

import static io.github.dzdialectapispring.other.Config.OBJECT_MAPPER;

import io.github.dzdialectapispring.generic.ResourceList;
import io.github.dzdialectapispring.pronoun.AbstractPronoun;
import io.github.dzdialectapispring.pronoun.PronounRepository;
import io.github.dzdialectapispring.verb.Verb;
import io.github.dzdialectapispring.verb.VerbRepository;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication
public class DzDialectApiSpringApplication {

  public static void main(String[] args) {
    SpringApplication.run(DzDialectApiSpringApplication.class, args);
  }

  @Bean
  CommandLineRunner verbsInit(VerbRepository repository, MongoTemplate mongoTemplate) {
    return args -> {
      repository.deleteAll();
      Set<String>
          files =
          new HashSet<>(ResourceList.getResources(Pattern.compile(".*verbs.*json")))
              .stream().filter(o -> !o.contains(".verb_config.json")).collect(Collectors.toSet());
      for (String fileName : files) {
        Verb verb = OBJECT_MAPPER.readValue(new File(fileName), Verb.class);
        repository.insert(verb);
      }
    };
  }


  @Bean
  CommandLineRunner pronounsInit(PronounRepository repository, MongoTemplate mongoTemplate) {
    return args -> {
      repository.deleteAll();

      List<AbstractPronoun> pronouns = List.of(OBJECT_MAPPER.readValue(new File("./src/main/resources/static/other/personal_pronouns.json"),
                                                                       AbstractPronoun[].class));
      repository.insert(pronouns);
    };
  }


}
