package com.lcsk42.frameworks.starter.common.util.time;

import java.text.DateFormat;
import java.time.format.DateTimeFormatter;

public interface IDatePattern {

    String getPattern();

    DateFormat getDateFormat();

    DateTimeFormatter getDateTimeFormatter();
}
