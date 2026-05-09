package ru.nsu.astakhov.autodocs.document;

import com.github.petrovich4j.Case;
import com.github.petrovich4j.Gender;
import com.github.petrovich4j.NameType;
import com.github.petrovich4j.Petrovich;

public class RussianWordDecliner {
    private final Petrovich petrovich;

    public RussianWordDecliner() {
        petrovich = new Petrovich();
    }

    public Gender getGenderByPatronymic(String patronymic) {
        return petrovich.gender(patronymic, Gender.Both);
    }

    public String getFormalStudentByGender(Gender gender) {
        final String maleStudent = "Обучающийся";
        final String femaleStudent = "Обучающаяся";

        return gender == Gender.Female ? femaleStudent : maleStudent;
    }

    public String getGenitiveFormalStudentByGender(Gender gender) {
        final String maleGenitiveStudent = "Обучающегося";
        final String femaleGenitiveStudent = "Обучающейся";

        return gender == Gender.Female ? femaleGenitiveStudent : maleGenitiveStudent;
    }

    public String getCommonStudentByGender(Gender gender) {
        final String maleStudent = "Студент";
        final String femaleStudent = "Студентка";

        return gender == Gender.Female ? femaleStudent : maleStudent;
    }

    public String getDativeStudentByGender(Gender gender) {
        final String maleDativeStudent = "Студенту";
        final String femaleDativeStudent = "Студентке";

        return gender == Gender.Female ? femaleDativeStudent : maleDativeStudent;
    }

    public String getInstrumentalCommonStudentByGender(Gender gender) {
        final String maleInstrumentalStudent = "студентом";
        final String femaleInstrumentalStudent = "студенткой";

        return gender == Gender.Female ? femaleInstrumentalStudent : maleInstrumentalStudent;
    }

    public String getFullNameInGenitiveCase(String fullName, Gender gender) {
        Case correctCase = Case.Genitive;
        return getFullNameInCorrectCase(fullName, gender, correctCase);
    }

    public String getFullNameInDativeCase(String fullName, Gender gender) {
        Case correctCase = Case.Dative;
        return getFullNameInCorrectCase(fullName, gender, correctCase);
    }

    public String getFullNameInInstrumentalCase(String fullName, Gender gender) {
        Case correctCase = Case.Instrumental;
        return getFullNameInCorrectCase(fullName, gender, correctCase);
    }

    // И. О. Фамилия
    public String getAbbreviatedName(String fullName) {
        String[] nameParts = fullName.split(" ");

        if (nameParts.length != 3) {
            return fullName;
        }
        char name = nameParts[1].charAt(0);
        char patronymic = nameParts[2].charAt(0);
        String surname = nameParts[0];

        return name + ". " + patronymic + ". " + surname;
    }

    // Фамилия И. О.
    public String getAbbreviatedName2(String fullName) {
        String[] nameParts = fullName.split(" ");

        if (nameParts.length != 3) {
            return fullName;
        }
        char name = nameParts[1].charAt(0);
        char patronymic = nameParts[2].charAt(0);
        String surname = nameParts[0];

        return surname + " " + name + ". " + patronymic + ".";
    }

    private String getFullNameInCorrectCase(String fullName, Gender gender, Case correctCase) {
        String[] nameParts = fullName.split(" ");

        if (nameParts.length == 3) {
            String surname = petrovich.say(nameParts[0], NameType.LastName, gender, correctCase);
            String name = petrovich.say(nameParts[1], NameType.FirstName, gender, correctCase);
            String patronymic = petrovich.say(nameParts[2], NameType.PatronymicName, gender, correctCase);

            return surname + ' ' + name + ' ' + patronymic;
        }
        else if (nameParts.length == 2) {
            String surname = petrovich.say(nameParts[0], NameType.LastName, gender, correctCase);
            String name = petrovich.say(nameParts[1], NameType.FirstName, gender, correctCase);

            return surname + ' ' + name;
        }
        else if (nameParts.length == 1) {
            return petrovich.say(nameParts[0], NameType.LastName, gender, correctCase);
        }
        else {
            return "";
        }
    }
}
