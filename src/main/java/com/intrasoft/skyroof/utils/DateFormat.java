package com.intrasoft.skyroof.utils;

import java.time.format.DateTimeFormatter;

public interface DateFormat {

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

}
