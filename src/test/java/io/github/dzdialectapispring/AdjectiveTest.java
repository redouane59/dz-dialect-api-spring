package io.github.dzdialectapispring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.dzdialectapispring.adjective.Adjective;
import io.github.dzdialectapispring.other.concrets.GenderedWord;
import io.github.dzdialectapispring.other.concrets.Possession;
import io.github.dzdialectapispring.other.enumerations.Gender;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.other.enumerations.Tense;
import io.github.dzdialectapispring.verb.conjugation.Conjugation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AdjectiveTest {

  @BeforeAll
  public static void init() {
    DB.init();
  }

  @Test
  public void testFrenchAuxiliarAvoir() {
    Adjective faim = DB.ADJECTIVES.stream().filter(a -> a.getId().equals("faim")).findFirst().get();
    assertTrue(faim.isFrAuxiliarAvoir());
    assertEquals(DB.AUX_AVOIR, faim.getAuxiliarFromAdjective(Lang.FR));
    assertEquals(DB.AUX_ETRE, faim.getAuxiliarFromAdjective(Lang.DZ));
  }

  @Test
  public void testFrenchAuxiliarEtre() {
    Adjective grand = DB.ADJECTIVES.stream().filter(a -> a.getId().equals("grand")).findFirst().get();
    assertEquals(DB.AUX_ETRE, grand.getAuxiliarFromAdjective(Lang.FR));
    assertEquals(DB.AUX_ETRE, grand.getAuxiliarFromAdjective(Lang.DZ));
  }

  @Test
  public void testGetConjugationAuxAvoir() {
    Adjective    faim   = DB.ADJECTIVES.stream().filter(a -> a.getId().equals("faim")).findFirst().get();
    GenderedWord adj    = faim.getValues().stream().filter(a -> a.isSingular() && a.getGender() == Gender.M).findFirst().get();
    Conjugation  result = faim.getAuxiliarConjugationFromAdjective(adj, Lang.FR, Possession.I, Tense.PRESENT);
    assertEquals(result.getTranslationValue(Lang.FR), "ai");
  }

  @Test
  public void testGetConjugationAuxEtre() {
    Adjective    grand  = DB.ADJECTIVES.stream().filter(a -> a.getId().equals("grand")).findFirst().get();
    GenderedWord adj    = grand.getValues().stream().filter(a -> a.isSingular() && a.getGender() == Gender.M).findFirst().get();
    Conjugation  result = grand.getAuxiliarConjugationFromAdjective(adj, Lang.FR, Possession.I, Tense.PRESENT);
    assertEquals(result.getTranslationValue(Lang.FR), "suis");
  }
}
