package bg.sofia.uni.fmi.mjt.grading.simulator.grader;

import bg.sofia.uni.fmi.mjt.grading.simulator.Assistant;
import bg.sofia.uni.fmi.mjt.grading.simulator.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodePosterGraderTest {

    @Test
    @Timeout(5)
    public void testFullClassExecution() throws InterruptedException {
        AdminGradingAPI adminGradingAPI = new CodePostGrader(3);

        Thread s1 = new Thread(new Student(1, "n1", adminGradingAPI));
        Thread s2 = new Thread(new Student(2, "n2", adminGradingAPI));
        Thread s3 = new Thread(new Student(3, "n3", adminGradingAPI));

        s1.start();
        s2.start();
        s3.start();

        s1.join();
        s2.join();
        s3.join();

        adminGradingAPI.finalizeGrading();
        adminGradingAPI.getAssistants().forEach(x -> {
            try {
                x.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        int assignmentsChecked = adminGradingAPI.getAssistants()
                .stream()
                .mapToInt(Assistant::getNumberOfGradedAssignments)
                .sum();

        assertEquals(3, assignmentsChecked);
        assertEquals(3, adminGradingAPI.getSubmittedAssignmentsCount());
    }
}
