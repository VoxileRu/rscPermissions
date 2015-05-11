package ru.simsonic.rscPermissions.Sponge.Commands;

import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;

public class SpongeCommands implements CommandExecutor
{
	private final CommandSpec commandHub = CommandSpec.builder()
		.setDescription(Texts.of("Hello World Command"))
		.setPermission("myplugin.command.helloworld")
		.setExecutor(this)
		.build();
	public SpongeCommands()
	{
		// game.getCommandDispatcher().register(plugin, commandHub, "helloworld", "hello", "test");
	}
	@Override
	public CommandResult execute(CommandSource src, CommandContext context) throws CommandException
	{
		src.sendMessage(Texts.of("Hello rscp's World!"));
		return CommandResult.success();
	}
}
