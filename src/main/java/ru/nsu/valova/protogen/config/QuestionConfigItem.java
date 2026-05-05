package ru.nsu.valova.protogen.config;

public class QuestionConfigItem {
    private String code;
    private QuestionType type;
    private int course;
    private int semester;
    private String educationalProfile;
    private String educationalProfileShort;
    private String degreeLevel;
    private String practiceTypeNominative;
    private String practiceTypeGenitive;
    private int sheetNumber;

    public QuestionConfigItem() {}

    public QuestionConfigItem(String code, QuestionType type, int course, int semester,
                              String educationalProfile, String educationalProfileShort,
                              String degreeLevel, String practiceTypeNominative,
                              String practiceTypeGenitive, int sheetNumber) {
        this.code = code;
        this.type = type;
        this.course = course;
        this.semester = semester;
        this.educationalProfile = educationalProfile;
        this.educationalProfileShort = educationalProfileShort;
        this.degreeLevel = degreeLevel;
        this.practiceTypeNominative = practiceTypeNominative;
        this.practiceTypeGenitive = practiceTypeGenitive;
        this.sheetNumber = sheetNumber;
    }

    public String getCode() { return code; }
    public QuestionType getType() { return type; }
    public int getCourse() { return course; }
    public int getSemester() { return semester; }
    public String getEducationalProfile() { return educationalProfile; }
    public String getEducationalProfileShort() { return educationalProfileShort; }
    public String getDegreeLevel() { return degreeLevel; }
    public String getPracticeTypeNominative() { return practiceTypeNominative; }
    public String getPracticeTypeGenitive() { return practiceTypeGenitive; }
    public int getSheetNumber() { return sheetNumber; }

    public void setCode(String code) { this.code = code; }
    public void setType(QuestionType type) { this.type = type; }
    public void setCourse(int course) { this.course = course; }
    public void setSemester(int semester) { this.semester = semester; }
    public void setEducationalProfile(String educationalProfile) { this.educationalProfile = educationalProfile; }
    public void setEducationalProfileShort(String educationalProfileShort) { this.educationalProfileShort = educationalProfileShort; }
    public void setDegreeLevel(String degreeLevel) { this.degreeLevel = degreeLevel; }
    public void setPracticeTypeNominative(String practiceTypeNominative) { this.practiceTypeNominative = practiceTypeNominative; }
    public void setPracticeTypeGenitive(String practiceTypeGenitive) { this.practiceTypeGenitive = practiceTypeGenitive; }
    public void setSheetNumber(int sheetNumber) { this.sheetNumber = sheetNumber; }
}
