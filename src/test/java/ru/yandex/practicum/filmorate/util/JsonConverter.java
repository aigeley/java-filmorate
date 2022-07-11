package ru.yandex.practicum.filmorate.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class JsonConverter {
    private static Gson gson;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
                        @Override
                        public void write(final JsonWriter jsonWriter, final LocalDate localDate)
                                throws IOException {
                            jsonWriter.value(localDate.format(DATE_FORMATTER));
                        }

                        @Override
                        public LocalDate read(final JsonReader jsonReader) throws IOException {
                            return LocalDate.parse(jsonReader.nextString(), DATE_FORMATTER);
                        }
                    }).create();
        }

        return gson;
    }
}
