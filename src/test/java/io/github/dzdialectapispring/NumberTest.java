package io.github.dzdialectapispring;

import static io.github.dzdialectapispring.other.Config.RANDOM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.dzdialectapispring.number.NumberService;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.enumerations.Lang;
import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NumberTest {

  @Autowired
  private NumberService numberService;

  @BeforeAll
  public static void init() throws IOException {
    DzDialectApiSpringApplication.main(new String[]{});
  }

  @Test
  public void testDBNumbers() {
    assertTrue(DB.NUMBERS.size() > 0);
    assertTrue(DB.NUMBERS.stream().skip(RANDOM.nextInt(DB.NUMBERS.size())).findFirst().get().getValue() > 0);
  }

  @Test
  public void testGetRandomNumber() {
    AbstractWord number = numberService.getRandomNumber();
    assertNotNull(number);
    assertNotNull(number.getValues().get(0).getTranslationByLang(Lang.FR));
    assertNotNull(number.getValues().get(0).getTranslationByLang(Lang.DZ));
  }

  @Test
  public void testGetNumberById() {
    AbstractWord number = numberService.getNumberById("1");
    assertNotNull(number);
    assertEquals("1", number.getValues().get(0).getTranslationByLang(Lang.FR).get().getValue());
    assertEquals("wa7ad", number.getValues().get(0).getTranslationByLang(Lang.DZ).get().getValue());
  }

  @Test
  public void testGetRandomNumbers() {
    assertTrue(numberService.getRandomNumbers(2, 0, 2).size() == 2);
    assertTrue(numberService.getRandomNumbers(2, 1, 1).size() == 2);
    assertTrue(numberService.getRandomNumbers(1, 0, 3).size() == 1);
    assertTrue(numberService.getRandomNumbers(1, 2, 3).size() == 1);
    assertTrue(numberService.getRandomNumbers(3, 0, 3).size() == 3);
    assertTrue(numberService.getRandomNumbers(1, -5, -1).size() == 0);
  }

}
