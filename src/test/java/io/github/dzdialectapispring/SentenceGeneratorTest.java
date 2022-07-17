package io.github.dzdialectapispring;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

  @Test
  public void testAllVerbsPV() throws ExecutionException, InterruptedException {
    GeneratorParameters generatorParameters = GeneratorParameters.builder().build();
    //  List<SentenceSchema> compatibleSchemas   = DB.SENTENCE_SCHEMAS.stream().filter(s -> s.getId().contains("V")).collect(Collectors.toList());
    for (Verb verb : DB.VERBS) {
      generatorParameters.setAbstractVerb(verb);
      List<SentenceDTO> sentences = sentenceService.generateRandomSentences(1,
                                                                            1,
                                                                            null,
                                                                            verb.getId(),
                                                                            null,
                                                                            null,
                                                                            null,
                                                                            null,
                                                                            null,
                                                                            true,
                                                                            true,
                                                                            "PV");
      if (sentences.isEmpty()) {
        System.err.println("Empty sentences for verb : " + verb.getId());
      }
      assertTrue(sentences.size() > 0);
      assertNotNull(sentences.get(0).getFr());
      assertNotNull(sentences.get(0).getDz());
      assertNotNull(sentences.get(0).getDzAr());
      System.out.println(verb.getId() + " : " + sentences.get(0).getFr() + " -> " + sentences.get(0).getDz());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "PV,,,,,,1",
      "NV,,,,,,1",
      "PVA_TEMP,,,,,,1",
      "PVA_DEF,,,,,,1",
      "NVA_TEMP,,,,,,1",
      "NVA_DEF,,,,,,1",
  })
  void testGenerateSentence(String sentenceSchemaId, String abstractPronounId, String abstractVerbId, String tenseId,
                            Boolean excludeNegative, Boolean excludePositive, Integer alternativeCount) {
/*    sentenceBuilder = new SentenceBuilder(pronounService, verbService, questionService,
                                          adjectiveService, adverbService, nounService);

    GeneratorParameters generatorParameters = GeneratorParameters.builder().build();

    Optional<SentenceSchema> sentenceSchemaOpt = sentenceService.getSentenceSchemaById(sentenceSchemaId);
    generatorParameters.setSentenceSchema(sentenceSchemaOpt.get());

    Optional<AbstractPronoun> abstractPronounOpt = pronounService.getPronounById(abstractPronounId);
    abstractPronounOpt.ifPresent(generatorParameters::setAbstractPronoun);

    Optional<Verb> abstractVerbOpt = verbService.getVerbById(abstractVerbId);
    abstractVerbOpt.ifPresent(generatorParameters::setAbstractVerb);

    if (tenseId != null && !tenseId.isEmpty()) {
      generatorParameters.setTense(Tense.valueOf(tenseId));
    }

    if (excludeNegative != null) {
      generatorParameters.setExcludeNegative(excludeNegative);
    }
    if (excludePositive != null) {
      generatorParameters.setExcludePositive(excludePositive);
    }
    generatorParameters.setAlternativeCount(alternativeCount);
    Optional<Sentence> sentence = sentenceBuilder.generate(generatorParameters);
    assertTrue(sentence.isPresent());
    System.out.println(sentence.get().getTranslationValue(Lang.FR) + " -> " + sentence.get().getTranslationValue(Lang.DZ));*/
  }
}
