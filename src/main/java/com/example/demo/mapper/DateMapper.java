package com.example.demo.mapper;

import com.example.demo.exception.DateMapperException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.util.Validator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Component
public class DateMapper {
    public LocalDate toDate(String expiryDate) {
        return formatDateToCorrect(expiryDate);
    }

    private LocalDate formatDateToCorrect(String expiryDate) {
        if (! Validator.isValidDateString(expiryDate)) throw new DateMapperException(ErrorCode.INVALID_DATE_STRING, expiryDate);
        String delimiter = findDelimiter(expiryDate);
        String[] dateElements = StringUtils.split(expiryDate, delimiter);
        DateTimeFormatter format = createFormat(dateElements, delimiter);
        return LocalDate.parse(expiryDate, format);
    }


    private static String findDelimiter(String str) {
        Matcher matcher = Pattern.compile("\\D").matcher(str);
        if (!matcher.find()) {
            throw new DateMapperException(ErrorCode.UNSUPPORTED_DATE_FORMAT);
        }
        String delimiter = matcher.group();
        if (!str.replaceFirst(Pattern.quote(delimiter), "").matches("[\\d" + Pattern.quote(delimiter) + "]+")) {
            throw new DateMapperException(ErrorCode.UNSUPPORTED_DATE_FORMAT);
        }
        return delimiter;
    }


    private DateTimeFormatter createFormat(String[] dateElements, String delimiter) {
        if (dateElements[0].length() == 4 && Integer.parseInt(dateElements[1]) <= 12) {
            return DateTimeFormatter.ofPattern("yyyy" + delimiter + "MM" + delimiter + "dd");
        }
        else if (dateElements[0].length() == 4 && Integer.parseInt(dateElements[1]) > 12) {
            return DateTimeFormatter.ofPattern("yyyy" + delimiter + "dd" + delimiter + "MM");
        }
        else if (Integer.parseInt(dateElements[1]) <= 12) {
            return DateTimeFormatter.ofPattern("dd" + delimiter + "MM" + delimiter + "yyyy");
        }
        else {
            return DateTimeFormatter.ofPattern("MM" + delimiter + "dd" + delimiter + "yyyy");
        }
    }
}