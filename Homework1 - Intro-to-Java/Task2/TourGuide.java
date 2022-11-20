public class TourGuide {
    public static int getBestSightseeingPairScore(int[] places) {

        if (places == null) {
            return 0;
        }

        int bestScore = 0;
        int indexOfFirstLocationInThePair = 0;
        int ratingOfFirstLocationInThePair = 0;

        for (int i = 0; i < places.length - 1; i++) {
            if (ratingOfFirstLocationInThePair + indexOfFirstLocationInThePair < places[i] + i) {
                for (int j = i + 1; j < places.length; j++) {
                    int currentScore = places[i] + places[j] + i - j;
                    if (bestScore < currentScore) {
                        bestScore = currentScore;
                        ratingOfFirstLocationInThePair = places[i];
                        indexOfFirstLocationInThePair = i;
                    }
                }
            }
        }

        return bestScore;
    }
}
