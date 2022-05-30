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
  public List<Sentence> generateRandomSentences(Integer count){
    if(count==null){
      count = 1;
    } else if (count<=0){
      throw new IllegalArgumentException("count argument should be positive");
    } else if (count>30){
      throw new IllegalArgumentException("count argument should be less than 30");
    }
    System.out.println("generate " + count + " random sentences");
    List<Sentence> result = new ArrayList<>();
    for(int i=0;i<count;i++){
      result.add(new Sentence(Set.of(new Translation(Lang.FR,"Bonjour " + i),new Translation(Lang.DZ,"Salam Alakyoum " + i))));
    }
    return result;
  }

  public Sentence getSentenceById(final String id) {
    Optional<Sentence> sentenceOpt = sentenceRepository.findById(id);
    if(sentenceOpt.isEmpty()){
      throw new IllegalArgumentException("No sentence found with id " + id);
    }
    return sentenceRepository.findById(id).get();
  }
}
