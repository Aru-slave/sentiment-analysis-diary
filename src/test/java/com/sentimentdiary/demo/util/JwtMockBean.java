package com.sentimentdiary.demo.util;

import com.google.gson.*;
import com.sentimentdiary.demo.auth.jwt.JwtTokenizer;
import com.sentimentdiary.demo.auth.utils.CustomAuthorityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class JwtMockBean {
    @Autowired
    protected MockMvc mockMvc;

    protected Gson gson;
    protected static String startWithUrl;

    @MockBean
    protected JwtTokenizer jwtTokenizer;

    @MockBean
    protected CustomAuthorityUtils customAuthorityUtils;

    public JwtMockBean() {
        gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

            @Override
            public JsonElement serialize(LocalDateTime localDateTime, Type srcType, JsonSerializationContext context) {
                return new JsonPrimitive(formatter.format(localDateTime));
            }
        }).registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public JsonElement serialize(LocalDate localDate, Type srcType, JsonSerializationContext context) {
                return new JsonPrimitive(formatter.format(localDate));
            }
        }).registerTypeAdapter(LocalDate.class, new JsonDeserializer <LocalDate>() {
            @Override
            public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException {
                return LocalDate.parse(json.getAsString(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd").withLocale(Locale.KOREA));
            }
        }).setPrettyPrinting().create();
    }
}
