package io.github.dzdialectapispring.other;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Random;

public class Config {

  public static final ObjectMapper    OBJECT_MAPPER = new ObjectMapper();
  public static       Random          RANDOM        = new Random();
  public static       List<Character> VOWELS        = List.of('a', 'e', 'é', 'è', 'ê', 'i', 'o', 'ô', 'u', 'h');

}
