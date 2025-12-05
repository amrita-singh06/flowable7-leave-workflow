package com.example.flowable.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component("hrProcessingDelegate")
public class HrProcessingDelegate implements JavaDelegate {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void execute(DelegateExecution execution) {
        String email = (String) execution.getVariable("employeeEmail");
        String employee = (String) execution.getVariable("employee");

        System.out.println("HR processing leave for " + employee + " -> will email " + email);

        if (email == null || !email.contains("@")) {
            System.out.println("Invalid or missing employeeEmail: " + email);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Leave Request Processed by HR");
        message.setText("Hello " + employee + ",\n\nHR has processed your leave request.\n\nRegards,\nHR Team");

        mailSender.send(message);
        System.out.println("HR email sent to: " + email);
    }
}
