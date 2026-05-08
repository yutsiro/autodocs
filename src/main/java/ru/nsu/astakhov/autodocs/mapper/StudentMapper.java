package ru.nsu.astakhov.autodocs.mapper;

import ru.nsu.astakhov.autodocs.model.StudentEntity;
import ru.nsu.astakhov.autodocs.model.StudentDto;

import java.util.List;

public final class StudentMapper {
    private StudentMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static List<StudentDto> listToDto(List<StudentEntity> entities) {
        return entities.stream()
                .map(StudentMapper::toDto)
                .toList();
    }

    public static StudentDto toDto(StudentEntity entity) {
        return StudentDto.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .gender(entity.getGender())
                .course(entity.getCourse())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .eduProgram(entity.getEduProgram())
                .groupName(entity.getGroupName())
                .specialization(entity.getSpecialization())
                .orderOnApprovalTopic(entity.getOrderOnApprovalTopic())
                .orderOnCorrectionTopic(entity.getOrderOnCorrectionTopic())
                .actualSupervisor(entity.getActualSupervisor())
                .thesisCoSupervisor(entity.getThesisCoSupervisor())
                .thesisConsultant(entity.getThesisConsultant())
                .thesisTopic(entity.getThesisTopic())
                .reviewer(entity.getReviewer())
                .internshipType(entity.getInternshipType())
                .thesisSupervisor(entity.getThesisSupervisor())
                .fullOrganizationName(entity.getFullOrganizationName())
                .NSUSupervisor(entity.getNSUSupervisor())
                .organizationSupervisor(entity.getOrganizationSupervisor())
                .administrativeActFromOrganization(entity.getAdministrativeActFromOrganization())
                .fullPlaceOfInternship(entity.getFullPlaceOfInternship())
                .organizationName(entity.getOrganizationName())
                .dateOfPracticeAssignment(entity.getDateOfPracticeAssignment())
                .thesisCoSupervisorDegree(entity.getThesisCoSupervisorDegree())
                .thesisCoSupervisorTitle(entity.getThesisCoSupervisorTitle())
                .thesisCoSupervisorPositionAndJob(entity.getThesisCoSupervisorPositionAndJob())
                .build();
    }

    public static List<StudentEntity> listToEntity(List<StudentDto> dtos) {
        return dtos.stream()
                .map(StudentMapper::toEntity)
                .toList();
    }

    public static StudentEntity toEntity(StudentDto dto) {
        return StudentEntity.builder()
                .id(dto.id())
                .fullName(dto.fullName())
                .gender(dto.gender())
                .course(dto.course())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
                .eduProgram(dto.eduProgram())
                .groupName(dto.groupName())
                .specialization(dto.specialization())
                .orderOnApprovalTopic(dto.orderOnApprovalTopic())
                .orderOnCorrectionTopic(dto.orderOnCorrectionTopic())
                .actualSupervisor(dto.actualSupervisor())
                .thesisCoSupervisor(dto.thesisCoSupervisor())
                .thesisConsultant(dto.thesisConsultant())
                .thesisTopic(dto.thesisTopic())
                .reviewer(dto.reviewer())
                .internshipType(dto.internshipType())
                .thesisSupervisor(dto.thesisSupervisor())
                .fullOrganizationName(dto.fullOrganizationName())
                .NSUSupervisor(dto.NSUSupervisor())
                .organizationSupervisor(dto.organizationSupervisor())
                .administrativeActFromOrganization(dto.administrativeActFromOrganization())
                .fullPlaceOfInternship(dto.fullPlaceOfInternship())
                .organizationName(dto.organizationName())
                .dateOfPracticeAssignment(dto.dateOfPracticeAssignment())
                .thesisCoSupervisorDegree(dto.thesisCoSupervisorDegree())
                .thesisCoSupervisorTitle(dto.thesisCoSupervisorTitle())
                .thesisCoSupervisorPositionAndJob(dto.thesisCoSupervisorPositionAndJob())
                .build();
    }
}
