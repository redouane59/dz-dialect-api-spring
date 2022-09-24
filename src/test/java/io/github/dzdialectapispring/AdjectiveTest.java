package io.github.dzdialectapispring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.dzdialectapispring.adjective.Adjective;
import io.github.dzdialectapispring.other.enumerations.Lang;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AdjectiveTest {

  @BeforeAll
  public static void init() {
    DB.init();
  }

  @Test
  public void testFrenchAuxiliar() {
    Adjective faim = DB.ADJECTIVES.stream().filter(a -> a.getId().equals("faim")).findFirst().get();
    assertTrue(faim.isFrAuxiliarAvoir());
    assertEquals(DB.AUX_AVOIR, faim.getAuxiliarFromAdjective(Lang.FR));
    assertEquals(DB.AUX_ETRE, faim.getAuxiliarFromAdjective(Lang.DZ));
    Adjective grand = DB.ADJECTIVES.stream().filter(a -> a.getId().equals("grand")).findFirst().get();
    assertEquals(DB.AUX_ETRE, grand.getAuxiliarFromAdjective(Lang.FR));
    assertEquals(DB.AUX_ETRE, grand.getAuxiliarFromAdjective(Lang.DZ));
  }
}
