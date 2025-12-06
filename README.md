# Flowable 7 Leave Workflow (Spring Boot)

A simple Leave Approval Workflow built using Flowable 7 and Spring Boot.
The workflow allows an employee to submit a leave request which HR can Approve or Reject, and sends an email notification based on the decision.

## 🚀 Features

BPMN-based Leave Approval Process

Approve / Reject workflow with HR task

Automatic email on approval or rejection

REST API endpoints to start and complete tasks

Clean Spring Boot + Flowable architecture

## 📂 Key Components

leave-approval.bpmn20.xml – Workflow definition

SubmitLeaveDelegate – Handles request submission

HRProcessingDelegate – HR review logic

ApproveLeaveDelegate – Sends approval email

RejectionDelegate – Sends rejection email

LeaveController – REST endpoints
