package io.github.dzdialectapispring.sentence;

import static io.github.dzdialectapispring.other.Config.OBJECT_MAPPER;

import io.github.dzdialectapispring.other.enumerations.Tense;
import io.github.dzdialectapispring.pronoun.AbstractPronoun;
import io.github.dzdialectapispring.pronoun.PronounService;
import io.github.dzdialectapispring.verb.Verb;
import io.github.dzdialectapispring.verb.VerbService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SentenceService {

  private SentenceBuilder sentenceBuilder;
  @Autowired
  private VerbService     verbService;
  @Autowired
  private PronounService  pronounService;

  public List<SentenceDTO> generateRandomSentences(Integer count,
                                                   String pronounId,
                                                   String verbId,
                                                   String tenseId,
                                                   String nounId,
                                                   String adjectiveId,
                                                   String questionId,
                                                   String adverbId,
                                                   boolean positive,
                                                   boolean negative) throws ExecutionException, InterruptedException {
    if (count == null) {
      count = 1;
    } else if (count <= 0) {
      throw new IllegalArgumentException("count argument should be positive");
    } else if (count > 30) {
      throw new IllegalArgumentException("count argument should be less than 30");
    }

    GeneratorParameters
        generatorParameters =
        buildParameters(pronounId, verbId, tenseId, nounId, adjectiveId, questionId, adverbId, positive, negative);
    List<SentenceDTO> result = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      result.add(new SentenceDTO(generateRandomSentence(generatorParameters)));
    }
    return result;
  }

  public GeneratorParameters buildParameters(String pronounId, String verbId, String tenseId, String nounId,
                                             String adjectiveId,
                                             String questionId,
                                             String adverbId,
                                             boolean excludePositive,
                                             boolean excludeNegative) throws ExecutionException, InterruptedException {
    Verb verb = null;
    if (verbId != null) {
      verb = verbService.getVerbById(verbId);
    }
    AbstractPronoun pronoun = null;
    if (pronounId != null) {
      pronoun = pronounService.getPronounById(pronounId);
    }
    Tense tense = null;
    if (tenseId != null) {
      Optional<Tense> tenseOptional = Tense.findById(tenseId);
      if (tenseOptional.isEmpty()) {
        throw new IllegalStateException("no tense found with id " + tenseId);
      } else {
        tense = tenseOptional.get();
      }
    }
    return GeneratorParameters.builder()
                              .abstractPronoun(pronoun)
                              .abstractVerb(verb)
                              .tense(tense)
                              .excludePositive(excludePositive)
                              .excludeNegative(excludeNegative)
                              .build();
  }

  // @todo to implement
  public Sentence generateRandomSentence(GeneratorParameters generatorParameters) {
    SentenceSchema sentenceSchema = null;
    try {
      sentenceSchema  = OBJECT_MAPPER.readValue(new File("./src/main/resources/static/sentence_schemas/pv_sentence.json"), SentenceSchema.class);
      sentenceBuilder = new SentenceBuilder(sentenceSchema, pronounService, verbService);
      return sentenceBuilder.generate(generatorParameters).get();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public SentenceDTO getSentenceById(final String id) {
    return null;
  }
/*    Optional<Sentence> sentenceOpt = sentenceRepository.findById(id);
    if (sentenceOpt.isEmpty()) {
      throw new IllegalArgumentException("No sentence found with id " + id);
    }
    return new SentenceDTO(sentenceOpt.get());
  }*/
}
