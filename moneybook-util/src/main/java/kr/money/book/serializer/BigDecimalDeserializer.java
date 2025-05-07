package kr.money.book.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.math.BigDecimal;

public class BigDecimalDeserializer extends StdDeserializer<BigDecimal> {
    public BigDecimalDeserializer() {
        this(null);
    }

    public BigDecimalDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String valueStr = p.getText();
        return new BigDecimal(valueStr).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
