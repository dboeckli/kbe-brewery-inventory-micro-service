package ch.dboeckli.springframeworkguru.kbe.inventory.services.web.mappers;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DateMapperTest {

    private final DateMapper mapper = new DateMapper();

    @Test
    void asOffsetDateTimeReturnsNullWhenTimestampIsNull() {
        assertNull(mapper.asOffsetDateTime(null));
    }

    @Test
    void asOffsetDateTimeMapsTimestampToUtcOffsetDateTime() {
        LocalDateTime localDateTime = LocalDateTime.of(2024, 5, 10, 12, 34, 56, 789);
        Timestamp timestamp = Timestamp.valueOf(localDateTime);

        OffsetDateTime result = mapper.asOffsetDateTime(timestamp);

        assertEquals(OffsetDateTime.of(localDateTime, ZoneOffset.UTC), result);
    }

    @Test
    void asTimestampReturnsNullWhenOffsetDateTimeIsNull() {
        assertNull(mapper.asTimestamp(null));
    }

    @Test
    void asTimestampMapsOffsetDateTimeToUtcTimestamp() {
        OffsetDateTime offsetDateTime = OffsetDateTime.of(2024, 5, 10, 14, 34, 56, 123000000, ZoneOffset.ofHours(2));

        Timestamp result = mapper.asTimestamp(offsetDateTime);

        LocalDateTime expectedUtcLocalDateTime = LocalDateTime.of(2024, 5, 10, 12, 34, 56, 123000000);
        assertEquals(Timestamp.valueOf(expectedUtcLocalDateTime), result);
    }
}
