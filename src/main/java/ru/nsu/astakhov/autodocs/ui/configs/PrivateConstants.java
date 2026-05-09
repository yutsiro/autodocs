package ru.nsu.astakhov.autodocs.ui.configs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
enum PrivateConstants {
    TAG_COLOR("TAG_COLOR"),
    DARK_COLORS_SECTION("Dark_colors"),
    LIGHT_COLORS_SECTION("Light_colors"),
    PINK_COLORS_SECTION("Pink_colors"),
    DARK_THEME("dark"),
    LIGHT_THEME("light"),
    PINK_THEME("pink");

    private final String value;
}
