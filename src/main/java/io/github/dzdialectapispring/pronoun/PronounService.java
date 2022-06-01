package io.github.dzdialectapispring.pronoun;

import io.github.dzdialectapispring.other.concrets.Possession;
import io.github.dzdialectapispring.other.concrets.PossessiveWord;
import io.github.dzdialectapispring.other.enumerations.Gender;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class PronounService {

  private final PronounRepository pronounRepository;

  public PossessiveWord getPronoun(final Gender gender, final boolean isSingular, final Possession possession) {
    Optional<PossessiveWord> result = pronounRepository.findAll().stream()
                                                       .map(o -> o.getValues().get(0))//@todo dirty ?
                                                       .map(o -> (PossessiveWord) o)
                                                       .filter(o -> o.isSingular() == isSingular)
                                                       .filter(o -> o.getPossession() == possession)
                                                       .filter(o -> o.getGender() == gender || gender == Gender.X || o.getGender() == Gender.X)
                                                       .findAny();
    if (result.isEmpty()) {
      throw new IllegalStateException("no pronoun found");
    }
    return result.get();

  }
}
