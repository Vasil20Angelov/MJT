public class DataCenter {
    public static int getCommunicatingServersCount(int[][] map) {

        if (map == null || map.length == 0) {
            return 0;
        }

        int rows = map.length;
        int columns = map[0].length;
        boolean[] connectionInRows = ConnectionsInLine(rows, columns, map, false);
        boolean[] connectionInColumns = ConnectionsInLine(columns, rows, map, true);

        int conectionsCount = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (map[i][j] == 1 && (connectionInRows[i] || connectionInColumns[j])) {
                    conectionsCount++;
                }
            }
        }
        return conectionsCount;
    }

    private static boolean[] ConnectionsInLine(int n, int m, int[][] map, boolean fillingColumns) {

        boolean[] connections = new boolean[n];
        for (int i = 0; i < n; i++) {
            boolean pcFound = false;
            for (int j = 0; j < m; j++) {
                if ((!fillingColumns && map[i][j] == 0) || (fillingColumns && map[j][i] == 0)) {
                    continue;
                }
                if (!pcFound) {
                    pcFound = true;
                }
                else {
                    connections[i] = true;
                }
            }
        }

        return connections;
    }
}
