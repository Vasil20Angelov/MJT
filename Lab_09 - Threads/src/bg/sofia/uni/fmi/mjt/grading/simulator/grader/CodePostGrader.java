package bg.sofia.uni.fmi.mjt.grading.simulator.grader;

import bg.sofia.uni.fmi.mjt.grading.simulator.Assistant;
import bg.sofia.uni.fmi.mjt.grading.simulator.assignment.Assignment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class CodePostGrader implements AdminGradingAPI {

    private boolean finalizedGrading = false;
    private AtomicInteger submittedAssignmentsCount = new AtomicInteger(0);
    private final List<Assistant> assistants = new ArrayList<>();
    private final Queue<Assignment> assignments = new LinkedList<>();

    public CodePostGrader(int assistantsCount) {
        for (int i = 1; i <= assistantsCount; i++) {
            Assistant assistant = new Assistant("Assistant " + i, this);
            assistants.add(assistant);
            assistant.start();
        }
    }

    @Override
    public synchronized Assignment getAssignment() {

        while (assignments.isEmpty() && !finalizedGrading) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return !assignments.isEmpty() ? assignments.poll() : null;
    }

    @Override
    public int getSubmittedAssignmentsCount() {
        return submittedAssignmentsCount.get();
    }

    @Override
    public synchronized void finalizeGrading() {
        finalizedGrading = true;
        notifyAll();
    }

    @Override
    public List<Assistant> getAssistants() {
        return assistants;
    }

    @Override
    public synchronized void submitAssignment(Assignment assignment) {
        if (!finalizedGrading) {
            submittedAssignmentsCount.incrementAndGet();
            assignments.add(assignment);
            notifyAll();
        }
    }
}
