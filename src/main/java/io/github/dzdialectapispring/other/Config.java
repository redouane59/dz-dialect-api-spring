package io.github.dzdialectapispring.other;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Random;

public class Config {

  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  public static       Random       RANDOM        = new Random();

}
