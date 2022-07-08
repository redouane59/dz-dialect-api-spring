package io.github.dzdialectapispring.pronoun;

import static io.github.dzdialectapispring.sentence.SentenceBuilder.RANDOM;

import io.github.dzdialectapispring.DB;
import io.github.dzdialectapispring.other.concrets.Possession;
import io.github.dzdialectapispring.other.concrets.PossessiveWord;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.concrets.Word;
import io.github.dzdialectapispring.other.enumerations.Gender;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.sentence.Sentence;
import io.github.dzdialectapispring.sentence.Sentence.SentenceContent;
import io.github.dzdialectapispring.sentence.SentenceDTO;
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


  public AbstractPronoun getAbstractPronoun(final Gender gender, final boolean isSingular, final Possession possession) {
    Optional<AbstractPronoun> abstractPronounOptional = DB.PERSONAL_PRONOUNS.stream()
                                                                            .filter(p -> (p.getValues().get(0)).getPossession()
                                                                                         == possession)
                                                                            .filter(p -> (p.getValues().get(0)).isSingular()
                                                                                         == isSingular)
                                                                            .filter(p -> (p.getValues().get(0)).getGender() == gender
                                                                                         || (p.getValues().get(0)).getGender()
                                                                                            == Gender.X
                                                                                         || gender == Gender.X)
                                                                            .findFirst();
    if (abstractPronounOptional.isEmpty()) {
      throw new IllegalStateException("no abstract pronoun found");
    }
    return abstractPronounOptional.get();
  }

  public PossessiveWord getPronoun(final Gender gender, final boolean isSingular, final Possession possession) {
    Optional<PossessiveWord> result = DB.PERSONAL_PRONOUNS.stream()
                                                          .map(o -> o.getValues().get(0))//@todo dirty ?
                                                          .filter(o -> o.isSingular() == isSingular)
                                                          .filter(o -> o.getPossession() == possession)
                                                          .filter(o -> o.getGender() == gender || gender == Gender.X || o.getGender() == Gender.X)
                                                          .findAny();
    if (result.isEmpty()) {
      throw new IllegalStateException("no pronoun found");
    }
    return result.get();
  }

/*      Collections.sort(abstractPronouns, new Comparator<AbstractPronoun>() {
    public int compare(AbstractPronoun o1, AbstractPronoun o2) {
      if (o1.getValues().get(0).getIndex() == o2.getValues().get(0).getIndex()) {
        return 0;
      }
      return o1.getValues().get(0).getIndex() < o2.getValues().get(0).getIndex() ? -1 : 1;
    }
  });*/

  public List<SentenceDTO> getAllPronouns() {
    List<SentenceDTO> result = new ArrayList<>();
    for (AbstractPronoun abstractPronoun : DB.PERSONAL_PRONOUNS) {
      for (Word word : abstractPronoun.getValues()) {
        Sentence sentence = new Sentence(List.of(new Translation(Lang.FR, word.getTranslationValue(Lang.FR)),
                                                 new Translation(Lang.DZ, word.getTranslationValue(Lang.DZ), word.getTranslationValueAr(Lang.DZ))));
        sentence.setContent(SentenceContent.builder().abstractPronoun(abstractPronoun).build());
        result.add(new SentenceDTO(sentence));
      }
    }
    return result;
  }

  public PossessiveWord getRandomPronoun() {
    List<AbstractPronoun> pronouns        = DB.PERSONAL_PRONOUNS;
    AbstractPronoun       abstractPronoun = pronouns.stream().skip(RANDOM.nextInt(pronouns.size())).findFirst().get();
    return abstractPronoun.getValues().get(0);
  }

  public AbstractPronoun getRandomAbstractPronoun(Possession possession) {
    List<AbstractPronoun> pronouns = DB.PERSONAL_PRONOUNS.stream()
                                                         .filter(p -> (p.getValues().get(0)).getPossession() == possession)
                                                         .collect(Collectors.toList());
    return pronouns.stream().skip(RANDOM.nextInt(pronouns.size())).findFirst().get();
  }

  public AbstractPronoun getRandomAbstractPronoun() {
    List<AbstractPronoun> pronouns = DB.PERSONAL_PRONOUNS;
    return pronouns.stream().skip(RANDOM.nextInt(pronouns.size())).findFirst().get();
  }


  public AbstractPronoun getPronounById(final String pronounId) {
    Optional<AbstractPronoun> pronounOpt = DB.PERSONAL_PRONOUNS.stream().filter(p -> p.getId().equals(pronounId)).findFirst();
    return pronounOpt.orElseThrow(() -> new IllegalArgumentException("No pronoun found with id " + pronounId));
  }

  public PossessiveWord getRandomImperativePersonalPronoun() {
    List<PossessiveWord> compatiblePronouns = DB.PERSONAL_PRONOUNS.stream()
                                                                  .map(o -> o.getValues().get(0))
                                                                  .filter(o -> o.getPossession() == Possession.YOU).collect(Collectors.toList());
    return compatiblePronouns.stream().skip(RANDOM.nextInt(compatiblePronouns.size()))
                             .findAny().get();
  }

}
