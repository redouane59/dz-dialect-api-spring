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
@Getter
@Setter
public class Word implements Comparable {

  // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private List<Translation> translations = new ArrayList<>();
  @JsonInclude(Include.NON_DEFAULT)
  private int               index;


  public Word(List<Translation> translations) {
    this.translations = translations;
  }

  public Optional<Translation> getTranslationByLang(Lang lang) {
    return translations.stream().filter(t -> t.getLang() == lang).findAny();
  }

  // @todo to fix
  @com.google.cloud.firestore.annotation.Exclude
  public String getDzTranslation() {
    return getTranslationByLang(Lang.DZ).orElse(new Translation(Lang.DZ, "")).getValue();
  }

  @com.google.cloud.firestore.annotation.Exclude
  public String getDzTranslationAr() {
    return (getTranslationByLang(Lang.DZ).orElse(new Translation(Lang.DZ, "", ""))).getArValue();
  }

  @com.google.cloud.firestore.annotation.Exclude
  public String getFrTranslation() {
    return getTranslationByLang(Lang.FR).orElse(new Translation(Lang.FR, "")).getValue();
  }

  @Override
  public int compareTo(final Object o) {
    return (getIndex() - ((Word) o).getIndex());
  }
}
