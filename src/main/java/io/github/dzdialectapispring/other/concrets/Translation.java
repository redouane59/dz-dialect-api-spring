package io.github.dzdialectapispring.other.concrets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    this.value = value;
  }

  public Translation(final Lang lang, final String value, final String arValue) {
    this.lang    = lang;
    this.value   = value;
    this.arValue = arValue;
  }
}
