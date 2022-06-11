package io.github.dzdialectapispring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

  @GetMapping("/")
  public String home() {
    return "Hello World! See https://github.com/redouane59/dz-dialect-api-spring";
  }

}
