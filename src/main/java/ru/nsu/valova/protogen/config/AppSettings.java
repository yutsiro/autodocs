package ru.nsu.valova.protogen.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppSettings {
    private static  final String SETTINGS_FILE = "src/main/resources/settings.json";

    private String lastProtocolNumber;
    private String chairmanFullName;
    private String secretaryFullName;
    private String protocolOutputPath;
    private String defaultTemplatePath;

    private ObjectMapper objectMapper;
    private List<String> defaultAttendees;

    public AppSettings() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        loadSettings();
    }

    public void loadSettings() {
        File file = new File(SETTINGS_FILE);

        if(!file.exists()) {
            setDefaultSettings();
            saveSettings();
            System.out.println("Создан файл конфигурации с настройками по умолчанию: " + SETTINGS_FILE);
            return;
        }

        try {
            Map<String, Object> settingsMap = objectMapper.readValue(file,
                    new TypeReference<Map<String, Object>>() {});

            if (settingsMap.containsKey("lastProtocolNumber")) {
                lastProtocolNumber = (String) settingsMap.get("lastProtocolNumber");
            }

            if (settingsMap.containsKey("chairmanFullName")) {
                chairmanFullName = (String) settingsMap.get("chairmanFullName");
            }

            if (settingsMap.containsKey("secretaryFullName")) {
                secretaryFullName = (String) settingsMap.get("secretaryFullName");
            }

            if (settingsMap.containsKey("protocolOutputPath")) {
                protocolOutputPath = (String) settingsMap.get("protocolOutputPath");
            }

            if (settingsMap.containsKey("defaultTemplatePath")) {
                defaultTemplatePath = (String) settingsMap.get("defaultTemplatePath");
            }

            if (settingsMap.containsKey("defaultAttendees")) {
                defaultAttendees = (List<String>) settingsMap.get("defaultAttendees");
            }

            System.out.println("Настройки успешно загружены из файла: " + SETTINGS_FILE);
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке настроек: " + e.getMessage());
            setDefaultSettings();
        }
    }

    public void saveSettings() {
        try {
            Map<String, Object> settingsMap = new HashMap<>();
            settingsMap.put("lastProtocolNumber", lastProtocolNumber);
            settingsMap.put("chairmanFullName", chairmanFullName);
            settingsMap.put("secretaryFullName", secretaryFullName);
            settingsMap.put("protocolOutputPath", protocolOutputPath);
            settingsMap.put("defaultTemplatePath", defaultTemplatePath);
            settingsMap.put("defaultAttendees", defaultAttendees);

            objectMapper.writeValue(new File(SETTINGS_FILE), settingsMap);
            System.out.println("Настройки сохранены в файл: " + SETTINGS_FILE);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении настроек: " + e.getMessage());
        }
    }

    private void setDefaultSettings() {
        String year = String.valueOf(LocalDate.now().getYear());
        this.lastProtocolNumber = "1-" + year.substring((2));

        this.chairmanFullName = "заведующий кафедрой д.ф.-м.н. Пальчунов Дмитрий Евгеньевич";
        this.secretaryFullName = "старший преподаватель Артемьева Анастасия Алексеевна";
        this.protocolOutputPath = "testdata/generatedprotocols";
        this.defaultTemplatePath = "testdata/practice_protocol_template.docx";
        this.defaultAttendees = List.of(
                "Пальчунов Д. Е.",
                "Артемьева А.А.",
                "Яхъяева Г. Э.",
                "Савостьянов А. Н.",
                "Найданов Ч. А.",
                "Хазанкин Г. Р.",
                "Трегубов А. С."
        );
    }

    public String getLastProtocolNumber() { return lastProtocolNumber; }
    public void setLastProtocolNumber(String lastProtocolNumber) {
        this.lastProtocolNumber = lastProtocolNumber;
        saveSettings();
    }

    public List<String> getDefaultAttendees() { return defaultAttendees; }
    public void setDefaultAttendees(List<String> defaultAttendees) {
        this.defaultAttendees = defaultAttendees;
        saveSettings();
    }

    public void setLastProtocolNumberIfValid(String protocolNumber) {
        if (isValidProtocolNumberFormat(protocolNumber)) {
            this.lastProtocolNumber = protocolNumber;
            saveSettings();
            System.out.println("Номер протокола сохранён: " + protocolNumber);
        } else {
            System.out.println("Номер протокола не соответствует формату ХХ-ГГ, пропускаем сохранение: " + protocolNumber);
        }
    }

    public void incrementProtocolNumber() {
        String[] parts = lastProtocolNumber.split("-");
        int currentYear = Integer.parseInt(parts[1]);
        int currentNumber = Integer.parseInt(parts[0]);

        int actualYear = java.time.Year.now().getValue() % 100;

        if (currentYear == actualYear) {
            currentNumber++;
        } else {
            currentYear = actualYear;
            currentNumber = 1;
        }

        this.lastProtocolNumber = currentNumber + "-" + currentYear;
        saveSettings();
    }

    public boolean isValidProtocolNumberFormat(String protocolNumber) {
        if (protocolNumber == null || protocolNumber.isEmpty()) {
            return false;
        }

        if (!protocolNumber.matches("\\d+-\\d{2}")) {
            return false;
        }

        String[] parts = protocolNumber.split("-");
        int yearFromNumber = Integer.parseInt(parts[1]);
        int currentYear = java.time.Year.now().getValue() % 100;

        return yearFromNumber == currentYear;
    }

    public String getChairmanFullName() { return chairmanFullName; }
    public void setChairmanFullName(String chairmanFullName) {
        this.chairmanFullName = chairmanFullName;
        saveSettings();
    }

    public String getSecretaryFullName() { return secretaryFullName; }
    public void setSecretaryFullName(String secretaryFullName) {
        this.secretaryFullName = secretaryFullName;
        saveSettings();
    }

    public String getProtocolOutputPath() { return protocolOutputPath; }
    public void setProtocolOutputPath(String protocolsOutputPath) {
        this.protocolOutputPath = protocolsOutputPath;
        saveSettings();
    }

    public String getDefaultTemplatePath() { return defaultTemplatePath; }
    public void setDefaultTemplatePath(String defaultTemplatePath) {
        this.defaultTemplatePath = defaultTemplatePath;
        saveSettings();
    }

    public String getConfigFilePath() { return SETTINGS_FILE; }
}
