package io.github.dzdialectapispring.sentence;

import io.github.dzdialectapispring.other.concrets.Translation;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.util.Pair;

public class SentenceBuilderHelper {

  public static List<Pair<String, String>> splitSentenceInWords(Translation translation, boolean alternative) {
    List<Pair<String, String>> result   = new ArrayList<>();
    String                     sentence = translation.getValue();
    long                       count    = sentence.chars().filter(ch -> ch == '-').count();
    for (int i = 0; i < count; i++) {
      result.add(Pair.of("t" + i, "-"));
    }
    String[] split = sentence.split("[- ]");
    for (int i = 0; i < split.length; i++) {
      String s  = split[i];
      String id = "s" + i;
      if (alternative) {
        id += "a";
      }
      if (!s.contains("'")) {
        result.add(Pair.of(id, s));
      } else {
        result.add(Pair.of(id + "1", s.substring(0, s.indexOf("'") + 1)));
        result.add(Pair.of(id + "2", s.substring(s.indexOf("'") + 1)));
      }
    }
    return result;
  }

}
