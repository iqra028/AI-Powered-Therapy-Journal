package org.example.seproject1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class EmailReminderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private JavaMailSender emailSender;


    @Scheduled(cron = "0 05 9 * * ?") // Runs at 10:35 AM every day
    public int sendDailyReminderEmails() {
        try {
            System.out.println("Starting daily reminder email job...");

            checkEmailConfiguration();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date startOfToday = calendar.getTime();

            List<User> allUsers = userRepository.findAll();
            int emailsSent = 0;
            int emailsFailed = 0;

            System.out.println("Found " + allUsers.size() + " total users");

            for (User user : allUsers) {
                String email = user.getEmail();

                if (email == null || !isValidEmail(email)) {
                    System.out.println("Skipping invalid email: " + email);
                    continue;
                }

                boolean hasJournalledToday = hasUserJournalledToday(user.getId(), startOfToday);

                if (!hasJournalledToday) {
                    System.out.println("Sending reminder to: " + email + " (User hasn't journaled today)");
                    try {
                        sendReminderEmail(user);
                        emailsSent++;
                        System.out.println("Successfully sent email to: " + email);
                    } catch (MailException e) {
                        emailsFailed++;
                        System.err.println("Failed to send reminder email to " + email + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Skipping user: " + email + " (Already journaled today)");
                }
            }

            System.out.println("Daily reminder job completed. Sent " + emailsSent + " reminder emails. Failed: " + emailsFailed);
            return emailsSent;
        } catch (Exception e) {
            System.err.println("Fatal error in email reminder job: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    private void checkEmailConfiguration() {
        try {
            if (emailSender == null) {
                System.err.println("ERROR: JavaMailSender is null. Check your email configuration.");
            } else {
                System.out.println("Email sender initialized successfully.");
            }
        } catch (Exception e) {
            System.err.println("Error checking email configuration: " + e.getMessage());
        }
    }

    public int sendManualReminderToInactiveUsers() {
        return sendDailyReminderEmails();
    }

    private boolean hasUserJournalledToday(String userId, Date startTime) {
        List<JournalEntry> recentEntries = journalEntryRepository.findByUserIdAndDateAfter(userId, startTime);
        return !recentEntries.isEmpty();
    }


    private void sendReminderEmail(User user) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Daily Journal Reminder");
        message.setText("Hello " + user.getUsername() + ",\n\n" +
                "Just a friendly reminder to write in your journal today. " +
                "Taking a few minutes to reflect can make a big difference in your day!\n\n" +
                "Best regards,\n" +
                "Your Journal App Team");

        emailSender.send(message);
    }


    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }
}