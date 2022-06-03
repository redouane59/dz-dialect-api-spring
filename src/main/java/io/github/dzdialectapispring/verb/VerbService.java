package io.github.dzdialectapispring.verb;

import static io.github.dzdialectapispring.other.Config.RANDOM;

import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.PossessiveWord;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.other.enumerations.Tense;
import io.github.dzdialectapispring.pronoun.PronounService;
import io.github.dzdialectapispring.sentence.Sentence;
import io.github.dzdialectapispring.sentence.Sentence.SentenceContent;
import io.github.dzdialectapispring.sentence.SentenceSchema;
import io.github.dzdialectapispring.verb.conjugation.Conjugation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Data
public class VerbService {

  private final VerbRepository verbRepository;

  @Autowired
  private PronounService pronounService;

  public Set<String> getAllVerbIds() {
    return verbRepository.findAll().stream().map(AbstractWord::getId).collect(Collectors.toSet());
  }

  public Set<String> getAvailableTenses(String verbId) {
    Optional<Verb> verbOptional = verbRepository.findById(verbId);
    if (verbOptional.isEmpty()) {
      throw new IllegalArgumentException("No verb found with id " + verbId);
    }
    return verbOptional.get()
                       .getValues()
                       .stream()
                       .map(v -> (Conjugation) v)
                       .map(c -> c.getSubtense().getTense().getId())
                       .collect(Collectors.toSet());
  }

  public Set<Verb> getAllVerbs() {
    return new HashSet<>(verbRepository.findAll());
  }

  public List<Sentence> getVerbConjugationsById(final String verbId, String tenseId) {
    Optional<Verb> verbOptional = verbRepository.findById(verbId);
    if (verbOptional.isEmpty()) {
      throw new IllegalArgumentException("No verb found with id " + verbId);
    }
    List<Conjugation> conjugations = verbOptional.get().getValues().stream().map(v -> (Conjugation) v).collect(Collectors.toList());
    if (tenseId != null) {
      Optional<Tense> tenseOptional = Arrays.stream(Tense.values()).filter(t -> t.getId().equals(tenseId)).findFirst();
      if (tenseOptional.isEmpty()) {
        throw new IllegalArgumentException("No tense found with id " + tenseId);
      }
      conjugations = conjugations.stream().filter(c -> c.getSubtense().getTense() == tenseOptional.get()).collect(Collectors.toList());
    }
    List<Sentence> result = new ArrayList();
    for (Conjugation conjugation : conjugations) {
      PossessiveWord pronoun   = pronounService.getPronoun(conjugation.getGender(), conjugation.isSingular(), conjugation.getPossession());
      String         frValue   = pronoun.getFrTranslation() + " " + conjugation.getFrTranslation();
      String         dzValue   = pronoun.getDzTranslation() + " " + conjugation.getDzTranslation();
      String         dzValueAr = pronoun.getDzTranslationAr() + " " + conjugation.getDzTranslationAr();
      Sentence       sentence  = new Sentence(List.of(new Translation(Lang.FR, frValue), new Translation(Lang.DZ, dzValue, dzValueAr)));
      sentence.setContent(SentenceContent.builder().subtense(conjugation.getSubtense()).abstractVerb(verbOptional.get()).build());
      result.add(sentence);
    }
    return result;
  }

  public Optional<Verb> getRandomAbstractVerb(SentenceSchema schema) {
    Set<Verb> verbs = getAllVerbs();
    if (schema.getTenses() != null) {
      verbs = verbs.stream()
                   .filter(v -> v.getValues()
                                 .stream()
                                 .map(o -> (Conjugation) o)
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
/*    if (schema.getFrSequence().contains(WordType.QUESTION)) {
      verbs = verbs.stream().filter(v -> v.getPossibleQuestionIds().contains(question.getId())).collect(Collectors.toSet());
      if (verbs.size() == 0) {
        System.out.println("no verb found based on question");
      }
    }*/
/*    if (schema.getFrSequence().contains(WordType.SUFFIX)) {
      if (schema.getFrSequence().contains(WordType.NOUN)) {
        verbs = verbs.stream().filter(Verb::isIndirectComplement)
                     .collect(Collectors.toSet());
      } else {
        verbs = verbs.stream().filter(Verb::isDirectComplement).collect(Collectors.toSet());
      }
      if (verbs.size() == 0) {
        System.out.println("no verb found based on suffix");
      }
    }*/
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


}
