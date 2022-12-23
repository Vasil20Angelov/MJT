package bg.sofia.uni.fmi.mjt.grading.simulator;

import bg.sofia.uni.fmi.mjt.grading.simulator.assignment.Assignment;
import bg.sofia.uni.fmi.mjt.grading.simulator.grader.CodePostGrader;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class StudentTest {

    @Mock
    private CodePostGrader codePostGraderMock = Mockito.mock(CodePostGrader.class);

    @Test
    public void testRunFinishesInTimeAndSubmitsAssignment() throws InterruptedException {
        doNothing().when(codePostGraderMock).submitAssignment(any(Assignment.class));

        Thread thread = new Thread(new Student(1, "s1", codePostGraderMock));

        thread.start();
        thread.join(1100);

       verify(codePostGraderMock, times(1)).submitAssignment(any(Assignment.class));
    }
}
