package com.example.flowable.controller;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/leave")
public class LeaveController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    // STEP 1 - start process
    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(@RequestParam String employee,
                                        @RequestParam String employeeEmail,
                                        @RequestParam int days) {

        Map<String, Object> vars = new HashMap<>();
        vars.put("employee", employee);
        vars.put("employeeEmail", employeeEmail);
        vars.put("days", days);

        var process = runtimeService.startProcessInstanceByKey("leaveApproval", vars);

        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "Leave workflow started");
        resp.put("processId", process.getId());
        return ResponseEntity.ok(resp);
    }

    // STEP 2 - manager fetch tasks
    @GetMapping("/tasks/manager")
    public List<TaskDto> getManagerTasks() {
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateGroup("manager")
                .list();

        return tasks.stream()
                .map(t -> new TaskDto(t.getId(), t.getName(), t.getAssignee()))
                .collect(Collectors.toList());
    }

    // STEP 2.5 - claim
    @PostMapping("/manager/claim")
    public ResponseEntity<?> claimTask(@RequestParam String taskId, @RequestParam String managerUser) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) return ResponseEntity.badRequest().body("Invalid Task ID: " + taskId);
        if (task.getAssignee() != null) return ResponseEntity.badRequest().body("Already assigned to " + task.getAssignee());
        taskService.claim(taskId, managerUser);
        return ResponseEntity.ok("Task claimed by " + managerUser);
    }

    // STEP 3 - manager action (complete)
    @PostMapping("/manager/action")
    public ResponseEntity<?> managerAction(@RequestParam String taskId,
                                           @RequestParam boolean approved,
                                           @RequestParam String managerUser) {

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) return ResponseEntity.badRequest().body("Invalid Task ID: " + taskId);

        if (task.getAssignee() == null) {
            taskService.claim(taskId, managerUser);
        }

        Map<String, Object> vars = new HashMap<>();
        vars.put("approved", approved);

        taskService.complete(taskId, vars);

        return ResponseEntity.ok(approved ? "Manager Approved" : "Manager Rejected"); 
    }

    // STEP 4 - check status by processId
    @GetMapping("/status/{processId}")
    public ResponseEntity<?> getStatus(@PathVariable String processId) {
        HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processId).includeProcessVariables().singleResult();
        if (instance == null) return ResponseEntity.notFound().build();
        Map<String,Object> vars = instance.getProcessVariables();
        Boolean approved = vars.containsKey("approved") ? (Boolean) vars.get("approved") : null;
        String status = approved == null ? "PENDING" : (approved ? "APPROVED" : "REJECTED");

        Map<String,Object> resp = new HashMap<>();
        resp.put("processId", processId);
        resp.put("employee", vars.get("employee"));
        resp.put("days", vars.get("days"));
        resp.put("approved", approved);
        resp.put("status", status);
        return ResponseEntity.ok(resp);
    }

    public record TaskDto(String id, String name, String assignee) {}
}
