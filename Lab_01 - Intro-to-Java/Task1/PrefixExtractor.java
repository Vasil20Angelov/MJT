public class PrefixExtractor {
    public static String getLongestCommonPrefix(String[] words) {
        if (words == null || words.length == 0) {
            return "";
        }

        int lastCommonCharIndex = -1;
        for (int i = 0; i < words[0].length(); i++) {
            if (!haveTheSameCharAtIndex(words, i)) {
                break;
            }
            lastCommonCharIndex++;
        }

        if (lastCommonCharIndex == -1) {
            return "";
        }

        return words[0].substring(0,lastCommonCharIndex + 1);
    }

    private static boolean haveTheSameCharAtIndex(String[] words, int index) {
        char currentChar = words[0].charAt(index);
        for (int i = 1; i < words.length; i++) {
            if (words[i].length() <= index || words[i].charAt(index) != currentChar) {
                return false;
            }
        }
        return true;
    }
}
