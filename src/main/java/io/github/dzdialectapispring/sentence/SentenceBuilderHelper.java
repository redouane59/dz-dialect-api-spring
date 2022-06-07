package io.github.dzdialectapispring.sentence;

import io.github.dzdialectapispring.other.concrets.Translation;
import java.util.ArrayList;
import java.util.List;

public class SentenceBuilderHelper {

  public static List<String> splitSentenceInWords(Translation translation, boolean alternative) {
    List<String> result   = new ArrayList<>();
    String       sentence = translation.getValue();
    long         count    = sentence.chars().filter(ch -> ch == '-').count();
    for (int i = 0; i < count; i++) {
      result.add("-");
    }
    String[] split = sentence.split("[- ]");
    for (int i = 0; i < split.length; i++) {
      String s = split[i];
      if (!s.contains("'")) {
        result.add(s);
      } else {
        result.add(s.substring(0, s.indexOf("'") + 1));
        result.add(s.substring(s.indexOf("'") + 1));
      }
    }
    return result;
  }

}
