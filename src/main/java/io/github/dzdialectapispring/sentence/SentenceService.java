package io.github.dzdialectapispring.sentence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SentenceService {

  private final SentenceRepository sentenceRepository;

  public Sentence generateRandomSentence(){
    return new Sentence(Set.of(new Translation(Lang.FR,"hello world"),new Translation(Lang.DZ,"Salam Alakyoum")));
  }

}
