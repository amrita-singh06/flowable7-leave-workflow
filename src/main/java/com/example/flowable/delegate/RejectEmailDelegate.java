package com.example.flowable.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component("rejectEmailDelegate")
public class RejectEmailDelegate implements JavaDelegate {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void execute(DelegateExecution execution) {
        String email = (String) execution.getVariable("employeeEmail");
        String employee = (String) execution.getVariable("employee");

        if (email == null || !email.contains("@")) {
            System.out.println("Invalid email for rejection: " + email);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your leave is rejected");
        message.setText("Hello " + employee + ",\n\nYour leave request has been rejected.\n\nRegards,\nHR Team");
        mailSender.send(message);

        System.out.println("Rejection email sent to: " + email);
    }
}
