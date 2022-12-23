package bg.sofia.uni.fmi.mjt.grading.simulator;

import bg.sofia.uni.fmi.mjt.grading.simulator.assignment.Assignment;
import bg.sofia.uni.fmi.mjt.grading.simulator.assignment.AssignmentType;
import bg.sofia.uni.fmi.mjt.grading.simulator.grader.CodePostGrader;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class AssistantTest {

    @Mock
    private CodePostGrader codePostGraderMock = Mockito.mock(CodePostGrader.class);

    @Test
    public void testRunFinishesInTimeAndUpdatesCount() throws InterruptedException {
        when(codePostGraderMock.getAssignment())
                .thenReturn(new Assignment(1, "s1", AssignmentType.HOMEWORK), null);

        Assistant assistant = new Assistant("a1", codePostGraderMock);

        assistant.start();
        assistant.join(100);

        assertEquals(1, assistant.getNumberOfGradedAssignments());
    }
}
