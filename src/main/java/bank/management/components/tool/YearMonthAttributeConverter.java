package bank.management.components.tool;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.time.YearMonth;

@Component
@Converter(autoApply = true)
public class YearMonthAttributeConverter implements AttributeConverter<YearMonth, String> {

    @Override
    public String convertToDatabaseColumn(YearMonth yearMonth) {
        return yearMonth == null ? null : addZeroIfSingle(yearMonth.getMonth().getValue()) + "/" + yearMonth.getYear();
    }

    @Override
    public YearMonth convertToEntityAttribute(String s) {
        var temporalData = s.split("/");
        int month = Integer.parseInt(temporalData[0]);
        int year = Integer.parseInt(temporalData[1]);
        return YearMonth.of(year, month);
    }

    private String addZeroIfSingle(int value) {
        if (String.valueOf(value).length() == 1) {
            return "0" + value;
        }
        return String.valueOf(value);
    }

}
