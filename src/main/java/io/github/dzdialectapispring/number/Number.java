package io.github.dzdialectapispring.number;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.dzdialectapispring.adjective.Adjective;
import io.github.dzdialectapispring.helper.FileHelper;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.concrets.Word;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.other.enumerations.WordType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Number extends AbstractWord {

  private final List<Word> values = new ArrayList<>();

  public Number() {
    super();
    this.setWordType(WordType.NUMBER);
  }

  public static Set<Number> deserializeFromCSV(final String fileName, final boolean removeHeader) {
    List<List<String>> entries        = FileHelper.getCsv(Adjective.class.getClassLoader().getResource(fileName).getPath(), ",", removeHeader);
    int                numberIdIndex  = 0;
    int                frValueIndex   = 0;
    int                dzValueIndex   = 1;
    int                dzValueArIndex = 2;
    Set<Number>        numbers        = new HashSet<>();

    for (List<String> values : entries) {
      Number           abstractNumber = new Number();
      Optional<Number> numberOpt      = numbers.stream().filter(o -> o.getId().equals(values.get(numberIdIndex))).findFirst();
      if (numberOpt.isEmpty()) { // new adjective
        abstractNumber.setId(values.get(numberIdIndex));
        numbers.add(abstractNumber);
      } else { // existing adjective
        abstractNumber = numberOpt.get();
      }

      try {

        String frValue   = values.get(frValueIndex);
        String dzValue   = values.get(dzValueIndex);
        String dzValueAr = null;
        if (values.size() > dzValueArIndex) {
          dzValueAr = values.get(dzValueArIndex);
        }

        Word numberWord = new Word();
        numberWord.setTranslations(List.of(new Translation(Lang.FR, frValue),
                                           new Translation(Lang.DZ, dzValue, dzValueAr)));
        abstractNumber.getValues().add(numberWord);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return numbers;
  }

  @JsonIgnore
  public int getValue() {
    int result = -1;
    try {
      result = Integer.parseInt(getId());
    } catch (Exception e) {
      System.out.println("impossible to parse number id " + e.getMessage());
    }
    return result;
  }

}
