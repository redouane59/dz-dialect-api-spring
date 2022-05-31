package io.github.dzdialectapispring.other.concrets;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.dzdialectapispring.other.enumerations.Lang;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Word {

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private Set<Translation> translations = new HashSet<>();

  public Optional<Translation> getTranslationByLang(Lang lang) {
    return translations.stream().filter(t -> t.getLang() == lang).findAny();
  }

  @JsonProperty("dz_value")
  public String getDzTranslation() {
    return getTranslationByLang(Lang.DZ).orElse(new Translation(Lang.DZ, "")).getValue();
  }

  @JsonProperty("dz_value_ar")
  public String getDzTranslationAr() {
    return (getTranslationByLang(Lang.DZ).orElse(new Translation(Lang.DZ, "", ""))).getArValue();
  }

  @JsonProperty("fr_value")
  public String getFrTranslation() {
    return getTranslationByLang(Lang.FR).orElse(new Translation(Lang.FR, "")).getValue();
  }


}
