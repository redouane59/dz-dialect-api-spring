package io.github.dzdialectapispring.verb;

import static io.github.dzdialectapispring.other.Config.RANDOM;

import io.github.dzdialectapispring.DB;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.PossessiveWord;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.other.enumerations.Tense;
import io.github.dzdialectapispring.other.enumerations.WordType;
import io.github.dzdialectapispring.pronoun.AbstractPronoun;
import io.github.dzdialectapispring.pronoun.PronounService;
import io.github.dzdialectapispring.question.AbstractQuestion;
import io.github.dzdialectapispring.sentence.Sentence;
import io.github.dzdialectapispring.sentence.Sentence.SentenceContent;
import io.github.dzdialectapispring.sentence.SentenceDTO;
import io.github.dzdialectapispring.sentence.SentenceSchema;
import io.github.dzdialectapispring.verb.conjugation.Conjugation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Data
@Slf4j
public class VerbService {

  @Autowired
  private PronounService pronounService;

  public Set<String> getAllVerbIds() {
    return DB.VERBS.stream().map(AbstractWord::getId).collect(Collectors.toSet());
  }

  public Set<String> getAvailableTenses(String verbId) {
    Verb verb = getVerbById(verbId);
    return verb
        .getValues()
        .stream()
        .map(c -> c.getSubtense().getTense().getId())
        .collect(Collectors.toSet());
  }

  public List<SentenceDTO> getVerbConjugationsById(final String verbId, String tenseId) {
    Verb              verb         = getVerbById(verbId);
    List<Conjugation> conjugations = verb.getValues();
    if (tenseId != null) {
      Optional<Tense> tenseOptional = Arrays.stream(Tense.values()).filter(t -> t.getId().equals(tenseId)).findFirst();
      if (tenseOptional.isEmpty()) {
        throw new IllegalArgumentException("No tense found with id " + tenseId);
      }
      conjugations = conjugations.stream().filter(c -> c.getSubtense().getTense() == tenseOptional.get()).collect(Collectors.toList());
    }
    List<SentenceDTO> result = new ArrayList();
    for (Conjugation conjugation : conjugations) {
      AbstractPronoun
          abstractPronoun =
          pronounService.getAbstractPronoun(conjugation.getGender(), conjugation.isSingular(), conjugation.getPossession());
      PossessiveWord pronoun   = abstractPronoun.getValues().get(0);
      String         frValue   = "";
      String         dzValue   = "";
      String         dzValueAr = "";
      if (conjugation.getSubtense().getTense() != Tense.IMPERATIVE) {
        frValue += pronoun.getTranslationValue(Lang.FR) + " ";
        dzValue += pronoun.getTranslationValue(Lang.DZ) + " ";
        dzValueAr += pronoun.getTranslationValueAr(Lang.DZ) + " ";
      }
      frValue += conjugation.getTranslationValue(Lang.FR);
      dzValue += conjugation.getTranslationValue(Lang.DZ);
      dzValueAr += conjugation.getTranslationValueAr(Lang.DZ);
      Sentence sentence = new Sentence(List.of(new Translation(Lang.FR, frValue), new Translation(Lang.DZ, dzValue, dzValueAr)));
      sentence.setContent(SentenceContent.builder()
                                         .subtense(conjugation.getSubtense())
                                         .abstractPronoun(abstractPronoun)
                                         .abstractVerb(verb)
                                         .build());
      result.add(new SentenceDTO(sentence));
    }
    return result;
  }

  public Optional<Verb> getRandomAbstractVerb(SentenceSchema schema, AbstractQuestion question) {
    Set<Verb> verbs = DB.VERBS;
    if (schema.getTenses() != null) {
      verbs = verbs.stream()
                   .filter(v -> v.getValues()
                                 .stream()
                                 .map(Conjugation::getSubtense)
                                 .anyMatch(c -> schema.getTenses().contains(c.getTense()))).collect(
              Collectors.toSet());
      if (verbs.size() == 0) {
        System.out.println("no verb found based on tenses");
      }
    }
    if (schema.getVerbType() != null) {
      verbs = verbs.stream().filter(v -> v.getVerbType() == schema.getVerbType()).collect(Collectors.toSet());
      if (verbs.size() == 0) {
        System.out.println("no verb found based on type (" + schema.getVerbType() + " expected)");
      }
    }
    if (schema.getFrSequence().contains(WordType.QUESTION)) {
      verbs = verbs.stream().filter(v -> v.getPossibleQuestionIds().contains(question.getId())).collect(Collectors.toSet());
      if (verbs.size() == 0) {
        System.out.println("no verb found based on question");
      }
    }
    if (schema.getFrSequence().contains(WordType.SUFFIX)) {
      if (schema.getFrSequence().contains(WordType.NOUN)) {
        verbs = verbs.stream().filter(Verb::isIndirectComplement)
                     .collect(Collectors.toSet());
      } else {
        verbs = verbs.stream().filter(Verb::isDirectComplement).collect(Collectors.toSet());
      }
      if (verbs.size() == 0) {
        System.out.println("no verb found based on suffix");
      }
    }
/*    if (schema.getFrSequence().contains(WordType.NOUN)) {
      verbs = verbs.stream().filter(v -> !v.getPossibleComplements().isEmpty())
                   .filter(v -> v.getPossibleComplements().size() > 1 || !v.getPossibleComplements().contains(NounType.ADVERB)) // @todo dirty
                   .collect(Collectors.toSet());
      if (verbs.size() == 0) {
        System.out.println("no verb found based on noun complements");
      }
    }*/
    if (verbs.size() == 0) {
      return Optional.empty();
    }
    return verbs.stream().skip(RANDOM.nextInt(verbs.size())).findFirst();
  }

  public Verb getVerbById(final String verbId) {
    Optional<Verb> verbOpt = DB.VERBS.stream().filter(v -> v.getId().equals(verbId)).findFirst();
    return verbOpt.orElseThrow(() -> new IllegalArgumentException("no verb found with id " + verbId));
  }


}
