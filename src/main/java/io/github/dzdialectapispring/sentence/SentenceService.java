package io.github.dzdialectapispring.sentence;

import io.github.dzdialectapispring.adjective.AdjectiveService;
import io.github.dzdialectapispring.adverb.adjective.AdverbService;
import io.github.dzdialectapispring.generic.ResourceList;
import io.github.dzdialectapispring.other.Config;
import io.github.dzdialectapispring.other.enumerations.Tense;
import io.github.dzdialectapispring.other.enumerations.WordType;
import io.github.dzdialectapispring.pronoun.AbstractPronoun;
import io.github.dzdialectapispring.pronoun.PronounService;
import io.github.dzdialectapispring.question.QuestionService;
import io.github.dzdialectapispring.verb.Verb;
import io.github.dzdialectapispring.verb.VerbService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SentenceService {

  private SentenceBuilder  sentenceBuilder;
  @Autowired
  private VerbService      verbService;
  @Autowired
  private PronounService   pronounService;
  @Autowired
  private QuestionService  questionService;
  @Autowired
  private AdjectiveService adjectiveService;
  @Autowired
  private AdverbService    adverbService;

  public List<SentenceDTO> generateRandomSentences(Integer count,
                                                   String pronounId,
                                                   String verbId,
                                                   String tenseId,
                                                   String nounId,
                                                   String adjectiveId,
                                                   String questionId,
                                                   String adverbId,
                                                   boolean positive,
                                                   boolean negative,
                                                   String sentenceSchemaId) throws ExecutionException, InterruptedException {
    if (count == null) {
      count = 1;
    } else if (count <= 0) {
      throw new IllegalArgumentException("count argument should be positive");
    } else if (count > 30) {
      throw new IllegalArgumentException("count argument should be less than 30");
    }

    GeneratorParameters
        generatorParameters =
        buildParameters(pronounId, verbId, tenseId, nounId, adjectiveId, questionId, adverbId, positive, negative, sentenceSchemaId);
    List<SentenceDTO> result = new ArrayList<>();
    int               i      = 0;
    while (result.size() < count && i < count * 5) { // in case no sentence is generated
      Optional<Sentence> sentence = generateRandomSentence(generatorParameters);
      sentence.ifPresent(value -> result.add(new SentenceDTO(value)));
      i++;
    }
    return result;
  }

  public GeneratorParameters buildParameters(String pronounId, String verbId, String tenseId, String nounId,
                                             String adjectiveId,
                                             String questionId,
                                             String adverbId,
                                             boolean excludePositive,
                                             boolean excludeNegative,
                                             String sentenceSchemaId) throws ExecutionException, InterruptedException {
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
        throw new IllegalArgumentException("no tense found with id " + tenseId);
      }
      tense = tenseOptional.get();
    }
    SentenceSchema schema = null;
    if (sentenceSchemaId != null) {
      Optional<SentenceSchema> sentenceSchemaOptional = getSentenceSchemaById(sentenceSchemaId);
      if (sentenceSchemaOptional.isEmpty()) {
        throw new IllegalArgumentException("no sentence schema found with id " + sentenceSchemaId);
      }
      schema = sentenceSchemaOptional.get();
    }
    return GeneratorParameters.builder()
                              .abstractPronoun(pronoun)
                              .abstractVerb(verb)
                              .tense(tense)
                              .excludePositive(excludePositive)
                              .excludeNegative(excludeNegative)
                              .sentenceSchema(schema)
                              .build();
  }

  public Optional<Sentence> generateRandomSentence(GeneratorParameters generatorParameters) {
    SentenceSchema sentenceSchema;
    if (generatorParameters.getSentenceSchema() == null) {
      Optional<SentenceSchema> sentenceSchemaOpt = getRandomSentenceSchema(generatorParameters);
      if (sentenceSchemaOpt.isEmpty()) {
        return Optional.empty();
      }
      sentenceSchema = sentenceSchemaOpt.get();
    } else {
      sentenceSchema = generatorParameters.getSentenceSchema();
    }
    sentenceBuilder = new SentenceBuilder(sentenceSchema, pronounService, verbService, questionService, adjectiveService, adverbService);
    return sentenceBuilder.generate(generatorParameters);
  }

  public Optional<SentenceSchema> getSentenceSchemaById(String sentenceSchemaId) {
    return getSentenceSchemas().stream().filter(s -> s.getId().equals(sentenceSchemaId)).findFirst();
  }

  public Set<SentenceSchema> getSentenceSchemas() {
    Set<SentenceSchema> sentenceSchemas = new HashSet<>();
    Set<String>         files           = new HashSet<>(ResourceList.getResources(Pattern.compile(".*sentence_schemas.*json")));
    for (String fileName : files) {
      try {
        sentenceSchemas.add(Config.OBJECT_MAPPER.readValue(new File(fileName), SentenceSchema.class));
      } catch (IOException e) {
        System.err.println("could not load file " + fileName + " -> " + e.getMessage());
      }
    }
    return sentenceSchemas;
  }

  // @todo to implement
  public Optional<SentenceSchema> getRandomSentenceSchema(GeneratorParameters generatorParameters) {

    List<SentenceSchema> matchingSentenceSchema = getSentenceSchemas().stream()
                                                                      .filter(SentenceSchema::isEnabled)
                                                                      .collect(Collectors.toList());
    if (generatorParameters.getAbstractVerb() != null) {
      matchingSentenceSchema = matchingSentenceSchema.stream()
                                                     .filter(s -> s.getFrSequence().contains(WordType.VERB))
                                                     .filter(s -> s.getVerbType() == generatorParameters.getAbstractVerb().getVerbType()
                                                                  || s.getVerbType() == null)
                                                     .collect(Collectors.toList());
    }
    if (generatorParameters.getTense() != null) {
      matchingSentenceSchema = matchingSentenceSchema.stream()
                                                     .filter(s -> s.getFrSequence().contains(WordType.VERB))
                                                     .filter(s -> s.getTenses().contains(generatorParameters.getTense()))
                                                     .collect(Collectors.toList());
    }
    if (matchingSentenceSchema.isEmpty()) {
      throw new IllegalArgumentException("no matching sentence schema found from arguments");
    }
    return Optional.of(matchingSentenceSchema.get(new Random().nextInt(matchingSentenceSchema.size())));
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
