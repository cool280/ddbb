package com.ddbb.controller.response.point;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class SignInStatusResponse implements Serializable {

    @ApiModelProperty(value = "当天是否签到")
    private boolean todaySignedIn;

    @ApiModelProperty(value = "当月签到记录")
    private Map<LocalDate, Boolean> monthRecords;

    public static SignInStatusResponse init() {

        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);

        Map<LocalDate, Boolean> monthRecords = Stream.iterate(firstDayOfMonth, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(firstDayOfMonth, now) + 1)
                .collect(Collectors.toMap(Function.identity(), d -> false));

        SignInStatusResponse response = new SignInStatusResponse();
        response.setTodaySignedIn(false);
        response.setMonthRecords(monthRecords);
        return response;
    }
}
