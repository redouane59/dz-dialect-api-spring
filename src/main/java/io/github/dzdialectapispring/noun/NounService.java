package io.github.dzdialectapispring.noun;

import static io.github.dzdialectapispring.other.Config.RANDOM;

import io.github.dzdialectapispring.DB;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.GenderedWord;
import io.github.dzdialectapispring.sentence.WordDTO;
import io.github.dzdialectapispring.verb.Verb;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;


@Service
public class NounService {

  public Set<String> getAllNounsIds() {
    return DB.NOUNS.stream().map(AbstractWord::getId).collect(Collectors.toSet());
  }

  public Noun getNounById(final String nounId) {
    Optional<Noun> nounOpt = DB.NOUNS.stream().filter(n -> n.getId().equals(nounId)).findFirst();
    return nounOpt.orElseThrow(() -> new IllegalArgumentException("no noun found with id " + nounId));
  }

  public List<WordDTO> getNounValuesById(final String nounId) {
    Noun          noun   = getNounById(nounId);
    List<WordDTO> result = new ArrayList<>();
    for (GenderedWord word : noun.getValues()) {
      result.add(new WordDTO(word));
    }
    return result;
  }

  public Optional<Noun> getRandomNoun(Verb abstractVerb) {
    Set<Noun> nouns = DB.NOUNS;
    // case where the noun is the complement
    if (abstractVerb != null) {
      nouns = nouns.stream().filter(n -> abstractVerb.getPossibleComplements().contains(n.getType())).collect(Collectors.toSet());
    }
    if (nouns.isEmpty()) {
      return Optional.empty();
    }
    return nouns.stream().skip(RANDOM.nextInt(nouns.size())).findFirst();
  }
}
