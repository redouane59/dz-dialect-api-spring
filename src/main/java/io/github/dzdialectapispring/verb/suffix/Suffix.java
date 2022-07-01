package io.github.dzdialectapispring.verb.suffix;

import static io.github.dzdialectapispring.sentence.SentenceBuilder.RANDOM;

import io.github.dzdialectapispring.DB;
import io.github.dzdialectapispring.other.concrets.Possession;
import io.github.dzdialectapispring.other.concrets.PossessiveWord;
import io.github.dzdialectapispring.other.enumerations.Gender;
import io.github.dzdialectapispring.other.enumerations.WordType;
import io.github.dzdialectapispring.sentence.SentenceSchema;
import io.github.dzdialectapispring.verb.Verb;
import java.util.Optional;

public class Suffix extends PossessiveWord {


  public static Optional<PossessiveWord> getRandomSuffix(final Possession other, boolean isDirect, boolean objectOnly, boolean isImperative) {
    Gender     randomGender     = Gender.getRandomGender();
    boolean    randomSingular   = RANDOM.nextBoolean();
    Possession randomPossession = Possession.getRandomPosession(other, objectOnly, isImperative);

    AbstractSuffix baseSuffix;
    if (isDirect) {
      baseSuffix = DB.DIRECT_SUFFIXES;
    } else {
      baseSuffix = DB.INDIRECT_SUFFIXES;
    }

    return baseSuffix.getValues().stream()
                     .filter(s -> s.isSingular() == randomSingular)
                     .filter(s -> s.getPossession() == randomPossession)
                     .filter(s -> s.getGender() == randomGender || s.getGender() == Gender.X || randomGender == Gender.X)
                     .findFirst();
  }

  public static Optional<PossessiveWord> getSuffix(Verb asbtractVerb,
                                                   SentenceSchema schema,
                                                   Possession possession,
                                                   boolean isObjectOnly,
                                                   boolean isImperative) {
    boolean isDirect = !schema.getFrSequence().contains(WordType.NOUN);
    // check if the verb manages the direct/undirect suffix
    if ((isDirect && !asbtractVerb.isDirectComplement()) || (!isDirect && !asbtractVerb.isIndirectComplement())) {
      return Optional.empty();
    }
    return getRandomSuffix(possession, isDirect, isObjectOnly, isImperative);
  }

}
