package org.example.spigotframework.logic.utils;

import lombok.Getter;

@Getter
public enum Language {
    DEFAULT("Please choose a language message. The current message is a default message."),
    NO_PERMISSION("&cYou do not have permissions to perform this action. Please contact your system administrator for assistance.");

    private final String message;

    Language(String message) {
        this.message = message;
    }

    /**
     * Returns the ordinal of this enum constant.
     *
     * @return Returns the ordinal of this enum constant as an integer.
     */
    public int getId() {
        return ordinal();
    }

    /**
     * Returns the message string after applying colorization.
     *
     * @return Returns a colorized version of the message string using ChatColor codes.
     */
    public String getMessageWithColor() {
        return ColorUtil.colorize(message);
    }

    /**
     * Gets the Language enum constant corresponding to the given name. If the name doesn't match any, returns the DEFAULT language.
     *
     * @param name The string representing the name of the Language enum constant.
     * @return Returns the Language enum constant corresponding to the given name, or Language.DEFAULT if no match is found.
     */
    public Language getLanguage(String name) {
        Language language = null;
        for (Language obj : Language.values()) {
            if (obj.name().equalsIgnoreCase(name)) {
                language = obj;
                break;
            }
        }
        if (language == null) language = Language.DEFAULT;
        return language;
    }
}
