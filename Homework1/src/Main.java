public class Main {
    public static void main(String[] args) {
        testPrefixExtractor();
        TestTourGuide();
        TestDataCenter();
    }

    public static void testPrefixExtractor() {
        System.out.println(PrefixExtractor.getLongestCommonPrefix(new String[]{"flower", "flow", "flight"}));
        System.out.println(PrefixExtractor.getLongestCommonPrefix(new String[]{"dog", "racecar", "car"}));
        System.out.println(PrefixExtractor.getLongestCommonPrefix(new String[]{"cat"}));
        System.out.println(PrefixExtractor.getLongestCommonPrefix(new String[]{ }));
        System.out.println(PrefixExtractor.getLongestCommonPrefix(null));
    }

    public static void TestTourGuide() {
        System.out.println(TourGuide.getBestSightseeingPairScore(new int[]{8, 1, 5, 2, 6}));
        System.out.println(TourGuide.getBestSightseeingPairScore(new int[]{3, 5, 1, 4, 6}));
        System.out.println(TourGuide.getBestSightseeingPairScore(new int[]{1, 2}));
        System.out.println(TourGuide.getBestSightseeingPairScore(null));
    }

    public static void TestDataCenter() {
        System.out.println(DataCenter.getCommunicatingServersCount(new int[][]{{1, 0}, {0, 1}}));
        System.out.println(DataCenter.getCommunicatingServersCount(new int[][]{{1, 0}, {1, 1}}));
        System.out.println(DataCenter.getCommunicatingServersCount(new int[][]{{1, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}}));
        System.out.println(DataCenter.getCommunicatingServersCount(new int[][] {}));
        System.out.println(DataCenter.getCommunicatingServersCount(null));
    }
}