package io.github.dzdialectapispring.adverb;

import static io.github.dzdialectapispring.other.Config.RANDOM;

import io.github.dzdialectapispring.DB;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.concrets.Word;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.sentence.Sentence;
import io.github.dzdialectapispring.sentence.SentenceDTO;
import io.github.dzdialectapispring.verb.Verb;
import io.github.dzdialectapispring.verb.VerbType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;


@Service
public class AdverbService {

  public Set<String> getAllAdverbsIds() {
    return DB.ADVERBS.stream().map(AbstractWord::getId).collect(Collectors.toSet());
  }

  public Adverb getAdverbById(final String adverbId) {
    Optional<Adverb> adverbOpt = DB.ADVERBS.stream().filter(a -> a.getId().equals(adverbId)).findFirst();
    return adverbOpt.orElseThrow(() -> new IllegalArgumentException("no adverb found with id " + adverbId));
  }

  public List<SentenceDTO> getAdverbValuesById(final String adverbId) {
    Adverb            adverb = getAdverbById(adverbId);
    List<SentenceDTO> result = new ArrayList<>();
    for (Word word : adverb.getValues()) {
      Sentence sentence = new Sentence(List.of(new Translation(Lang.FR, word.getTranslationValue(Lang.FR)),
                                               new Translation(Lang.DZ, word.getTranslationValue(Lang.DZ), word.getTranslationValueAr(Lang.DZ))));
      result.add(new SentenceDTO(sentence));
    }
    return result;
  }

  public AbstractWord getRandomAdverb(Verb verb) {
    Set<Adverb> adverbs = DB.ADVERBS;
    if (verb != null) {
      adverbs = adverbs.stream().filter(a -> !a.isExcludeStateVerbs() || verb.getVerbType() == VerbType.STATE
      ).collect(Collectors.toSet());
    }
    return adverbs.stream().skip(RANDOM.nextInt(adverbs.size())).findFirst().get();
  }
}
