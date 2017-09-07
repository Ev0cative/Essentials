package io.complete.api;

import com.google.common.collect.Sets;
import java.util.Set;

public abstract class Module {
    protected final Set<CommandClass> commands = Sets.newHashSet();
    protected boolean enabled = true;

    Set<CommandClass> getCommands() {
        return this.commands;
    }

    void unregisterCommand(CommandClass command) {
        this.commands.remove(command);
    }

    void unregisterCommands() {
        this.commands.clear();
    }

    boolean isEnabled() {
        return this.enabled;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

