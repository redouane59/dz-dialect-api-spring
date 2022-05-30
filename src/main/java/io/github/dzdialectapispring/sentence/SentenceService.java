package io.github.dzdialectapispring.sentence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SentenceService {

  private final SentenceRepository sentenceRepository;

  // @todo the random sentence generator should be called there ?
  public Sentence generateRandomSentence(){
    return new Sentence(Set.of(new Translation(Lang.FR,"hello world"),new Translation(Lang.DZ,"Salam Alakyoum")));
  }

  public Sentence getSentenceById(final String id) {
    Optional<Sentence> sentenceOpt = sentenceRepository.findById(id);
    if(sentenceOpt.isEmpty()){
      throw new IllegalArgumentException("No sentence found with id " + id);
    }
    return sentenceRepository.findById(id).get();
  }
}
