package org.teacon.xkdeco.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;

/**
 * Type-level default that treats fields, methods (return values) and parameters as {@link Nonnull}
 * unless explicitly annotated otherwise.
 *
 * <p>In-house replacement for {@code snownee.kiwi.util.NotNullByDefault}, which was removed in
 * Kiwi 26.x. See {@code MIGRATION-26.1.md} (task C1).
 */
@Documented
@Nonnull
@TypeQualifierDefault({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NotNullByDefault {
}
