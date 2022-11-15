package com.gmail.davideblade99.clashofminecrafters.setting.section;

import com.gmail.davideblade99.clashofminecrafters.yaml.CoMYamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;

/**
 * Class representing a generic section in the config.yml. More specifically, this class represents a specific
 * {@link ConfigurationSection}.
 *
 * @since v3.1.1
 */
abstract class SectionConfiguration {

    /** Part of the config.yml containing the settings of a specific section */
    final ConfigurationSection section;

    /**
     * Finds the {@link ConfigurationSection} with the specified path within the {@code configuration} passed as a
     * parameter
     *
     * @param configuration {@link CoMYamlConfiguration} containing the section
     * @param sectionPath   Path of the section in the config.yml
     */
    public SectionConfiguration(@Nonnull final CoMYamlConfiguration configuration, @Nonnull final String sectionPath) {
        this.section = configuration.getConfigurationSection(sectionPath);
    }
}
