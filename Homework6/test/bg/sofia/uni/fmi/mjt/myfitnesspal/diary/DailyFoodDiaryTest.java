package bg.sofia.uni.fmi.mjt.myfitnesspal.diary;

import bg.sofia.uni.fmi.mjt.myfitnesspal.exception.UnknownFoodException;
import bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition.NutritionInfo;
import bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition.NutritionInfoAPI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DailyFoodDiaryTest {

    @Mock
    private NutritionInfoAPI nutritionInfoAPIMock;

    @InjectMocks
    private DailyFoodDiary dailyFoodDiary;

    public void addSampleFoodEntriesToDailyFoodDiary(List<FoodEntry> foodEntries) throws UnknownFoodException {
        when(nutritionInfoAPIMock.getNutritionInfo(foodEntries.get(0).food()))
                .thenReturn(foodEntries.get(0).nutritionInfo());

        when(nutritionInfoAPIMock.getNutritionInfo(foodEntries.get(2).food()))
                .thenReturn(foodEntries.get(2).nutritionInfo());

        dailyFoodDiary.addFood(Meal.LUNCH, foodEntries.get(0).food(), foodEntries.get(0).servingSize());
        dailyFoodDiary.addFood(Meal.LUNCH, foodEntries.get(2).food(), foodEntries.get(2).servingSize());
        dailyFoodDiary.addFood(Meal.DINNER, foodEntries.get(1).food(), foodEntries.get(1).servingSize());
    }

    public List<FoodEntry> getSampleFoodEntries() {
        String foodName1 = "Rice";
        double servingSize1 = 1;
        NutritionInfo nutritionInfo1 = new NutritionInfo(80,5,15);
        FoodEntry entry1 = new FoodEntry(foodName1, servingSize1, nutritionInfo1);

        String foodName2 = "Meat";
        double servingSize2 = 2;
        NutritionInfo nutritionInfo2 = new NutritionInfo(5,10,85);
        FoodEntry entry2 = new FoodEntry(foodName2, servingSize2, nutritionInfo2);

        double servingSize3 = 1;
        FoodEntry entry3 = new FoodEntry(foodName2, servingSize3, nutritionInfo2);

        return new ArrayList<>() {{add(entry1); add(entry3); add(entry2);}};
    }

    @Test
    public void testAddFoodThrowsWhenGivenMealIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> dailyFoodDiary.addFood(null, "meat", 1),
                "addFood should throw IllegalArgumentException when meal is null");
    }

    @Test
    public void testAddFoodThrowsWhenGivenFoodNameIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> dailyFoodDiary.addFood(Meal.LUNCH, null, 1),
                "addFood should throw IllegalArgumentException when foodName is null");
    }

    @Test
    public void testAddFoodThrowsWhenGivenFoodNameIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> dailyFoodDiary.addFood(Meal.LUNCH, " ", 1),
                "addFood should throw IllegalArgumentException when foodName is blank");
    }

    @Test
    public void testAddFoodThrowsWhenGivenServingSizeIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> dailyFoodDiary.addFood(Meal.LUNCH, "Meat", -1),
                "addFood should throw IllegalArgumentException when servingSize is negative");
    }

    @Test
    public void testAddFoodThrowsWhenThereIsNoNutritionInfoForTheGivenFood() throws UnknownFoodException {
        String foodName = "Lutenica Olinezko";
        when(nutritionInfoAPIMock.getNutritionInfo(foodName))
                .thenThrow(new UnknownFoodException("There is no info about that food"));

        assertThrows(UnknownFoodException.class,
                () -> dailyFoodDiary.addFood(Meal.LUNCH, foodName, 3),
                "addFood should throw UnknownFoodException when there is no information about the given food");
    }

    @Test
    public void testAddFoodReturnsCorrectFoodEntryWithCorrectParameters() throws UnknownFoodException {
        String foodName = "Rice";
        double servingSize = 150;
        NutritionInfo nutritionInfo = new NutritionInfo(80,5,15);

        when(nutritionInfoAPIMock.getNutritionInfo(foodName))
                .thenReturn(nutritionInfo);

        FoodEntry expected = new FoodEntry(foodName, servingSize, nutritionInfo);

        assertEquals(expected, dailyFoodDiary.addFood(Meal.LUNCH, foodName, servingSize),
                "addFood return correct foodEntry with correct data");
    }

    @Test
    public void testGetAllFoodEntriesReturnsEmptyCollectionWhenNoFoodIsAdded() {
        assertTrue(dailyFoodDiary.getAllFoodEntries().isEmpty(),
                "Returned collection should be empty");
    }

    @Test
    public void testGetAllFoodEntriesReturnsCollectionWithAllFoodEntries() throws UnknownFoodException {
        List<FoodEntry> expected = getSampleFoodEntries();
        addSampleFoodEntriesToDailyFoodDiary(expected);
        Collection<FoodEntry> actual = dailyFoodDiary.getAllFoodEntries();

        assertTrue(expected.size() == actual.size() &&
                        expected.containsAll(actual) && actual.containsAll(expected),
                "Returned collection should contain all food entries");
    }

    @Test
    public void testGetAllFoodEntriesByProteinContentReturnsSortedByProteinContentCollection() throws UnknownFoodException {
        List<FoodEntry> expected = getSampleFoodEntries();
        addSampleFoodEntriesToDailyFoodDiary(expected);

        assertIterableEquals(expected, dailyFoodDiary.getAllFoodEntriesByProteinContent(),
                "Returned collection should be sorted by protein content");
    }

    @Test
    public void testGetDailyCaloriesIntakePerMealThrowsWhenMealIsNull() {
        assertThrows(IllegalArgumentException.class, () -> dailyFoodDiary.getDailyCaloriesIntakePerMeal(null),
                "getDailyCaloriesIntakePerMeal should throw IllegalArgumentException when meal is null");
    }

    @Test
    public void testGetDailyCaloriesIntakePerMealReturnsZeroWhenNoFoodEntriesAreAddedForTheMeal() {
        assertEquals(0.0, dailyFoodDiary.getDailyCaloriesIntakePerMeal(Meal.DINNER),
                "getDailyCaloriesIntakePerMeal should return 0.0 when no food entries are added for the meal");
    }

    @Test
    public void testGetDailyCaloriesIntakePerMealReturnsCorrectCalories() throws UnknownFoodException {
        List<FoodEntry> foodEntries = getSampleFoodEntries();
        addSampleFoodEntriesToDailyFoodDiary(foodEntries);

        double expectedCalories = 1325;
        assertEquals(expectedCalories, dailyFoodDiary.getDailyCaloriesIntakePerMeal(Meal.LUNCH),
                "getDailyCaloriesIntakePerMeal should return correct calories amount for the meal");
    }

    @Test
    public void testGetDailyCaloriesIntakeReturnsZeroWhenNoFoodAreAdded() {
        assertEquals(0.0, dailyFoodDiary.getDailyCaloriesIntake(),
                "getDailyCaloriesIntake should return 0.0 when no food is added for the day");
    }

    @Test
    public void testGetDailyCaloriesIntakeReturnsCorrectCalories() throws UnknownFoodException {
        List<FoodEntry> foodEntries = getSampleFoodEntries();
        addSampleFoodEntriesToDailyFoodDiary(foodEntries);

        double expectedCalories = 1775;
        assertEquals(expectedCalories, dailyFoodDiary.getDailyCaloriesIntake(),
                "getDailyCaloriesIntake should return correct calories amount for the day");
    }
}
