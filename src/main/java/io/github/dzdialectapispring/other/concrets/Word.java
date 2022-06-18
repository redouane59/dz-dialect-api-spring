package io.github.dzdialectapispring.other.concrets;

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
public class Word {

  // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Setter
  @Getter
  private List<Translation> translations = new ArrayList<>();

  public Optional<Translation> getTranslationByLang(Lang lang) {
    return translations.stream().filter(t -> t.getLang() == lang).findAny();
  }

  public String getTranslationValue(Lang lang) {
    return getTranslationByLang(lang).orElse(new Translation(lang, "")).getValue();
  }

  public String getTranslationValueAr(Lang lang) {
    return getTranslationByLang(lang).orElse(new Translation(lang, "")).getArValue();
  }


}
