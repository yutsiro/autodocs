package ru.nsu.astakhov.autodocs.model;

import com.github.petrovich4j.Gender;
import lombok.Builder;

@Builder
public record StudentDto(
        Long id,
        String fullName,
        Gender gender,
        Course course,
        String email,
        String phoneNumber,
        EduProgram eduProgram,
        String groupName,
        Specialization specialization,
        String orderOnApprovalTopic,
        String orderOnCorrectionTopic,
        String actualSupervisor,
        String thesisCoSupervisor,
        String thesisConsultant,
        String thesisTopic,
        String reviewer,
        InternshipType internshipType,
        Supervisor thesisSupervisor,
        String fullOrganizationName,
        Supervisor NSUSupervisor,
        Supervisor organizationSupervisor,
        String administrativeActFromOrganization,
        String fullPlaceOfInternship,
        String organizationName,
        String dateOfPracticeAssignment,
        String thesisCoSupervisorDegree,
        String thesisCoSupervisorTitle,
        String thesisCoSupervisorPositionAndJob
) {
}
