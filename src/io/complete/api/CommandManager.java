package io.complete.api;

public interface CommandManager {
    public boolean containsCommand(CommandClass var1);

    public void registerAll(Module var1);

    public void registerCommand(CommandClass var1);

    public void registerCommands(CommandClass[] var1);

    public void unregisterCommand(CommandClass var1);

    public CommandClass getCommand(String var1);
}