package io.github.dzdialectapispring.sentence;

import static io.github.dzdialectapispring.other.Config.OBJECT_MAPPER;

import io.github.dzdialectapispring.other.enumerations.RootTense;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class SentenceService {

  private final SentenceRepository sentenceRepository;
  private       SentenceBuilder    sentenceBuilder;

  public SentenceService(SentenceRepository sentenceRepository) {
    this.sentenceRepository = sentenceRepository;
  }

  public List<Sentence> generateRandomSentences(Integer count, RootTense tense) {
    if (count == null) {
      count = 1;
    } else if (count <= 0) {
      throw new IllegalArgumentException("count argument should be positive");
    } else if (count > 30) {
      throw new IllegalArgumentException("count argument should be less than 30");
    }
    System.out.println("generating " + count + " random sentences");
    List<Sentence> result = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      result.add(generateRandomSentence(tense));
    }
    return result;
  }

  // @todo to implement
  public Sentence generateRandomSentence(RootTense tense) {
    SentenceSchema sentenceSchema = null;
    try {
      sentenceSchema  = OBJECT_MAPPER.readValue(new File("./src/main/resources/static/sentence_schemas/pv_sentence.json"), SentenceSchema.class);
      sentenceBuilder = new SentenceBuilder(sentenceSchema);
      return sentenceBuilder.generate().get();
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
