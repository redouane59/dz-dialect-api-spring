package io.github.dzdialectapispring.generators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.dzdialectapispring.DB;
import io.github.dzdialectapispring.DzDialectApiSpringApplication;
import io.github.dzdialectapispring.other.enumerations.Tense;
import io.github.dzdialectapispring.sentence.GeneratorParameters;
import io.github.dzdialectapispring.sentence.SentenceDTO;
import io.github.dzdialectapispring.sentence.SentenceSchema;
import io.github.dzdialectapispring.sentence.SentenceService;
import io.github.dzdialectapispring.verb.Verb;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SentenceGeneratorTest {


  private final Random          RAND = new Random();
  @Autowired
  private       SentenceService sentenceService;

  @BeforeAll
  public static void init() throws IOException {
    DzDialectApiSpringApplication.main(new String[]{});
  }

  @Test
  public void testAllSentenceSchemas() throws ExecutionException, InterruptedException {
    GeneratorParameters generatorParameters = GeneratorParameters.builder().build();
    for (SentenceSchema sentenceSchema : DB.SENTENCE_SCHEMAS.stream().filter(s -> s.isEnabled()).collect(Collectors.toSet())) {
      generatorParameters.setSentenceSchema(sentenceSchema);
      List<SentenceDTO> sentences = sentenceService.generateRandomSentences(1,
                                                                            1,
                                                                            null,
                                                                            null,
                                                                            null,
                                                                            null,
                                                                            null,
                                                                            null,
                                                                            null,
                                                                            true,
                                                                            true,
                                                                            sentenceSchema.getId());
      if (sentences.isEmpty()) {
        System.err.println("Empty sentences for schema : " + sentenceSchema.getId());
      }
      assertTrue(sentences.size() > 0);
      assertNotNull(sentences.get(0).getFr());
      assertNotNull(sentences.get(0).getDz());
      assertNotNull(sentences.get(0).getDzAr());
      System.out.println(sentenceSchema.getId() + " : " + sentences.get(0).getFr() + " -> " + sentences.get(0).getDz());
    }
  }

  @ParameterizedTest
  @CsvSource({"PV,false"})
  public void testAllVerbsPV(String sentenceSchemaId, Boolean excludeNegative) throws ExecutionException, InterruptedException {
    GeneratorParameters generatorParameters = GeneratorParameters.builder().build();
    for (Verb verb : DB.VERBS) {
      generatorParameters.setAbstractVerb(verb);
      int count = 1;
      List<SentenceDTO> sentences = sentenceService.generateRandomSentences(count,
                                                                            1,
                                                                            null,
                                                                            verb.getId(),
                                                                            Tense.PRESENT.getId(),
                                                                            null,
                                                                            null,
                                                                            null,
                                                                            null,
                                                                            false,
                                                                            excludeNegative,
                                                                            sentenceSchemaId);
      if (sentences.isEmpty()) {
        System.err.println("Empty sentences for verb : " + verb.getId());
      }
      assertEquals(count, sentences.size());
      assertNotNull(sentences.get(0).getFr());
      assertNotNull(sentences.get(0).getDz());
      assertNotNull(sentences.get(0).getDzAr());
      System.out.println(verb.getId() + " : " + sentences.get(0).getFr() + " -> " + sentences.get(0).getDz());
    }
  }

  @ParameterizedTest
  @CsvSource({"PVA_TEMP,true", "PVA_DEF,true", "PVA_TEMP,false", "PVA_DEF,false"})
  public void testAllAdjectivesPVA_TEMP(String sentenceSchemaId, Boolean excludeNegative) throws ExecutionException, InterruptedException {
    GeneratorParameters generatorParameters = GeneratorParameters.builder().build(); // todo no generator param for adj
    for (int i = 0; i < DB.ADJECTIVES.size() / 4; i++) {
      List<SentenceDTO> sentences = sentenceService.generateRandomSentences(1,
                                                                            1,
                                                                            null,
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
      assertNotNull(sentences.get(0).getFr());
      assertNotNull(sentences.get(0).getDz());
      assertNotNull(sentences.get(0).getDzAr());
      System.out.println(sentenceSchemaId + " : " + sentences.get(0).getFr() + " -> " + sentences.get(0).getDz());
    }
  }

}
