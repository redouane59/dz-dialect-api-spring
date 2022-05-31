package io.github.dzdialectapispring.other.enumerations;

import static io.github.dzdialectapispring.other.Config.RANDOM;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Gender {

  M, // masculin
  F, // feminin
  X; // genderless

  public static Gender getRandomGender() {
    return Arrays.stream(values()).skip(RANDOM.nextInt(values().length)).findFirst().get();
  }
}