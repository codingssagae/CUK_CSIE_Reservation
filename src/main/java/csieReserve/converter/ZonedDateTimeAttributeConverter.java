package csieReserve.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Converter(autoApply = true)
public class ZonedDateTimeAttributeConverter implements AttributeConverter<ZonedDateTime, String> {

    private static final ZoneId TARGET_ZONE = ZoneId.of("Asia/Seoul");

    @Override
    public String convertToDatabaseColumn(ZonedDateTime attribute) {
        return attribute == null ? null : attribute.withZoneSameInstant(TARGET_ZONE).toString();
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ZonedDateTime.parse(dbData);
    }
}
