package io.github.dzdialectapispring.other.concrets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
public class Word implements Comparable {

  // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Setter
  @Getter
  private List<Translation> translations = new ArrayList<>();
  @JsonInclude(Include.NON_DEFAULT)
  @Setter
  @Getter
  private int               index;


  public Word(List<Translation> translations) {
    this.translations = translations;
  }

  public Optional<Translation> getTranslationByLang(Lang lang) {
    return translations.stream().filter(t -> t.getLang() == lang).findAny();
  }

  @com.google.cloud.firestore.annotation.Exclude
  public String getDzTranslationValue() {
    return getTranslationByLang(Lang.DZ).orElse(new Translation(Lang.DZ, "")).getValue();
  }

  @com.google.cloud.firestore.annotation.Exclude
  public String getDzTranslationValueAr() {
    return (getTranslationByLang(Lang.DZ).orElse(new Translation(Lang.DZ, "", ""))).getArValue();
  }

  @com.google.cloud.firestore.annotation.Exclude
  public String getFrTranslationValue() {
    return getTranslationByLang(Lang.FR).orElse(new Translation(Lang.FR, "")).getValue();
  }

  @Override
  public int compareTo(final Object o) {
    return (getIndex() - ((Word) o).getIndex());
  }
}
