package io.github.dzdialectapispring.adjective;

import static io.github.dzdialectapispring.other.Config.RANDOM;

import io.github.dzdialectapispring.DB;
import io.github.dzdialectapispring.noun.Noun;
import io.github.dzdialectapispring.other.NounType;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.GenderedWord;
import io.github.dzdialectapispring.other.enumerations.WordType;
import io.github.dzdialectapispring.sentence.SentenceSchema;
import io.github.dzdialectapispring.sentence.WordDTO;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdjectiveService {

  public Set<String> getAllAdjectivesIds(boolean includeTemporal, boolean includeDefinitive) {
    Set<Adjective> adjectives = DB.ADJECTIVES;
    Set<Adjective> result     = new HashSet<>();
    if (includeTemporal) {
      result.addAll(adjectives.stream().filter(Adjective::isTemporal).collect(Collectors.toSet()));
    }
    if (includeDefinitive) {
      result.addAll(adjectives.stream().filter(Adjective::isDefinitive).collect(Collectors.toSet()));
    }
    return result.stream().map(AbstractWord::getId).collect(Collectors.toSet());
  }

  public Adjective getAdjectiveById(final String adjectiveId) {
    Optional<Adjective> adjectiveOpt = DB.ADJECTIVES.stream().filter(a -> a.getId().equals(adjectiveId)).findFirst();
    return adjectiveOpt.orElseThrow(() -> new IllegalArgumentException("no adjective found with id " + adjectiveId));
  }

  public List<WordDTO> getAdjectiveValuesById(final String adjectiveId) {
    Adjective     adjective = getAdjectiveById(adjectiveId);
    List<WordDTO> result    = new ArrayList<>();
    for (GenderedWord word : adjective.getValues()) {
      result.add(new WordDTO(word));
    }
    return result;
  }

  public Optional<Adjective> getAbstractAdjective(SentenceSchema schema, AbstractWord subject, Noun nounSubject) {
    NounType type = null;
    if (subject.getWordType() == WordType.PRONOUN) {
      type = NounType.PERSON;
    } else if (nounSubject != null) {
      type = nounSubject.getType();
    }

    final NounType finalNounType = type;

    Set<Adjective> adjectives = DB.ADJECTIVES;

    if (type != null) {
      adjectives = adjectives.stream()
                             .filter(a -> a.getPossibleNouns() != null)
                             .filter(a -> a.getPossibleNouns().stream()
                                           .anyMatch(x -> x == finalNounType))
                             .collect(Collectors.toSet());
      if (adjectives.isEmpty()) {
        System.err.println("adjectives empty after noun types");
        return Optional.empty();
      }
    }

    if (schema.isDefinitiveAdjective()) {
      adjectives = adjectives.stream().filter(Adjective::isDefinitive).collect(Collectors.toSet());
    } else {
      adjectives = adjectives.stream().filter(Adjective::isTemporal).collect(Collectors.toSet());
    }
    if (adjectives.isEmpty()) {
      System.out.println("adjectives empty after is definitive");
      return Optional.empty();
    }

    Optional<Adjective> adjectiveOpt = adjectives.stream().skip(RANDOM.nextInt(adjectives.size())).findFirst();
    if (adjectiveOpt.isEmpty()) {
      System.out.println("adjective empty");
      return Optional.empty();
    }
    return adjectiveOpt;
  }

}
