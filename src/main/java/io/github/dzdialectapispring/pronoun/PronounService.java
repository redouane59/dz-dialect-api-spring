package io.github.dzdialectapispring.pronoun;

import static io.github.dzdialectapispring.other.Config.RANDOM;

import io.github.dzdialectapispring.other.concrets.Possession;
import io.github.dzdialectapispring.other.concrets.PossessiveWord;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.enumerations.Gender;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.sentence.Sentence;
import io.github.dzdialectapispring.sentence.Sentence.SentenceContent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Data
public class PronounService {

  private final PronounRepository pronounRepository;

  public AbstractPronoun getAbstractPronoun(final Gender gender, final boolean isSingular, final Possession possession) {
    Optional<AbstractPronoun> abstractPronounOptional = pronounRepository.findAll().stream()
                                                                         .filter(p -> ((PossessiveWord) p.getValues().get(0)).getPossession()
                                                                                      == possession)
                                                                         .filter(p -> ((PossessiveWord) p.getValues().get(0)).isSingular()
                                                                                      == isSingular)
                                                                         .filter(p -> ((PossessiveWord) p.getValues().get(0)).getGender() == gender
                                                                                      || ((PossessiveWord) p.getValues().get(0)).getGender()
                                                                                         == Gender.X
                                                                                      || gender == Gender.X)
                                                                         .findFirst();
    if (abstractPronounOptional.isEmpty()) {
      throw new IllegalStateException("no abstract pronoun found");
    }
    return abstractPronounOptional.get();
  }

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
    List<AbstractPronoun> abtractPronouns = pronounRepository.findAll();
    List<Sentence>        result          = new ArrayList<>();
    for (AbstractPronoun abstractPronoun : abtractPronouns) {
      for (Object word : abstractPronoun.getValues()) {
        PossessiveWord possessiveWord = (PossessiveWord) word;
        Sentence sentence = new Sentence(List.of(new Translation(Lang.FR, possessiveWord.getFrTranslation()),
                                                 new Translation(Lang.DZ, possessiveWord.getDzTranslation(), possessiveWord.getDzTranslationAr())));
        sentence.setContent(SentenceContent.builder().abstractPronoun(abstractPronoun).build());
        result.add(sentence);
      }
    }

    return result;
  }

  public PossessiveWord getRandomPronoun() {
    List<AbstractPronoun> pronouns        = pronounRepository.findAll();
    AbstractPronoun       abstractPronoun = pronouns.stream().skip(RANDOM.nextInt(pronouns.size())).findFirst().get();
    return (PossessiveWord) abstractPronoun.getValues().get(0);
  }

  public AbstractPronoun getRandomAbstractPronoun(Possession possession) {
    List<AbstractPronoun> pronouns = pronounRepository.findAll().stream()
                                                      .filter(p -> ((PossessiveWord) p.getValues().get(0)).getPossession() == possession)
                                                      .collect(Collectors.toList());
    return pronouns.stream().skip(RANDOM.nextInt(pronouns.size())).findFirst().get();
  }

  public AbstractPronoun getRandomAbstractPronoun() {
    List<AbstractPronoun> pronouns = pronounRepository.findAll();
    return pronouns.stream().skip(RANDOM.nextInt(pronouns.size())).findFirst().get();
  }
}
