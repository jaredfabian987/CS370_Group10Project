public class User {
    private int userID;
    // log in info
    private String username;
    private String password;
    // we are going to edit this later so that fitness profile is a seperate class from User 
    // and so that fitnessProfile inherits user
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
