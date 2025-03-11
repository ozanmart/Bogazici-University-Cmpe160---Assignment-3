class Main {
    public static void main(String[] args) {
        String whichMap = "20-20";
        String mapData = String.format("test_cases/%s/mapData.txt", whichMap);
        String mapDistanceData = String.format("test_cases/%s/distances.txt", whichMap);
        String objectiveData = String.format("test_cases/%s/objectives.txt", whichMap);
        String outputFile = String.format("test_cases/%s/output.txt", whichMap);
        Environment map = new Environment();
        map.generateMap(mapData, mapDistanceData, objectiveData, outputFile);
    }
}
