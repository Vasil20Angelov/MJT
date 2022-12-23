package bg.sofia.uni.fmi.mjt.grading.simulator;

import bg.sofia.uni.fmi.mjt.grading.simulator.assignment.Assignment;
import bg.sofia.uni.fmi.mjt.grading.simulator.grader.AdminGradingAPI;

public class Assistant extends Thread {

    private final String name;
    private final AdminGradingAPI adminGradingAPI;
    private int gradedAssignmentsCount = 0;

    public Assistant(String name, AdminGradingAPI grader) {
        this.name = name;
        this.adminGradingAPI = grader;
    }

    @Override
    public void run() {

        Assignment assignment;
        while ((assignment = adminGradingAPI.getAssignment()) != null) {
            try {
                Thread.sleep(assignment.type().getGradingTime());
                gradedAssignmentsCount++;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getAssistantName() {
        return name;
    }
    public int getNumberOfGradedAssignments() {
        return gradedAssignmentsCount;
    }

}