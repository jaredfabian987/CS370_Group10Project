public class Exercise {
    private int userID;
    // log in info
    private String username;
    private String passwordHash;
    // profile
    private String firstName;
    private String LastName;
    private int age;
    private String gender;
    //
    public enum FitnessLevel {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
    private FitnessLevel fitnessLevel;
    private String goal;
    private int frequencyPerWeek;
    private int minsPerSession;
}
