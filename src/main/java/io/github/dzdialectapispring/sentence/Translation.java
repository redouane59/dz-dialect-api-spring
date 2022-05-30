package io.github.dzdialectapispring.sentence;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Translation {

  private Lang   lang;
  private String value;


}
