package io.github.dzdialectapispring.other.concrets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.dzdialectapispring.other.Config;
import io.github.dzdialectapispring.other.enumerations.Gender;
import io.github.dzdialectapispring.other.enumerations.Lang;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Translation {

  private Lang   lang;
  private String value;
  @JsonInclude(Include.NON_NULL)
  private Gender gender; // only used for nouns that have different gender in FR/DZ
  @JsonInclude(Include.NON_NULL)
  @JsonProperty("ar_value")
  private String arValue; // translation written in arabic letters

  public Translation(final Lang lang, final String value) {
    this.lang  = lang;
    this.value = cleanValue(value);
  }

  public Translation(final Lang lang, final String value, final String arValue) {
    this.lang  = lang;
    this.value = value;
    this.value = cleanValue(value);
  }

  public String cleanValue(String value) {
    if (value == null) {
      return "";
    }
    String newValue = value;
    newValue = newValue.replace("' ", "'").replace("  ", " ");
    // replacing pronouns & articles ending with a vowel when the next word also start by a vowel
    if (lang == Lang.FR) {
      for (char v : Config.VOWELS) {
        newValue = newValue.replace(" je " + v, " j'" + v);
        newValue = newValue.replace(" ce " + v, " c'" + v);
        newValue = newValue.replace(" me " + v, " m'" + v);
        newValue = newValue.replace(" te " + v, " t'" + v);
        newValue = newValue.replace(" le " + v, " l'" + v);
        newValue = newValue.replace(" la " + v, " l'" + v);
        newValue = newValue.replace(" ne " + v, " n'" + v);
        if (newValue.startsWith("le " + v)) {
          newValue = newValue.replace("le " + v, " l'" + v);
        } else if (newValue.startsWith("la " + v)) {
          newValue = newValue.replace("la " + v, " l'" + v);
        } else if (newValue.startsWith("je " + v)) {
          newValue = newValue.replace("je " + v, "j'" + v);
        } else if (newValue.startsWith("ne " + v)) {
          newValue = newValue.replace("ne " + v, "n'" + v);
        }
      }
      newValue = newValue.replace("que il", "qu'il");
      newValue = newValue.replace("que elle", "qu'elle");
      newValue = newValue.replace("que on", "qu'on");
      newValue = newValue.replace("à le", "au");
      newValue = newValue.replace("l' ", "l'");
      newValue = newValue.replace("-me", "-moi");
    } else if (lang == Lang.DZ) {
      // suffixes managed in suffix class
    }
    newValue = newValue.replace("  ", " ");
    newValue = newValue.trim();
    return newValue;
  }
}
