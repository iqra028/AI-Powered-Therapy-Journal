package org.example.seproject1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class EmailController {

    @Autowired
    private EmailReminderService emailReminderService;


    @PostMapping("/remind-inactive")
    public ResponseEntity<?> sendReminderToInactiveUsers() {
        try {
            int emailsSent = emailReminderService.sendManualReminderToInactiveUsers();
            return ResponseEntity.ok(new EmailResponse("Reminder emails sent successfully", emailsSent));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to send emails: " + e.getMessage()));
        }
    }

    public static class EmailResponse {
        private String message;
        private int emailsSent;

        public EmailResponse(String message, int emailsSent) {
            this.message = message;
            this.emailsSent = emailsSent;
        }

        public String getMessage() { return message; }
        public int getEmailsSent() { return emailsSent; }
    }

    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() { return error; }
    }
}