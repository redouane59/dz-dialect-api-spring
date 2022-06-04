package io.github.dzdialectapispring.other.concrets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.dzdialectapispring.other.enumerations.Lang;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Word {

  // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private List<Translation> translations = new ArrayList<>();

  public Optional<Translation> getTranslationByLang(Lang lang) {
    return translations.stream().filter(t -> t.getLang() == lang).findAny();
  }

  @JsonIgnore
  public String getDzTranslation() {
    return getTranslationByLang(Lang.DZ).orElse(new Translation(Lang.DZ, "")).getValue();
  }

  @JsonIgnore
  public String getDzTranslationAr() {
    return (getTranslationByLang(Lang.DZ).orElse(new Translation(Lang.DZ, "", ""))).getArValue();
  }

  @JsonIgnore
  public String getFrTranslation() {
    return getTranslationByLang(Lang.FR).orElse(new Translation(Lang.FR, "")).getValue();
  }

}
