package io.github.dzdialectapispring.other.concrets;

import static io.github.dzdialectapispring.other.Config.RANDOM;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Possession {

  I, // related to I, me, mine, our, ours, etc.
  YOU, // related to you, your, yours, etc.
  OTHER; // related to he, she, his, her, their, etc.

  public static Possession getRandomPosession() {
    return Arrays.stream(values()).skip(RANDOM.nextInt(values().length)).findFirst().get();
  }

  public static Possession getRandomPosession(Possession otherPossession, boolean objectOnly) {
    Set<Possession> matchingPossession = Set.of(values());
    if (!objectOnly) {
      matchingPossession =
          matchingPossession.stream().filter(o -> o != otherPossession || otherPossession == Possession.OTHER).collect(Collectors.toSet());
    } else {
      matchingPossession = matchingPossession.stream().filter(o -> o == Possession.OTHER).collect(Collectors.toSet());
    }
    return matchingPossession.stream().skip(RANDOM.nextInt(matchingPossession.size())).findFirst().get();
  }

}