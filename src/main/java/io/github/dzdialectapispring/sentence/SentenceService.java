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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SentenceService {

  private final SentenceRepository sentenceRepository;
  private       SentenceBuilder    sentenceBuilder;
  @Autowired
  private       VerbService        verbService;
  @Autowired
  private       PronounService     pronounService;

  public SentenceService(SentenceRepository sentenceRepository) {
    this.sentenceRepository = sentenceRepository;
  }

  public List<Sentence> generateRandomSentences(Integer count, String pronounId, String verbId, String tenseId) {
    if (count == null) {
      count = 1;
    } else if (count <= 0) {
      throw new IllegalArgumentException("count argument should be positive");
    } else if (count > 30) {
      throw new IllegalArgumentException("count argument should be less than 30");
    }
    Verb verb = null;
    if (verbId != null) {
      Optional<Verb> verbOptional = verbService.getVerbRepository().findById(verbId);
      if (verbOptional.isEmpty()) {
        throw new IllegalStateException("no verb found with id " + verbId);
      } else {
        verb = verbOptional.get();
      }
    }
    AbstractPronoun pronoun = null;
    if (pronounId != null) {
      Optional<AbstractPronoun> pronounOptional = pronounService.getPronounRepository().findById(pronounId);
      if (pronounOptional.isEmpty()) {
        throw new IllegalStateException("no pronoun found with id " + pronounId);
      } else {
        pronoun = pronounOptional.get();
      }
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
    System.out.println("generating " + count + " random sentences");
    List<Sentence> result = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      result.add(generateRandomSentence(pronoun, verb, tense));
    }
    return result;
  }

  // @todo to implement
  public Sentence generateRandomSentence(AbstractPronoun pronoun, Verb verb, Tense tense) {
    SentenceSchema sentenceSchema = null;
    try {
      sentenceSchema  = OBJECT_MAPPER.readValue(new File("./src/main/resources/static/sentence_schemas/pv_sentence.json"), SentenceSchema.class);
      sentenceBuilder = new SentenceBuilder(sentenceSchema, pronounService, verbService);
      return sentenceBuilder.generate(pronoun, verb, tense).get();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public Sentence getSentenceById(final String id) {
    Optional<Sentence> sentenceOpt = sentenceRepository.findById(id);
    if (sentenceOpt.isEmpty()) {
      throw new IllegalArgumentException("No sentence found with id " + id);
    }
    return sentenceRepository.findById(id).get();
  }
}
