package io.github.dzdialectapispring.sentence;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.enumerations.Lang;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import org.springframework.data.util.Pair;

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

    jgen.writeFieldName("values");
    jgen.writeStartObject();
    for (Translation t : sentence.getTranslations()) {
      jgen.writeObjectField(t.getLang().getId(), t.getValue());
      if (t.getArValue() != null) {
        jgen.writeObjectField(t.getLang().getId() + "_ar", t.getArValue());
      }
    }
    jgen.writeEndObject();

    // if (Config.SERIALIZE_ADDITIONAL_INFO) {
    if (!sentence.getAdditionalInformations().isEmpty()) {
      jgen.writeObjectField("additional_information", sentence.getAdditionalInformations());
    }
    //if (Config.SERIALIZE_WORD_PROPOSITIONS) {
    if (!sentence.getContent().getRandomWords().isEmpty()) {

      jgen.writeFieldName("word_propositions");
      jgen.writeStartArray();

      for (Entry<Lang, List<Pair<String, String>>> s : sentence.getContent().getRandomWords().entrySet()) {
        jgen.writeStartObject();
        jgen.writeFieldName(s.getKey().getId());

        jgen.writeStartArray();
        for (Pair<String, String> pair : s.getValue()) {
          jgen.writeStartObject();
          jgen.writeStringField("key", pair.getFirst());
          jgen.writeStringField("value", pair.getSecond());
          jgen.writeEndObject();
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
      }
      jgen.writeEndArray();
    }
    //  }
    jgen.writeEndObject();
  }

}
