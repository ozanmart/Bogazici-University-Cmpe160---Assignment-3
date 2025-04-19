class Main {
    public static void main(String[] args) {
        String mapData, mapDistanceData, objectiveData, outputFile;
        outputFile = "output.txt";
        if (args.length != 4) {
            // usage:
            // javac -d out -cp "path/stdlib.jar" src/*.java
            // java -cp "out:path/stdlib.jar" Main -draw test_cases/20-20/mapData.txt test_cases/20-20/distances.txt test_cases/20-20/objectives.txt
            return;
        } else {
            mapData = args[1];
            mapDistanceData = args[2];
            objectiveData = args[3];
        }
        int type = 0; // 0 for RegularExecution, 1 for BonusExecution
        // String whichMap = "20-20"; // 20-20-Bonus for BonusExecution
        // String mapData = String.format("test_cases/%s/mapData.txt", whichMap);
        // String mapDistanceData = String.format("test_cases/%s/distances.txt", whichMap);
        // String objectiveData = String.format("test_cases/%s/objectives.txt", whichMap);
        // String outputFile = String.format("test_cases/%s/output.txt", whichMap);
        Environment map = new Environment();
        map.generateMap(mapData, mapDistanceData, objectiveData, outputFile, type);
    }
}
