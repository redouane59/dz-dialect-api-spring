package io.github.dzdialectapispring.sentence;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.enumerations.Lang;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class SentenceSerializer extends StdSerializer<Sentence> {

  public SentenceSerializer() {
    this(null);
  }

  public SentenceSerializer(Class<Sentence> t) {
    super(t);
  }

  @Override
  public void serialize(
      Sentence sentence, JsonGenerator jgen, SerializerProvider provider)
  throws IOException {
    jgen.writeStartObject();
 /*   jgen.writeFieldName("values");
    jgen.writeStartObject()*/
    for (Translation t : sentence.getTranslations()) {
      jgen.writeObjectField(t.getLang().getId(), t.getValue());
      if (t.getArValue() != null) {
        jgen.writeObjectField(t.getLang().getId() + "_ar", t.getArValue());
      }
    }
    //  jgen.writeEndObject();
    //   if (!sentence.getAdditionalInformations().isEmpty()) {
    jgen.writeObjectField("additional_information", sentence.getContent());
    //   }
    if (!sentence.getRandomWords().isEmpty()) {
      jgen.writeFieldName("word_propositions");
      jgen.writeStartArray();
      for (Entry<Lang, List<String>> s : sentence.getRandomWords().entrySet()) {
        jgen.writeStartObject();
        jgen.writeObjectField(s.getKey().getId(), s.getValue());
        jgen.writeEndObject();
      }
      jgen.writeEndArray();
    }
    jgen.writeEndObject();
  }

}
