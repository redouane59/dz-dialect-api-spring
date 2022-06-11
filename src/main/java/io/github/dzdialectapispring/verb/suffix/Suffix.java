package io.github.dzdialectapispring.verb.suffix;

import static io.github.dzdialectapispring.sentence.SentenceBuilder.RANDOM;

import io.github.dzdialectapispring.other.Config;
import io.github.dzdialectapispring.other.concrets.Possession;
import io.github.dzdialectapispring.other.concrets.PossessiveWord;
import io.github.dzdialectapispring.other.enumerations.Gender;
import io.github.dzdialectapispring.other.enumerations.WordType;
import io.github.dzdialectapispring.sentence.SentenceSchema;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class Suffix extends PossessiveWord {


  public static Optional<PossessiveWord> getRandomSuffix(final Possession other, boolean isDirect, boolean objectOnly, boolean isImperative) {
    Gender     randomGender     = Gender.getRandomGender();
    boolean    randomSingular   = RANDOM.nextBoolean();
    Possession randomPossession = Possession.getRandomPosession(other, objectOnly, isImperative);

    AbstractSuffix baseSuffix;

    // @todo dirty
    if (isDirect) {
      try {
        baseSuffix =
            Config.OBJECT_MAPPER.readValue(new File("./src/main/resources/static/suffixes/direct_pronoun_suffixes.json"),
                                           AbstractSuffix.class);
      } catch (IOException e) {
        e.printStackTrace();
        return Optional.empty();
      }
    } else {
      try {
        baseSuffix =
            Config.OBJECT_MAPPER.readValue(new File("./src/main/resources/static/suffixes/indirect_pronoun_suffixes.json"),
                                           AbstractSuffix.class);
      } catch (IOException e) {
        e.printStackTrace();
        return Optional.empty();
      }
    }

    return baseSuffix.getValues().stream().map(o -> o)
                     .filter(s -> s.isSingular() == randomSingular)
                     .filter(s -> s.getPossession() == randomPossession)
                     .filter(s -> s.getGender() == randomGender || s.getGender() == Gender.X || randomGender == Gender.X)
                     .findFirst();
  }

  public static Optional<PossessiveWord> getSuffix(SentenceSchema schema, Possession possession, boolean isObjectOnly, boolean isImperative) {
    boolean isDirect = !schema.getFrSequence().contains(WordType.NOUN);
    return getRandomSuffix(possession, isDirect, isObjectOnly, isImperative);
  }

}
