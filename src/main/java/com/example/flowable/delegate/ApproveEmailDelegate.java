package com.example.flowable.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component("approveEmailDelegate")
public class ApproveEmailDelegate implements JavaDelegate {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void execute(DelegateExecution execution) {
        String email = (String) execution.getVariable("employeeEmail");
        String employee = (String) execution.getVariable("employee");

        if (email == null || !email.contains("@")) {
            System.out.println("Invalid email for approval: " + email);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your leave is approved");
        message.setText("Hello " + employee + ",\n\nYour leave request has been approved.\n\nRegards,\nHR Team");
        mailSender.send(message);

        System.out.println("Approval email sent to: " + email);
    }
}
