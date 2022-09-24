package io.github.dzdialectapispring.generators;

import static io.github.dzdialectapispring.other.Config.OBJECT_MAPPER;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.dzdialectapispring.DB;
import io.github.dzdialectapispring.DzDialectApiSpringApplication;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.enumerations.Tense;
import io.github.dzdialectapispring.sentence.SentenceDTO;
import io.github.dzdialectapispring.sentence.SentenceService;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PGeneratorTest {


  @Autowired
  private SentenceService sentenceService;

  @BeforeAll
  public static void init() throws IOException {
    DzDialectApiSpringApplication.main(new String[]{});
  }

  @ParameterizedTest
  @CsvSource({"P,true"})
  public void testAllAdjectivesPV(String sentenceSchemaId, Boolean excludeNegative)
  throws ExecutionException, InterruptedException, JsonProcessingException {
    List<String> pronounIds = DB.PERSONAL_PRONOUNS.stream().map(AbstractWord::getId).collect(Collectors.toList());
    for (String pronounId : pronounIds) {
      List<SentenceDTO> sentences = sentenceService.generateRandomSentences(1,
                                                                            3,
                                                                            pronounId,
                                                                            null,
                                                                            Tense.PRESENT.getId(),
                                                                            null,
                                                                            null,
                                                                            null,
                                                                            null,
                                                                            false,
                                                                            excludeNegative,
                                                                            sentenceSchemaId);
      if (sentences.isEmpty()) {
        System.err.println("Empty sentences with sentenceSchemaId " + sentenceSchemaId);
      }
      assertTrue(sentences.size() > 0);
      SentenceDTO sentence = sentences.get(0);
      assertNotNull(sentence.getFr());
      assertNotNull(sentence.getDz());
      assertNotNull(sentence.getDzAr());
      System.out.println(OBJECT_MAPPER.writeValueAsString(sentence));
/*        System.out.println(sentenceSchemaId + ";" + adjectiveId + ";" + pronounId + ";"
                           + sentences.get(0).getFr() + ";"
                           + sentences.get(0).getDz() + ";"
                           + sentences.get(0).getDzAr());*/
    }
  }
}
