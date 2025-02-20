package me.StevenLawson.TotalFreedomMod.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandPermissions
{
    AdminLevel level();

    SourceType source();

    boolean blockHostConsole() default false;
}
