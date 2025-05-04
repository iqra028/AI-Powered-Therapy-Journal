package org.example.seproject1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/stats")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminStatsController {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private TherapistRepository therapistRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private JournalEntryRepository journalEntryRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user-growth")
    public ResponseEntity<?> getUserGrowthStats(@RequestParam String period) {
        try {
            // Get all users from UserRepository
            List<User> allUsers = userRepository.findAll();
            List<Map<String, Object>> userGrowthData = new ArrayList<>();

            if ("month".equals(period)) {
                // Get current month data split by weeks
                for (int i = 1; i <= 4; i++) {
                    Map<String, Object> weekData = new HashMap<>();
                    weekData.put("label", "Week " + i);

                    // Count users registered by this week of the month
                    int userCount = countCreatedByWeekOfMonth(allUsers, i);
                    weekData.put("count", userCount);

                    // For active users, we might want to count users who have been active recently
                    // For now, we'll just use the same count as registered users
                    weekData.put("activeUsers", userCount);

                    userGrowthData.add(weekData);
                }
            } else if ("year".equals(period)) {
                // Use actual month names
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

                for (int i = 0; i < 12; i++) {
                    Map<String, Object> monthData = new HashMap<>();
                    monthData.put("label", months[i]);

                    // Calculate users registered by this month
                    int userCount = countCreatedByMonth(allUsers, i+1);
                    monthData.put("count", userCount);

                    // For active users, we might want to count users who have been active recently
                    monthData.put("activeUsers", userCount);

                    userGrowthData.add(monthData);
                }
            } else { // week
                String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

                for (int i = 0; i < 7; i++) {
                    Map<String, Object> dayData = new HashMap<>();
                    dayData.put("label", days[i]);

                    // Calculate users registered by this day of week
                    int userCount = countCreatedByDayOfWeek(allUsers, i+2); // Calendar.MONDAY is 2
                    dayData.put("count", userCount);

                    // For active users, we might want to count users who have been active recently
                    dayData.put("activeUsers", userCount);

                    userGrowthData.add(dayData);
                }
            }

            return ResponseEntity.ok(userGrowthData);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error retrieving user growth stats: " + e.getMessage()));
        }
    }

    @GetMapping("/journal-activity")
    public ResponseEntity<?> getJournalActivityStats(@RequestParam String period) {
        try {
            List<JournalEntry> allJournals = journalEntryRepository.findAll();
            List<Map<String, Object>> journalData = new ArrayList<>();

            // Get current date for reference
            Calendar calendar = Calendar.getInstance();
            Date now = calendar.getTime();

            if ("month".equals(period)) {
                // Group by weeks of current month
                for (int week = 1; week <= 4; week++) {
                    Map<String, Object> weekData = new HashMap<>();
                    weekData.put("label", "Week " + week);

                    // Count journals created in this week
                    int totalCount = countJournalsByWeek(allJournals, week);
                    weekData.put("count", totalCount);

                    // Count approved journals
                    int approvedCount = countApprovedJournalsByWeek(allJournals, week);
                    weekData.put("approvedCount", approvedCount);

                    journalData.add(weekData);
                }
            } else if ("year".equals(period)) {
                // Group by months
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

                for (int i = 0; i < 12; i++) {
                    Map<String, Object> monthData = new HashMap<>();
                    monthData.put("label", months[i]);

                    // Count journals created in this month
                    int totalCount = countJournalsByMonth(allJournals, i+1);
                    monthData.put("count", totalCount);

                    // Count approved journals
                    int approvedCount = countApprovedJournalsByMonth(allJournals, i+1);
                    monthData.put("approvedCount", approvedCount);

                    journalData.add(monthData);
                }
            } else { // week
                // Group by days of week
                String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

                for (int i = 0; i < 7; i++) {
                    Map<String, Object> dayData = new HashMap<>();
                    dayData.put("label", days[i]);

                    // Count journals created on this day of week
                    int totalCount = countJournalsByDayOfWeek(allJournals, i+2); // Calendar.MONDAY is 2
                    dayData.put("count", totalCount);

                    // Count approved journals
                    int approvedCount = countApprovedJournalsByDayOfWeek(allJournals, i+2); // Calendar.MONDAY is 2
                    dayData.put("approvedCount", approvedCount);

                    journalData.add(dayData);
                }
            }

            return ResponseEntity.ok(journalData);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error retrieving journal activity stats: " + e.getMessage()));
        }
    }

    @GetMapping("/appointment-distribution")
    public ResponseEntity<?> getAppointmentDistribution(@RequestParam String period) {
        try {
            List<Appointment> appointments = appointmentRepository.findAll();
            List<Map<String, Object>> appointmentData = new ArrayList<>();

            if ("month".equals(period)) {
                // Group appointments by week of month
                for (int i = 1; i <= 4; i++) {
                    Map<String, Object> weekData = new HashMap<>();
                    weekData.put("label", "Week " + i);

                    // Count appointments in this week
                    int totalCount = countAppointmentsByWeekOfMonth(appointments, i);
                    weekData.put("count", totalCount);

                    // Count completed appointments in this week
                    int completedCount = countCompletedAppointmentsByWeekOfMonth(appointments, i);
                    weekData.put("completedCount", completedCount);

                    appointmentData.add(weekData);
                }
            } else if ("year".equals(period)) {
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

                for (int i = 0; i < 12; i++) {
                    Map<String, Object> monthData = new HashMap<>();
                    monthData.put("label", months[i]);

                    // Count appointments in this month
                    int monthNumber = i + 1;
                    int totalCount = countAppointmentsByMonth(appointments, monthNumber);
                    monthData.put("count", totalCount);

                    // Count completed appointments in this month
                    int completedCount = countCompletedAppointmentsByMonth(appointments, monthNumber);
                    monthData.put("completedCount", completedCount);

                    appointmentData.add(monthData);
                }
            } else { // week
                String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

                for (int i = 0; i < 7; i++) {
                    Map<String, Object> dayData = new HashMap<>();
                    dayData.put("label", days[i]);

                    // Count appointments on this day of week
                    int dayOfWeek = i + 2; // Calendar.MONDAY is 2
                    int totalCount = countAppointmentsByDayOfWeek(appointments, dayOfWeek);
                    dayData.put("count", totalCount);

                    // Count completed appointments on this day of week
                    int completedCount = countCompletedAppointmentsByDayOfWeek(appointments, dayOfWeek);
                    dayData.put("completedCount", completedCount);

                    appointmentData.add(dayData);
                }
            }

            return ResponseEntity.ok(appointmentData);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error retrieving appointment distribution: " + e.getMessage()));
        }
    }

    @GetMapping("/therapist-specialties")
    public ResponseEntity<?> getTherapistSpecialties() {
        try {
            List<Profile> profiles = profileRepository.findByApproved(true);

            // Group by specialty
            Map<String, Long> specialtyCounts = profiles.stream()
                    .filter(p -> p.getSpecialty() != null && !p.getSpecialty().isEmpty())
                    .collect(Collectors.groupingBy(
                            Profile::getSpecialty,
                            Collectors.counting()
                    ));

            // Also look at the specialties array
            Map<String, Long> arraySpecialtyCounts = new HashMap<>();
            for (Profile profile : profiles) {
                String[] specialties = profile.getSpecialties();
                if (specialties != null) {
                    for (String specialty : specialties) {
                        if (specialty != null && !specialty.isEmpty()) {
                            arraySpecialtyCounts.put(specialty,
                                    arraySpecialtyCounts.getOrDefault(specialty, 0L) + 1);
                        }
                    }
                }
            }

            // Merge the two maps, preferring the array counts
            Map<String, Long> mergedSpecialtyCounts = new HashMap<>(specialtyCounts);
            arraySpecialtyCounts.forEach((specialty, count) ->
                    mergedSpecialtyCounts.merge(specialty, count, Math::max));

            // Convert to the format expected by the frontend
            List<Map<String, Object>> result = new ArrayList<>();
            for (Map.Entry<String, Long> entry : mergedSpecialtyCounts.entrySet()) {
                Map<String, Object> specialtyData = new HashMap<>();
                specialtyData.put("name", entry.getKey());
                specialtyData.put("count", entry.getValue());
                result.add(specialtyData);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error retrieving therapist specialties: " + e.getMessage()));
        }
    }

    // Helper methods for calculating statistics

    // Generic method to check creation date of entities with getCreatedAt method
    private <T> int countCreatedByWeekOfMonth(List<T> entities, int weekNumber) {
        return (int) entities.stream()
                .filter(entity -> {
                    try {
                        LocalDateTime createdAt = (LocalDateTime) entity.getClass().getMethod("getCreatedAt").invoke(entity);
                        if (createdAt != null) {
                            Calendar cal = Calendar.getInstance();
                            cal.set(createdAt.getYear(), createdAt.getMonthValue() - 1, createdAt.getDayOfMonth());
                            int entityWeek = cal.get(Calendar.WEEK_OF_MONTH);
                            return entityWeek == weekNumber;
                        }
                        return false;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();
    }

    private <T> int countCreatedByMonth(List<T> entities, int monthNumber) {
        return (int) entities.stream()
                .filter(entity -> {
                    try {
                        LocalDateTime createdAt = (LocalDateTime) entity.getClass().getMethod("getCreatedAt").invoke(entity);
                        if (createdAt != null) {
                            return createdAt.getMonthValue() == monthNumber;
                        }
                        return false;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();
    }

    private <T> int countCreatedByDayOfWeek(List<T> entities, int dayOfWeek) {
        return (int) entities.stream()
                .filter(entity -> {
                    try {
                        LocalDateTime createdAt = (LocalDateTime) entity.getClass().getMethod("getCreatedAt").invoke(entity);
                        if (createdAt != null) {
                            Calendar cal = Calendar.getInstance();
                            cal.set(createdAt.getYear(), createdAt.getMonthValue() - 1, createdAt.getDayOfMonth());
                            int entityDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                            return entityDayOfWeek == dayOfWeek;
                        }
                        return false;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();
    }

    // Journal helper methods
    private int countJournalsByWeek(List<JournalEntry> journals, int weekNumber) {
        Calendar cal = Calendar.getInstance();
        return (int) journals.stream()
                .filter(j -> {
                    cal.setTime(j.getCreatedAt());
                    int journalWeek = cal.get(Calendar.WEEK_OF_MONTH);
                    return journalWeek == weekNumber;
                })
                .count();
    }

    private int countApprovedJournalsByWeek(List<JournalEntry> journals, int weekNumber) {
        Calendar cal = Calendar.getInstance();
        return (int) journals.stream()
                .filter(j -> {
                    cal.setTime(j.getCreatedAt());
                    int journalWeek = cal.get(Calendar.WEEK_OF_MONTH);
                    return journalWeek == weekNumber && "published".equals(j.getStatus());
                })
                .count();
    }

    private int countJournalsByMonth(List<JournalEntry> journals, int monthNumber) {
        Calendar cal = Calendar.getInstance();
        return (int) journals.stream()
                .filter(j -> {
                    cal.setTime(j.getCreatedAt());
                    return cal.get(Calendar.MONTH) + 1 == monthNumber;
                })
                .count();
    }

    private int countApprovedJournalsByMonth(List<JournalEntry> journals, int monthNumber) {
        Calendar cal = Calendar.getInstance();
        return (int) journals.stream()
                .filter(j -> {
                    cal.setTime(j.getCreatedAt());
                    return cal.get(Calendar.MONTH) + 1 == monthNumber && "published".equals(j.getStatus());
                })
                .count();
    }

    private int countJournalsByDayOfWeek(List<JournalEntry> journals, int dayOfWeek) {
        Calendar cal = Calendar.getInstance();
        return (int) journals.stream()
                .filter(j -> {
                    cal.setTime(j.getCreatedAt());
                    return cal.get(Calendar.DAY_OF_WEEK) == dayOfWeek;
                })
                .count();
    }

    private int countApprovedJournalsByDayOfWeek(List<JournalEntry> journals, int dayOfWeek) {
        Calendar cal = Calendar.getInstance();
        return (int) journals.stream()
                .filter(j -> {
                    cal.setTime(j.getCreatedAt());
                    return cal.get(Calendar.DAY_OF_WEEK) == dayOfWeek && "published".equals(j.getStatus());
                })
                .count();
    }

    private int countAppointmentsByWeekOfMonth(List<Appointment> appointments, int weekNumber) {
        Calendar cal = Calendar.getInstance();
        return (int) appointments.stream()
                .filter(a -> {
                    try {
                        if (a.getDate() == null) return false;

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = sdf.parse(a.getDate());
                        cal.setTime(date);
                        int appointmentWeek = cal.get(Calendar.WEEK_OF_MONTH);
                        return appointmentWeek == weekNumber;
                    } catch (ParseException e) {
                        return false;
                    }
                })
                .count();
    }

    private int countCompletedAppointmentsByWeekOfMonth(List<Appointment> appointments, int weekNumber) {
        Calendar cal = Calendar.getInstance();
        return (int) appointments.stream()
                .filter(a -> {
                    try {
                        if (a.getDate() == null || a.getStatus() == null) return false;

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = sdf.parse(a.getDate());
                        cal.setTime(date);
                        int appointmentWeek = cal.get(Calendar.WEEK_OF_MONTH);

                        // Check for various completed status possibilities
                        boolean isCompleted = a.getStatus() != null && (
                                a.getStatus().equalsIgnoreCase("COMPLETED") ||
                                        a.getStatus().equalsIgnoreCase("DONE") ||
                                        a.getStatus().equalsIgnoreCase("FINISHED"));

                        System.out.println("Appointment ID: " + a.getId() +
                                " | Status: " + a.getStatus() +
                                " | Week: " + appointmentWeek +
                                " | IsCompleted: " + isCompleted);

                        return appointmentWeek == weekNumber && isCompleted;
                    } catch (ParseException e) {
                        return false;
                    }
                })
                .count();
    }

    private int countAppointmentsByMonth(List<Appointment> appointments, int monthNumber) {
        Calendar cal = Calendar.getInstance();
        return (int) appointments.stream()
                .filter(a -> {
                    try {
                        if (a.getDate() == null) return false;

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = sdf.parse(a.getDate());
                        cal.setTime(date);
                        return cal.get(Calendar.MONTH) + 1 == monthNumber;
                    } catch (ParseException e) {
                        return false;
                    }
                })
                .count();
    }

    private int countCompletedAppointmentsByMonth(List<Appointment> appointments, int monthNumber) {
        Calendar cal = Calendar.getInstance();
        return (int) appointments.stream()
                .filter(a -> {
                    try {
                        if (a.getDate() == null || a.getStatus() == null) return false;

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = sdf.parse(a.getDate());
                        cal.setTime(date);
                        return cal.get(Calendar.MONTH) + 1 == monthNumber &&
                                ("COMPLETED".equalsIgnoreCase(a.getStatus()) ||
                                        "DONE".equalsIgnoreCase(a.getStatus()));
                    } catch (ParseException e) {
                        return false;
                    }
                })
                .count();
    }

    private int countAppointmentsByDayOfWeek(List<Appointment> appointments, int dayOfWeek) {
        Calendar cal = Calendar.getInstance();
        return (int) appointments.stream()
                .filter(a -> {
                    try {
                        if (a.getDate() == null) return false;

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = sdf.parse(a.getDate());
                        cal.setTime(date);
                        return cal.get(Calendar.DAY_OF_WEEK) == dayOfWeek;
                    } catch (ParseException e) {
                        return false;
                    }
                })
                .count();
    }

    private int countCompletedAppointmentsByDayOfWeek(List<Appointment> appointments, int dayOfWeek) {
        Calendar cal = Calendar.getInstance();
        return (int) appointments.stream()
                .filter(a -> {
                    try {
                        if (a.getDate() == null || a.getStatus() == null) return false;

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = sdf.parse(a.getDate());
                        cal.setTime(date);
                        return cal.get(Calendar.DAY_OF_WEEK) == dayOfWeek &&
                                ("COMPLETED".equalsIgnoreCase(a.getStatus()) ||
                                        "DONE".equalsIgnoreCase(a.getStatus()));
                    } catch (ParseException e) {
                        return false;
                    }
                })
                .count();
    }
}
