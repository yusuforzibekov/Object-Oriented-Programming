package com.epam.rd.autotasks.timing;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TimeZone;

public interface SchedulingAssistant {

    LocalDateTime schedule(long meetingDurationMinutes, MeetingTimingPreferences preferences);

    static SchedulingAssistant create(Collection<Developer> team, LocalDate today) {
        return new SchedulingAssistantAssistantImpl(team, today);
    }
}

class SchedulingAssistantAssistantImpl implements SchedulingAssistant {
    private final Collection<Developer> team;
    private final LocalDate currentDate;

    SchedulingAssistantAssistantImpl(Collection<Developer> team, LocalDate currentDate) {
        this.team = team;
        this.currentDate = currentDate;
    }

    @Override
    public LocalDateTime schedule(long meetingDurationMinutes, MeetingTimingPreferences preferences) {
        final ArrayList<LocalDateTime> workingTimes = calculateWorkingTimes();
        final boolean isEarliestPreference = (preferences.inPeriod == MeetingTimingPreferences.InPeriodPreference.EARLIEST);
        LocalDateTime latestWorkingTime = Collections.max(workingTimes);
        LocalDateTime earliestWorkingTime = Collections.min(workingTimes);
        LocalDateTime suggestedMeetingTime;

        if (isEarliestPreference) {
            suggestedMeetingTime = latestWorkingTime;
            if (suggestedMeetingTime.plusMinutes(meetingDurationMinutes).isBefore(earliestWorkingTime.plusHours(8)) ||
                    suggestedMeetingTime.plusMinutes(meetingDurationMinutes)
                            .isEqual(earliestWorkingTime.plusHours(8))) {
                if (preferences.period == MeetingTimingPreferences.PeriodPreference.TOMORROW) {
                    suggestedMeetingTime = suggestedMeetingTime.plusDays(1);
                }
                return suggestedMeetingTime;
            }
        } else {
            suggestedMeetingTime = earliestWorkingTime.plusHours(8).minusMinutes(meetingDurationMinutes);
            if (suggestedMeetingTime.isAfter(latestWorkingTime) || suggestedMeetingTime.isEqual(latestWorkingTime)) {
                if (preferences.period == MeetingTimingPreferences.PeriodPreference.TOMORROW) {
                    suggestedMeetingTime = suggestedMeetingTime.plusDays(1);
                } else if (preferences.period == MeetingTimingPreferences.PeriodPreference.THIS_WEEK) {
                    suggestedMeetingTime = suggestedMeetingTime.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
                }
                return suggestedMeetingTime;
            }
        }
        return null;
    }

    private ArrayList<LocalDateTime> calculateWorkingTimes() {
        ArrayList<LocalDateTime> workingTimes = new ArrayList<>();
        for (var developer : this.team) {
            LocalDateTime workStartTime = LocalDateTime.of(this.currentDate, developer.workDayStartTime);
            ZoneId zoneId;
            switch (developer.city.toLowerCase()) {
                case "london":
                    zoneId = ZoneId.of("Europe/London");
                    break;
                case "new york":
                    zoneId = ZoneId.of("America/New_York");
                    break;
                case "los angeles":
                    zoneId = ZoneId.of("America/Los_Angeles");
                    break;
                case "paris":
                    zoneId = ZoneId.of("Europe/Paris");
                    break;
                case "samara":
                    zoneId = ZoneId.of("Europe/Samara");
                    break;
                case "prague":
                    zoneId = ZoneId.of("Europe/Prague");
                    break;
                case "tbilisi":
                    zoneId = ZoneId.of("Asia/Tbilisi");
                    break;
                default:
                    throw new IllegalArgumentException("Unknown city: " + developer.city);
            }

            LocalDateTime workStartTimeInUTC = workStartTime.atZone(zoneId)
                    .withZoneSameInstant(ZoneId.of("UTC"))
                    .toLocalDateTime();
            workingTimes.add(workStartTimeInUTC);
        }
        return workingTimes;
    }
}
