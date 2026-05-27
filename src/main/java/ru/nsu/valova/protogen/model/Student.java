package ru.nsu.valova.protogen.model;

public class Student {
    private String fullName;
    private String educationalProfile;      //"Программная инженерия и компьютерные науки"
    private String group;                   //"23202"
    private int course;
    private String practiceType;            //"Учебная практика (эксплуатационная практика)"
    private String practiceBase;            //"ИМ СО РАН, Лаборатория теории вычислимости и прикладной логики"

    private String supervisorFullName;
    private String supervisorPosition;      // должность
    private String supervisorDegree;        // степень
    private String supervisorAcademicTitle; // звание
    private String supervisorJobPlace;

    private String nsuSupervisorFullName;
    private String nsuSupervisorPosition;
    private String nsuSupervisorDegree;
    private String nsuSupervisorAcademicTitle;
    private String nsuSupervisorJobPlace;

    private String instituteSupervisorFullName;
    private String instituteSupervisorPosition;
    private String instituteSupervisorDegree;
    private String instituteSupervisorAcademicTitle;
    private String instituteSupervisorJobPlace;

    private String practiceBaseFull;        // место практики полностью
    private String practiceBaseName;        // "ИМ СО РАН"
    private String email;
    private String educationalProgram;      //"09.03.01 Информатика и вычислительная техника

    private int sheetNumber;

    private String fullPlaceOfInternship;
    private String organizationName;

    private String thesisTopic;

    private String thesisSupervisorFullName;
    private String thesisSupervisorDegree;
    private String thesisSupervisorTitle;
    private String thesisSupervisorPosition;
    private String thesisSupervisorJobPlace;

    private String thesisCoSupervisorFull;
    private String thesisConsultant;

    public Student() {}

    public Student(String fullName, String group, String practiceBase,
                   String nsuSupervisorFullName, String nsuSupervisorPosition,
                   String nsuSupervisorDegree, String nsuSupervisorAcademicTitle,
                   String instituteSupervisorFullName, String instituteSupervisorPosition,
                   String instituteSupervisorDegree, String instituteSupervisorAcademicTitle,
                   int sheetNumber) {
        this.fullName = fullName;
        this.group = group;
        this.practiceBase = practiceBase;
        this.nsuSupervisorFullName = nsuSupervisorFullName;
        this.nsuSupervisorPosition = nsuSupervisorPosition;
        this.nsuSupervisorDegree = nsuSupervisorDegree;
        this.nsuSupervisorAcademicTitle = nsuSupervisorAcademicTitle;
        this.instituteSupervisorFullName = instituteSupervisorFullName;
        this.instituteSupervisorPosition = instituteSupervisorPosition;
        this.instituteSupervisorDegree = instituteSupervisorDegree;
        this.instituteSupervisorAcademicTitle = instituteSupervisorAcademicTitle;
        this.sheetNumber = sheetNumber;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEducationalProfile() { return educationalProfile; }
    public void setEducationalProfile(String educationalProfile) { this.educationalProfile = educationalProfile; }

    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }

    public int getCourse() { return course; }
    public void setCourse(int course) { this.course = course; }

    public String getPracticeType() { return practiceType; }
    public void setPracticeType(String practiceType) { this.practiceType = practiceType; }

    public String getSupervisorFullName() { return supervisorFullName; }
    public void setSupervisorFullName(String supervisorFullName) { this.supervisorFullName = supervisorFullName; }

    public String getSupervisorPosition() { return supervisorPosition; }
    public void setSupervisorPosition(String supervisorPosition) { this.supervisorPosition = supervisorPosition; }

    public String getSupervisorDegree() { return supervisorDegree; }
    public void setSupervisorDegree(String supervisorDegree) { this.supervisorDegree = supervisorDegree; }

    public String getSupervisorAcademicTitle() { return supervisorAcademicTitle; }
    public void setSupervisorAcademicTitle(String supervisorAcademicTitle) { this.supervisorAcademicTitle = supervisorAcademicTitle; }

    public String getSupervisorJobPlace() { return supervisorJobPlace; }
    public void setSupervisorJobPlace(String supervisorJobPlace) { this.supervisorJobPlace = supervisorJobPlace; }

    public String getPracticeBase() { return practiceBase; }
    public void setPracticeBase(String practiceBase) { this.practiceBase = practiceBase; }

    public String getNsuSupervisorFullName() { return nsuSupervisorFullName; }
    public void setNsuSupervisorFullName(String nsuSupervisorFullName) { this.nsuSupervisorFullName = nsuSupervisorFullName; }

    public String getNsuSupervisorPosition() { return nsuSupervisorPosition; }
    public void setNsuSupervisorPosition(String nsuSupervisorPosition) { this.nsuSupervisorPosition = nsuSupervisorPosition; }

    public String getNsuSupervisorDegree() { return nsuSupervisorDegree; }
    public void setNsuSupervisorDegree(String nsuSupervisorDegree) { this.nsuSupervisorDegree = nsuSupervisorDegree; }

    public String getNsuSupervisorAcademicTitle() { return nsuSupervisorAcademicTitle; }
    public void setNsuSupervisorAcademicTitle(String nsuSupervisorAcademicTitle) { this.nsuSupervisorAcademicTitle = nsuSupervisorAcademicTitle; }

    public String getNsuSupervisorJobPlace() { return nsuSupervisorJobPlace; }
    public void setNsuSupervisorJobPlace(String nsuSupervisorJobPlace) { this.nsuSupervisorJobPlace = nsuSupervisorJobPlace; }

    public String getInstituteSupervisorFullName() { return instituteSupervisorFullName; }
    public void setInstituteSupervisorFullName(String instituteSupervisorFullName) { this.instituteSupervisorFullName = instituteSupervisorFullName; }

    public String getInstituteSupervisorPosition() { return instituteSupervisorPosition; }
    public void setInstituteSupervisorPosition(String instituteSupervisorPosition) { this.instituteSupervisorPosition = instituteSupervisorPosition; }

    public String getInstituteSupervisorDegree() { return instituteSupervisorDegree; }
    public void setInstituteSupervisorDegree(String instituteSupervisorDegree) { this.instituteSupervisorDegree = instituteSupervisorDegree; }

    public String getInstituteSupervisorAcademicTitle() { return instituteSupervisorAcademicTitle; }
    public void setInstituteSupervisorAcademicTitle(String instituteSupervisorAcademicTitle) { this.instituteSupervisorAcademicTitle = instituteSupervisorAcademicTitle; }

    public String getInstituteSupervisorJobPlace() { return instituteSupervisorJobPlace; }
    public void setInstituteSupervisorJobPlace(String instituteSupervisorJobPlace) { this.instituteSupervisorJobPlace = instituteSupervisorJobPlace; }

    public String getPracticeBaseFull() { return practiceBaseFull; }
    public void setPracticeBaseFull(String practiceBaseFull) { this.practiceBaseFull = practiceBaseFull; }

    public String getPracticeBaseName() { return practiceBaseName; }
    public void setPracticeBaseName(String practiceBaseName) { this.practiceBaseName = practiceBaseName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEducationalProgram() { return educationalProgram; }
    public void setEducationalProgram(String educationalProgram) { this.educationalProgram = educationalProgram; }

    public int getSheetNumber() { return sheetNumber; }
    public void setSheetNumber(int sheetNumber) { this.sheetNumber = sheetNumber; }

    public String getFullPlaceOfInternship() { return fullPlaceOfInternship; }
    public void setFullPlaceOfInternship(String fullPlaceOfInternship) { this.fullPlaceOfInternship = fullPlaceOfInternship; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getThesisTopic() { return thesisTopic; }
    public void setThesisTopic(String thesisTopic) { this.thesisTopic = thesisTopic; }

    public String getThesisSupervisorFullName() { return thesisSupervisorFullName; }
    public void setThesisSupervisorFullName(String thesisSupervisorFullName) { this.thesisSupervisorFullName = thesisSupervisorFullName; }

    public String getThesisSupervisorDegree() { return thesisSupervisorDegree; }
    public void setThesisSupervisorDegree(String thesisSupervisorDegree) { this.thesisSupervisorDegree = thesisSupervisorDegree; }

    public String getThesisSupervisorTitle() { return thesisSupervisorTitle; }
    public void setThesisSupervisorTitle(String thesisSupervisorTitle) { this.thesisSupervisorTitle = thesisSupervisorTitle; }

    public String getThesisSupervisorPosition() { return thesisSupervisorPosition; }
    public void setThesisSupervisorPosition(String thesisSupervisorPosition) { this.thesisSupervisorPosition = thesisSupervisorPosition; }

    public String getThesisSupervisorJobPlace() { return thesisSupervisorJobPlace; }
    public void setThesisSupervisorJobPlace(String thesisSupervisorJobPlace) { this.thesisSupervisorJobPlace = thesisSupervisorJobPlace; }

    public String getThesisConsultant() { return thesisConsultant; }
    public void setThesisConsultant(String thesisConsultant) { this.thesisConsultant = thesisConsultant; }

    public String getThesisCoSupervisorFull() { return thesisCoSupervisorFull; }
    public void setThesisCoSupervisorFull(String thesisCoSupervisorFull) { this.thesisCoSupervisorFull = thesisCoSupervisorFull; }

    public String getCourseGroup() { return course + " курс, группа " + group; }

    public String getDegreeLevel() {
        if (course < 3) {
            return "Магистратура";
        } else {
            return "Бакалавриат";
        }
    }

    public String getFullNsuSupervisor() {
        StringBuilder sb = new StringBuilder();
        if (nsuSupervisorFullName != null && !nsuSupervisorFullName.isEmpty()) sb.append(nsuSupervisorFullName);
        if (nsuSupervisorPosition != null && !nsuSupervisorPosition.isEmpty()) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(nsuSupervisorPosition);
        }
        if (nsuSupervisorJobPlace != null && !nsuSupervisorJobPlace.isEmpty()) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(nsuSupervisorJobPlace);
        }
        if (nsuSupervisorDegree != null && !nsuSupervisorDegree.isEmpty()) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(nsuSupervisorDegree);
        }
        if (nsuSupervisorAcademicTitle != null && !nsuSupervisorAcademicTitle.isEmpty()) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(nsuSupervisorAcademicTitle);
        }
        return sb.toString();
    }

    public String getFullInstituteSupervisor() {
        StringBuilder sb = new StringBuilder();
        if (instituteSupervisorFullName != null && !instituteSupervisorFullName.isEmpty()) sb.append(instituteSupervisorFullName);
        if (instituteSupervisorPosition != null && !instituteSupervisorPosition.isEmpty()) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(instituteSupervisorPosition);
        }
        if (instituteSupervisorJobPlace != null && !instituteSupervisorJobPlace.isEmpty()) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(instituteSupervisorJobPlace);
        }
        if (instituteSupervisorDegree != null && !instituteSupervisorDegree.isEmpty()) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(instituteSupervisorDegree);
        }
        if (instituteSupervisorAcademicTitle != null && !instituteSupervisorAcademicTitle.isEmpty()) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(instituteSupervisorAcademicTitle);
        }
        return sb.toString();
    }

    public String getFullThesisSupervisor() {
        StringBuilder sb = new StringBuilder();
        if (thesisSupervisorFullName != null && !thesisSupervisorFullName.isEmpty()) sb.append(thesisSupervisorFullName);
        if (thesisSupervisorDegree != null && !thesisSupervisorDegree.isEmpty()) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(thesisSupervisorDegree);
        }
        if (thesisSupervisorPosition != null && !thesisSupervisorPosition.isEmpty()) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(thesisSupervisorPosition);
        }
        if (thesisSupervisorJobPlace != null && !thesisSupervisorJobPlace.isEmpty()) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(thesisSupervisorJobPlace);
        }
        return sb.toString();
    }
}
