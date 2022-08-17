package ru.yandex.practicum.filmorate.storage;

public class DbUtils {
    private DbUtils() {
    }

    public static String getPlaceHolders(int count) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < count; i++) {
            sb.append("(?, ?)");

            if (i == count - 1) {
                break; //не ставим запятую вконце
            }

            sb.append(", ");
        }

        return sb.toString();
    }
}
