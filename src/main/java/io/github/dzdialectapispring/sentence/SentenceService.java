package io.github.dzdialectapispring.sentence;

import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.other.enumerations.RootTense;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SentenceService {

  private final SentenceRepository sentenceRepository;

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
    return new Sentence(List.of(new Translation(Lang.FR, "Bonjour"), new Translation(Lang.DZ, "Salam Alakyoum", "____")));
  }

  public Sentence getSentenceById(final String id) {
    Optional<Sentence> sentenceOpt = sentenceRepository.findById(id);
    if (sentenceOpt.isEmpty()) {
      throw new IllegalArgumentException("No sentence found with id " + id);
    }
    return sentenceRepository.findById(id).get();
  }
}
