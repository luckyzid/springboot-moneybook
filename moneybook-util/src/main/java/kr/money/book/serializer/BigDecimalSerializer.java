package kr.money.book.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.math.BigDecimal;

public class BigDecimalSerializer extends StdSerializer<BigDecimal> {
    public BigDecimalSerializer() {
        super(BigDecimal.class);
    }

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(value.setScale(2, BigDecimal.ROUND_HALF_UP));
    }
}
