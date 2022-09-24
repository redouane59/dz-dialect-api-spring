package io.github.dzdialectapispring;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.dzdialectapispring.adjective.Adjective;
import io.github.dzdialectapispring.gsheets.SheetsHelper;
import io.github.dzdialectapispring.helper.WordFromCSVSerializer;
import io.github.dzdialectapispring.number.Number;
import io.github.dzdialectapispring.other.Config;
import io.github.dzdialectapispring.other.concrets.Word;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DataIntegrationTest {

  @BeforeAll
  public static void init() throws IOException {
    DzDialectApiSpringApplication.main(new String[]{});
  }

/*  @Test
  public void parseAdjectiveTest() {

    String         fileName   = "adjectives.csv";
    Set<Adjective> adjectives = Adjective.deserializeFromCSV(fileName, true);

    adjectives.forEach(o -> {
      ObjectMapper mapper = new ObjectMapper();
      SimpleModule module = new SimpleModule();
      module.addSerializer(Word.class, new WordFromCSVSerializer());
      mapper.registerModule(module);
      try {
        System.out.println(Config.OBJECT_MAPPER.writeValueAsString(o));
        mapper.writeValue(Paths.get("./src/test/resources/imported_adjectives/" + o.getId() + ".json").toFile(), o);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }*/

  @Test
  public void parseNumbersTest() {

    String      fileName = "numbers.csv";
    Set<Number> numbers  = Number.deserializeFromCSV(fileName, true);

    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addSerializer(Word.class, new WordFromCSVSerializer());
    mapper.registerModule(module);
    try {
      System.out.println(Config.OBJECT_MAPPER.writeValueAsString(numbers));
      mapper.writeValue(Paths.get("./src/test/resources/numbers.json").toFile(), numbers);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void importAdjectivesFromSheets() throws GeneralSecurityException, IOException {
    List<List<String>> result = SheetsHelper.readLine("adjectives!A2:F");
    assertNotNull(result);
    Set<Adjective> adjectives = Adjective.deserializeFromList(result, true);

    adjectives.forEach(o -> {
      ObjectMapper mapper = new ObjectMapper();
      SimpleModule module = new SimpleModule();
      module.addSerializer(Word.class, new WordFromCSVSerializer());
      mapper.registerModule(module);
      try {
        System.out.println(Config.OBJECT_MAPPER.writeValueAsString(o));
        mapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get("./src/test/resources/imported_adjectives/" + o.getId() + ".json").toFile(), o);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  @Test
  public void testAdjectiveConfig() {
    for (Adjective adjective : DB.ADJECTIVES) {
      if (adjective.getPossibleNouns().isEmpty()) {
        System.err.println("no configuration found for adjective " + adjective.getId());
      }
    }
  }

}
