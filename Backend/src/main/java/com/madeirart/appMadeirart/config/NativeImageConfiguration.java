package com.madeirart.appMadeirart.config;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * Configuração de hints para GraalVM Native Image
 */
@Configuration
@ImportRuntimeHints(NativeImageConfiguration.NativeHintsRegistrar.class)
public class NativeImageConfiguration {

    static class NativeHintsRegistrar implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            // Registrar recursos necessários
            hints.resources()
                .registerPattern("application.properties")
                .registerPattern("application-*.properties")
                .registerPattern("db/migration/*")
                .registerPattern("META-INF/*");

            // Registrar driver SQLite para reflection
            hints.reflection()
                .registerType(org.sqlite.JDBC.class, MemberCategory.values())
                .registerType(org.sqlite.SQLiteConnection.class, MemberCategory.values())
                .registerType(org.sqlite.jdbc4.JDBC4Connection.class, MemberCategory.values());

            // Registrar dialeto do Hibernate
            try {
                Class<?> dialectClass = Class.forName("org.hibernate.community.dialect.SQLiteDialect");
                hints.reflection().registerType(dialectClass, MemberCategory.values());
            } catch (ClassNotFoundException e) {
                // Ignorar se não encontrar
            }
        }
    }
}
