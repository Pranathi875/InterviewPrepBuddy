public class SpacedRepetition {

    /**
     * Calculates the next review interval based on attempt result.
     *
     * SOLVED_EASILY    → interval × 2
     * SOLVED_WITH_HELP → interval stays the same
     * COULD_NOT_SOLVE  → interval resets to 1
     *
     * @param result          the attempt result
     * @param currentInterval the current interval in days
     * @return the new interval in days
     */
    public static int calculateNextInterval(String result, int currentInterval) {
        return switch (result) {
            case "SOLVED_EASILY" -> currentInterval * 2;
            case "SOLVED_WITH_HELP" -> currentInterval;
            case "COULD_NOT_SOLVE" -> 1;
            default -> throw new IllegalArgumentException("Unknown result: " + result);
        };
    }
}
