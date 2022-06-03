package io.github.dzdialectapispring.pronoun;

import static io.github.dzdialectapispring.other.Config.RANDOM;

import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.Possession;
import io.github.dzdialectapispring.other.concrets.PossessiveWord;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.concrets.Word;
import io.github.dzdialectapispring.other.enumerations.Gender;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.sentence.Sentence;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

  public List<Sentence> getAllPronouns() {
    List<? super Word> values = pronounRepository.findAll().stream().map(AbstractWord::getValues).collect(Collectors.toList());
    List<Sentence>     result = new ArrayList<>();
    for (Object word : values) {
      PossessiveWord possessiveWord = (PossessiveWord) ((ArrayList) word).get(0);
      Sentence sentence = new Sentence(List.of(new Translation(Lang.FR, possessiveWord.getFrTranslation()),
                                               new Translation(Lang.DZ, possessiveWord.getDzTranslation(), possessiveWord.getDzTranslationAr())));
      result.add(sentence);
    }
    return result;
  }

  public PossessiveWord getRandomPronoun() {
    List<AbstractPronoun> pronouns        = pronounRepository.findAll();
    AbstractPronoun       abstractPronoun = pronouns.stream().skip(RANDOM.nextInt(pronouns.size())).findFirst().get();
    return (PossessiveWord) abstractPronoun.getValues().get(0);
  }
}
