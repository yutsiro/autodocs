package ru.nsu.astakhov.autodocs.ui.configs;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.nsu.astakhov.autodocs.util.Ini;

import java.awt.Color;


@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigManager {
    private static Ini ini = null;

    public static void setIni(Ini ini) {
        if (ConfigManager.ini != null) {
            throw new IllegalStateException("Ini already initialized");
        }
        ConfigManager.ini = ini;
    }

    public static boolean isLightTheme() {
        String theme = getSetting(ConfigConstants.THEME);
        return theme.equals(PrivateConstants.LIGHT_THEME.getValue());
    }

    public static String getSetting(ConfigConstants configConstants) {
        if (configConstants.getSection().equals(PrivateConstants.TAG_COLOR.getValue())) {
            return getColor(configConstants);
        }
        return ini.getValue(configConstants.getSection(), configConstants.getSettingName());
    }

    public static Color parseHexColor(String hexColor) {
        if (hexColor == null || hexColor.isBlank()) {
            return Color.BLACK;
        }
        try {
            String typedHexColor;
            if (hexColor.startsWith("0x")) {
                typedHexColor = hexColor.substring(2);
            }
            else if (hexColor.startsWith("#")) {
                typedHexColor = hexColor.substring(1);
            }
            else {
                typedHexColor = hexColor;
            }
            int hexSystem = 16;
            int rgb = Integer.parseInt(typedHexColor, hexSystem);
            return new Color(rgb);
        }
        catch (NumberFormatException e) {
            logger.error("Некорректный формат цвета: {}", hexColor);
            return Color.BLACK;
        }
    }

    private static String getColor(ConfigConstants configConstants) {
        String theme = ConfigManager.getSetting(ConfigConstants.THEME);
        String section;

        if (theme.equals(PrivateConstants.DARK_THEME.getValue())) {
            section = PrivateConstants.DARK_COLORS_SECTION.getValue();
        }
        else if (theme.equals(PrivateConstants.LIGHT_THEME.getValue())) {
            section = PrivateConstants.LIGHT_COLORS_SECTION.getValue();
        }
        else if (theme.equals(PrivateConstants.PINK_THEME.getValue())) {
            section = PrivateConstants.PINK_COLORS_SECTION.getValue();
        }
        else {
            throw new IllegalStateException("Invalid theme");
        }

        return ini.getValue(section, configConstants.getSettingName());
    }
}
