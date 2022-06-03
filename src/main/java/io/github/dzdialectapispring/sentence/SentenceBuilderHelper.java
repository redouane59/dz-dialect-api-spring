package io.github.dzdialectapispring.sentence;

import java.util.ArrayList;
import java.util.List;

public class SentenceBuilderHelper {

  public static List<String> splitSentenceInWords(String frSentence) {
    List<String> result = new ArrayList<>();
    long         count  = frSentence.chars().filter(ch -> ch == '-').count();
    for (int i = 0; i < count; i++) {
      result.add("-");
    }
    for (String s : frSentence.split("[- ]")) {
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
