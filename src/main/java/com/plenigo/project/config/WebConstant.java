package com.plenigo.project.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WebConstant {

    public static final String EPIC_FETCH_IMAGES_BY_DATE_URL = "https://api.nasa.gov/EPIC/api/natural/date/";
    public static final String EPIC_BASE_URL = "https://epic.gsfc.nasa.gov/archive/natural/";
    public static final String EPIC_FETCH_ALL = "https://api.nasa.gov/EPIC/api/natural/all";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FORMAT_N2 = "yyyy/MM/dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String API_KEY = "p960B4skMQHGdPnetw2KYFVzzoomz4GV5oZMZjUM";
}
