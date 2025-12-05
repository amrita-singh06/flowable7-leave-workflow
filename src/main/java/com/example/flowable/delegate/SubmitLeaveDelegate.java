package com.example.flowable.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("submitLeaveDelegate")
public class SubmitLeaveDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        String employee = (String) execution.getVariable("employee");
        Integer days = (Integer) execution.getVariable("days");
        System.out.println("Employee " + employee + " applied for " + days + " days leave.");
    }
}
