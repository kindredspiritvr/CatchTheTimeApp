package com.chuck.catchthetime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public final class CatchTimes {
    private CatchTimes() {}

    // Deliberately curated: obvious repeats, runs and mirror/palindrome moments.
    // Edit this list if you decide a time is too boring or gloriously catch-worthy.
    public static final List<LocalTime> TIMES = Arrays.asList(
            LocalTime.of(0, 0),
            LocalTime.of(1, 11), LocalTime.of(1, 23),
            LocalTime.of(2, 22), LocalTime.of(2, 34),
            LocalTime.of(3, 21), LocalTime.of(3, 33), LocalTime.of(3, 45),
            LocalTime.of(4, 32), LocalTime.of(4, 44), LocalTime.of(4, 56),
            LocalTime.of(5, 43), LocalTime.of(5, 55),
            LocalTime.of(6, 54),
            LocalTime.of(10, 1), LocalTime.of(10, 10),
            LocalTime.of(11, 11),
            LocalTime.of(12, 12), LocalTime.of(12, 21), LocalTime.of(12, 34),
            LocalTime.of(13, 13), LocalTime.of(13, 31),
            LocalTime.of(14, 14), LocalTime.of(14, 41),
            LocalTime.of(15, 15), LocalTime.of(15, 51),
            LocalTime.of(16, 16), LocalTime.of(17, 17), LocalTime.of(18, 18), LocalTime.of(19, 19),
            LocalTime.of(20, 2), LocalTime.of(20, 20),
            LocalTime.of(21, 12), LocalTime.of(21, 21),
            LocalTime.of(22, 22),
            LocalTime.of(23, 23), LocalTime.of(23, 32), LocalTime.of(23, 45)
    );

    public static LocalDateTime nextAfter(LocalDateTime now) {
        LocalDate today = now.toLocalDate();
        for (LocalTime time : TIMES) {
            LocalDateTime candidate = LocalDateTime.of(today, time);
            if (candidate.isAfter(now)) {
                return candidate;
            }
        }
        return LocalDateTime.of(today.plusDays(1), TIMES.get(0));
    }
}
