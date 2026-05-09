package ru.nsu.valova.protogen.model;

import ru.nsu.valova.protogen.handlers.QuestionHandler;
import java.util.ArrayList;
import java.util.List;

public class ProtocolData {
    private String protocolNumber;
    private String day;
    private String month;
    private String year;
    private String chairman;
    private String secretary;
    private List<QuestionHandler> selectedQuestions = new ArrayList<>();
    private List<String> attendees;

    public String getProtocolNumber() { return protocolNumber; }
    public void setProtocolNumber(String protocolNumber) { this.protocolNumber = protocolNumber; }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getChairman() { return chairman; }
    public void setChairman(String chairman) { this.chairman = chairman; }

    public String getSecretary() { return secretary; }
    public void setSecretary(String secretary) { this.secretary = secretary; }

    public List<QuestionHandler> getSelectedQuestions() { return selectedQuestions; }
    public void setSelectedQuestions(List<QuestionHandler> selectedQuestions) {
        this.selectedQuestions = selectedQuestions;
    }

    public List<String> getAttendees() { return attendees; }
    public void setAttendees(List<String> attendees) { this.attendees = attendees; }

    public void addQuestion(QuestionHandler question) {
        this.selectedQuestions.add(question);
    }

    public List<String> getAllAgendaItems() {
        List<String> allItems = new ArrayList<>();
        for (QuestionHandler q : selectedQuestions) {
            allItems.add(q.getAgendaItemText());
        }
        return allItems;
    }

    public List<String> getAllConsideredItems() {
        List<String> allItems = new ArrayList<>();
        for (QuestionHandler q : selectedQuestions) {
            allItems.addAll(q.getConsideredItems());
        }
        return allItems;
    }

    public List<String> getAllDecisionTexts() {
        List<String> allDecisions = new ArrayList<>();
        for (QuestionHandler q : selectedQuestions) {
            allDecisions.add(q.getDecisionText());
        }
        return allDecisions;
    }

    public List<Student> getAllStudents() {
        List<Student> allStudents = new ArrayList<>();
        for (QuestionHandler q : selectedQuestions) {
            if (q.getStudents() != null) {
                allStudents.addAll(q.getStudents());
            }
        }
        return allStudents;
    }
}
