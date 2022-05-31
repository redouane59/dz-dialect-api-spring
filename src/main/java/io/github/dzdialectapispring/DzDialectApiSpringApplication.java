package io.github.dzdialectapispring;

import io.github.dzdialectapispring.other.concrets.Possession;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.concrets.Word;
import io.github.dzdialectapispring.other.conjugation.Conjugation;
import io.github.dzdialectapispring.other.enumerations.Gender;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.other.enumerations.Tense;
import io.github.dzdialectapispring.other.enumerations.WordType;
import io.github.dzdialectapispring.verb.Verb;
import io.github.dzdialectapispring.verb.VerbRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
  CommandLineRunner runner(VerbRepository repository, MongoTemplate mongoTemplate) {
    return args -> {
      Verb verb = new Verb();
      verb.setId("être");
      verb.setWordType(WordType.VERB);
      List<? super Word> conjugations = new ArrayList<>();
      Conjugation        conjugation1 = new Conjugation();
      conjugation1.setTense(Tense.PRESENT);
      conjugation1.setGender(Gender.X);
      conjugation1.setPossession(Possession.I);
      conjugation1.setTranslations(Set.of(new Translation(Lang.FR, "suis"), new Translation(Lang.DZ, "rani", "__")));
      conjugations.add(conjugation1);
      Conjugation conjugation2 = new Conjugation();
      conjugation2.setTense(Tense.PRESENT);
      conjugation2.setGender(Gender.M);
      conjugation2.setPossession(Possession.YOU);
      conjugation2.setTranslations(Set.of(new Translation(Lang.FR, "es"), new Translation(Lang.DZ, "rak", "___")));
      conjugations.add(conjugation2);
      verb.setValues(conjugations);

      repository.findById(verb.getId()).ifPresentOrElse(
          s -> {
            System.out.println(verb.getId() + " already exists");
          }, () -> {
            System.out.println("inserting verb " + verb.getId());
            repository.insert(verb);
          });

    };
  }

}
