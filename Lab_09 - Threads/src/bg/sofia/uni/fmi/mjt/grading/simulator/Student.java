package bg.sofia.uni.fmi.mjt.grading.simulator;

import bg.sofia.uni.fmi.mjt.grading.simulator.assignment.Assignment;
import bg.sofia.uni.fmi.mjt.grading.simulator.assignment.AssignmentType;
import bg.sofia.uni.fmi.mjt.grading.simulator.grader.StudentGradingAPI;

import java.util.Random;

public class Student implements Runnable {

    private final static int MAX_WAIT_TIME = 1000;
    private final int fn;
    private final String name;
    private final StudentGradingAPI studentGradingAPI;

    public Student(int fn, String name, StudentGradingAPI studentGradingAPI) {
        this.fn = fn;
        this.name = name;
        this.studentGradingAPI = studentGradingAPI;
    }

    @Override
    public void run() {
        AssignmentType[] assignmentTypes = AssignmentType.values();
        AssignmentType assignmentType = assignmentTypes[new Random().nextInt(assignmentTypes.length)];
        Assignment assignment = new Assignment(fn, name, assignmentType);

        try {
            Thread.sleep(new Random().nextInt(MAX_WAIT_TIME));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        studentGradingAPI.submitAssignment(assignment);
    }

    public int getFn() {
        return fn;
    }

    public String getName() {
        return name;
    }

    public StudentGradingAPI getGrader() {
        return studentGradingAPI;
    }
}