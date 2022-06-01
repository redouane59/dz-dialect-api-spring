package io.github.dzdialectapispring.verb;

import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.PossessiveWord;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.conjugation.Conjugation;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.other.enumerations.RootTense;
import io.github.dzdialectapispring.pronoun.PronounService;
import io.github.dzdialectapispring.sentence.Sentence;
import io.github.dzdialectapispring.sentence.Sentence.SentenceContent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class VerbService {

  private final VerbRepository verbRepository;

  @Autowired
  private PronounService pronounService;

  public Set<String> getAllVerbIds() {
    return verbRepository.findAll().stream().map(AbstractWord::getId).collect(Collectors.toSet());
  }

  public List<Sentence> getVerbConjugationsById(final String id, RootTense rootTense) {
    Optional<Verb> verbOptional = verbRepository.findById(id);
    if (verbOptional.isEmpty()) {
      throw new IllegalArgumentException("No verb found with id " + id);
    }
    List<Conjugation> conjugations = verbOptional.get().getValues().stream().map(v -> (Conjugation) v).collect(Collectors.toList());
    if (rootTense != null) {
      conjugations = conjugations.stream().filter(c -> c.getTense().getRootTense() == rootTense).collect(Collectors.toList());
    }
    List<Sentence> result = new ArrayList();
    for (Conjugation conjugation : conjugations) {
      PossessiveWord pronoun   = pronounService.getPronoun(conjugation.getGender(), conjugation.isSingular(), conjugation.getPossession());
      String         frValue   = pronoun.getFrTranslation() + " " + conjugation.getFrTranslation();
      String         dzValue   = pronoun.getDzTranslation() + " " + conjugation.getDzTranslation();
      String         dzValueAr = pronoun.getDzTranslationAr() + " " + conjugation.getDzTranslationAr();
      Sentence       sentence  = new Sentence(List.of(new Translation(Lang.FR, frValue), new Translation(Lang.DZ, dzValue, dzValueAr)));
      sentence.setContent(SentenceContent.builder().tense(conjugation.getTense()).build());
      result.add(sentence);
    }
    return result;
  }
}
