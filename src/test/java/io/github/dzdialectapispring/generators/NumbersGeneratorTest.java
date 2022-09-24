package io.github.dzdialectapispring.generators;

import static io.github.dzdialectapispring.other.Config.OBJECT_MAPPER;
import static io.github.dzdialectapispring.other.Config.RANDOM;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.dzdialectapispring.DB;
import io.github.dzdialectapispring.DzDialectApiSpringApplication;
import io.github.dzdialectapispring.number.Number;
import io.github.dzdialectapispring.other.concrets.Word;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.sentence.Sentence;
import io.github.dzdialectapispring.sentence.Sentence.SentenceContent;
import io.github.dzdialectapispring.sentence.SentenceDTO;
import io.github.dzdialectapispring.sentence.SentenceDTO.WordPropositionsDTO;
import io.github.dzdialectapispring.sentence.SentenceService;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NumbersGeneratorTest {

  @Autowired
  private SentenceService sentenceService;

  @BeforeAll
  public static void init() throws IOException {
    DzDialectApiSpringApplication.main(new String[]{});
  }

  @Test
  public void testAllANumbers() throws JsonProcessingException {
    int nbPropositions = 4;
    for (Number number : DB.NUMBERS) {
      Sentence sentence = new Sentence();
      sentence.setTranslations(number.getValues().get(0).getTranslations());
      sentence.setContent(SentenceContent.builder()
                                         .sentenceSchema(DB.SENTENCE_SCHEMAS.stream()
                                                                            .filter(s -> s.getId().equals("number"))
                                                                            .findFirst()
                                                                            .orElse(null))
                                         .build());

      SentenceDTO         sentenceDTO      = new SentenceDTO(sentence);
      WordPropositionsDTO wordPropositions = new WordPropositionsDTO();
      String              stringValue      = String.valueOf(number.getValue());
      List<Number> possibleOthers = DB.NUMBERS.stream().filter(n -> n.getValue() != number.getValue())
                                              .filter(n ->
                                                          (stringValue.charAt(0) == String.valueOf(n.getValue())
                                                                                          .charAt(String.valueOf(n.getValue()).length()
                                                                                                  - 1)
                                                           || (stringValue.charAt(0) == String.valueOf(n.getValue()).charAt(0))
                                                          ))
                                              .filter(n -> (stringValue.contains("50") && String.valueOf(n.getValue()).contains("50") ||
                                                            !stringValue.contains("50") && !String.valueOf(n.getValue()).contains("50")
                                              ))
                                              .collect(Collectors.toList());
      wordPropositions.getFr().add(number.getValues().get(0).getTranslationValue(Lang.FR));
      wordPropositions.getDz().add(number.getValues().get(0).getTranslationValue(Lang.DZ));
      int maxTry = 30;
      int nbTry  = 0;
      while (wordPropositions.getFr().size() < nbPropositions && nbTry < maxTry) {
        Word randomNumber = possibleOthers.stream().skip(RANDOM.nextInt(possibleOthers.size())).findFirst().get().getValues().get(0);
        if (!wordPropositions.getFr().contains(randomNumber.getTranslationValue(Lang.FR))) {
          wordPropositions.getFr().add(randomNumber.getTranslationValue(Lang.FR));
          wordPropositions.getDz().add(randomNumber.getTranslationValue(Lang.DZ));
        }
        nbTry++;
      }
      if (nbTry >= maxTry) {
        while (wordPropositions.getFr().size() < nbPropositions) {
          Word randomNumber = DB.NUMBERS.stream().skip(RANDOM.nextInt(DB.NUMBERS.size())).findFirst().get().getValues().get(0);
          if (!wordPropositions.getFr().contains(randomNumber.getTranslationValue(Lang.FR))) {
            wordPropositions.getFr().add(randomNumber.getTranslationValue(Lang.FR));
            wordPropositions.getDz().add(randomNumber.getTranslationValue(Lang.DZ));
          }
          nbTry++;
        }
      }
      sentenceDTO.setWordPropositions(wordPropositions);
      System.out.println(OBJECT_MAPPER.writeValueAsString(sentenceDTO));
    }
  }
}
