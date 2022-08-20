package io.github.dzdialectapispring.number;

import static io.github.dzdialectapispring.other.Config.RANDOM;

import io.github.dzdialectapispring.DB;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.concrets.Word;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.sentence.Sentence;
import io.github.dzdialectapispring.sentence.SentenceDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;


@Service
public class NumberService {

  public Set<String> getAllNumbersIds() {
    return DB.NUMBERS.stream().map(AbstractWord::getId).collect(Collectors.toSet());
  }

  public Number getNumberById(final String numberId) {
    Optional<Number> NumberOpt = DB.NUMBERS.stream().filter(a -> a.getId().equals(numberId)).findFirst();
    return NumberOpt.orElseThrow(() -> new IllegalArgumentException("no Number found with id " + numberId));
  }

  public List<SentenceDTO> getNumberValuesById(final String NumberId) {
    Number            Number = getNumberById(NumberId);
    List<SentenceDTO> result = new ArrayList<>();
    for (Word word : Number.getValues()) {
      Sentence sentence = new Sentence(List.of(new Translation(Lang.FR, word.getTranslationValue(Lang.FR)),
                                               new Translation(Lang.DZ, word.getTranslationValue(Lang.DZ), word.getTranslationValueAr(Lang.DZ))));
      result.add(new SentenceDTO(sentence));
    }
    return result;
  }

  public AbstractWord getRandomNumber() {
    return DB.NUMBERS.stream().skip(RANDOM.nextInt(DB.NUMBERS.size())).findFirst().get();
  }
}
