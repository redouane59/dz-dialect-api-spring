package io.github.dzdialectapispring;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")}, info =
@Info(title = "DZDialect API", version = "1.0.0", description = "DZDialect API v1.0"))
@SpringBootApplication
@Slf4j
@Controller
public class DzDialectApiSpringApplication {

  public static void main(String[] args) throws IOException {
    System.out.println("main()");
    String      credentialString = System.getenv("GOOGLE_CREDENTIALS");
    InputStream serviceAccount;
    if (credentialString != null) {
      System.out.println("loading credentials from variable environment");
      serviceAccount = new ByteArrayInputStream(credentialString.getBytes(StandardCharsets.UTF_8));
    } else {
      System.out.println("loading credentials from local file");
      File file = new File("../dz-dialect-api-443aafbaf7a9.json");
      serviceAccount = new FileInputStream(file.getAbsolutePath());
    }
    FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build();

    System.out.println("firebase options OK");
    FirebaseApp.initializeApp(options);
    DB.init();
    System.out.println("app init OK");
    SpringApplication.run(DzDialectApiSpringApplication.class, args);
    System.out.println("app run OK");
  }


  @RequestMapping("/")
  @ResponseBody
  String home() {
    return "Hello World!";
  }

}
