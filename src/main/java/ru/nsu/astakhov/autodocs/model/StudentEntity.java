package ru.nsu.astakhov.autodocs.model;

import com.github.petrovich4j.Gender;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.nsu.astakhov.autodocs.model.converters.CourseConverter;
import ru.nsu.astakhov.autodocs.model.converters.EduProgramConverter;
import ru.nsu.astakhov.autodocs.model.converters.GenderConverter;
import ru.nsu.astakhov.autodocs.model.converters.InternshipTypeConverter;
import ru.nsu.astakhov.autodocs.model.converters.SpecializationConverter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "students")
@Builder
public class StudentEntity {
    // TODO: поменять порядок полей
    // TODO: привязать либо к таблице, либо отдельно отсортировать по логике
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName; // фио

    @Convert(converter = GenderConverter.class)
    private Gender gender; // пол

    @Convert(converter = CourseConverter.class)
    @Column(nullable = false)
    private Course course; // курс

    private String email; // почта

    private String phoneNumber; // телефон

    @Convert(converter = EduProgramConverter.class)
    private EduProgram eduProgram; // образовательная программа

//    @Column(nullable = false)
    private String  groupName; // группа

    @Convert(converter = SpecializationConverter.class)
//    @Column(nullable = false)
    private Specialization specialization;  // профиль обучения

    private String orderOnApprovalTopic; // распоряжение об утверждении темы

    private String orderOnCorrectionTopic; // распоряжение о корректировке темы

    private String actualSupervisor; // с кем по факту работает

    private String thesisCoSupervisor; // соруководитель вкр

    private String thesisConsultant; // консультант вкр

    private String thesisTopic; // тема вкр

    private String reviewer; // рецензент

    @Convert(converter = InternshipTypeConverter.class)
    private InternshipType internshipType; // вид практики

    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "thesis_supervisor_name"))
    @AttributeOverride(name = "job", column = @Column(name = "thesis_supervisor_job"))
    @AttributeOverride(name = "position", column = @Column(name = "thesis_supervisor_position"))
    @AttributeOverride(name = "degree", column = @Column(name = "thesis_supervisor_degree"))
    @AttributeOverride(name = "title", column = @Column(name = "thesis_supervisor_title"))
    private Supervisor thesisSupervisor; // руководитель вкр

    private String fullOrganizationName; // Название организации, структурного подразделения номер кабинета

    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "nsu_supervisor_name"))
    @AttributeOverride(name = "job", column = @Column(name = "nsu_supervisor_job"))
    @AttributeOverride(name = "position", column = @Column(name = "nsu_supervisor_position"))
    @AttributeOverride(name = "degree", column = @Column(name = "nsu_supervisor_degree"))
    @AttributeOverride(name = "title", column = @Column(name = "nsu_supervisor_title"))
    private Supervisor NSUSupervisor; // руководитель вкр от нгу

    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "organization_supervisor_name"))
    @AttributeOverride(name = "job", column = @Column(name = "organization_supervisor_job"))
    @AttributeOverride(name = "position", column = @Column(name = "organization_supervisor_position"))
    @AttributeOverride(name = "degree", column = @Column(name = "organization_supervisor_degree"))
    @AttributeOverride(name = "title", column = @Column(name = "organization_supervisor_title"))
    private Supervisor organizationSupervisor; // руководитель вкр от организации

    private String administrativeActFromOrganization; // распорядительный акт от организации

    private String fullPlaceOfInternship; // Место практики полностью

    private String organizationName; // Наименование организации

    private String dateOfPracticeAssignment; // Дата выдачи задания практик

    private String thesisCoSupervisorDegree; // Ученая степень соруководителя ВКР

    private String thesisCoSupervisorTitle; // Звание соруководителя ВКР

    private String thesisCoSupervisorPositionAndJob; // Должность соруководителя ВКР, место работы

    public void setEduProgram(String value) {
        EduProgram newEduProgram = EduProgram.fromValue(value);
        if (newEduProgram != null) {
            this.eduProgram = newEduProgram;
        }
    }

    public void setSpecialization(String value) {
        Specialization newSpecialization = Specialization.fromValue(value);
        if (newSpecialization != null) {
            this.specialization = newSpecialization;
        }
    }

    public void setGender(String value) {
        if (Gender.Male.name().equals(value)) {
            this.gender = Gender.Male;
        }
        else if (Gender.Female.name().equals(value)) {
            this.gender = Gender.Female;
        }
    }
}