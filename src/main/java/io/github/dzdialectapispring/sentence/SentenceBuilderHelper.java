package io.github.dzdialectapispring.sentence;

import java.util.HashMap;
import java.util.Map;

public class SentenceBuilderHelper {

  public static Map<String, String> splitSentenceInWords(String frSentence, boolean alternative) {
    Map<String, String> result = new HashMap<>();
    long                count  = frSentence.chars().filter(ch -> ch == '-').count();
    for (int i = 0; i < count; i++) {
      result.put("t" + i, "-");
    }
    String[] split = frSentence.split("[- ]");
    for (int i = 0; i < split.length; i++) {
      String s  = split[i];
      String id = "s" + i;
      if (alternative) {
        id += "a";
      }
      if (!s.contains("'")) {
        result.put(id, s);
      } else {
        result.put(id + "1", s.substring(0, s.indexOf("'") + 1));
        result.put(id + "2", s.substring(s.indexOf("'") + 1));
      }
    }
    return result;
  }

}
