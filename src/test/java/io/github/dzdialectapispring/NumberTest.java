package io.github.dzdialectapispring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

}
