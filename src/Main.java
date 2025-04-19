class Main {
    public static void main(String[] args) {
//        if (args.length != 5) {
//            System.out.println("Usage: java Main <type> <mapData.txt> <distance.txt> <objectives.txt> <output.txt>");
//            System.out.println("type: 0 for Regular Execution, 1 for Bonus Execution");
//            return;
//        } else {
//            int type = Integer.parseInt(args[0]);
//            String mapData = args[1];
//            String mapDistanceData = args[2];
//            String objectiveData = args[3];
//            String outputFile = args[4];
//        }
        int type = 0; // 0 for RegularExecution, 1 for BonusExecution
        String whichMap = "20-20"; // 20-20-Bonus for BonusExecution
        String mapData = String.format("test_cases/%s/mapData.txt", whichMap);
        String mapDistanceData = String.format("test_cases/%s/distances.txt", whichMap);
        String objectiveData = String.format("test_cases/%s/objectives.txt", whichMap);
        String outputFile = String.format("test_cases/%s/output.txt", whichMap);
        Environment map = new Environment();
        map.generateMap(mapData, mapDistanceData, objectiveData, outputFile, type);
    }
}