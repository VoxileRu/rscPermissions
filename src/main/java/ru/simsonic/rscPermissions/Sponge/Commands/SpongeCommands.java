package ru.simsonic.rscPermissions.Sponge.Commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class SpongeCommands implements CommandExecutor
{
	private final CommandSpec commandHub = CommandSpec.builder()
		.description(Text.of("Hello World Command"))
		.permission("myplugin.command.helloworld")
		.executor(this)
		.build();
	public SpongeCommands()
	{
		// game.getCommandDispatcher().register(plugin, commandHub, "helloworld", "hello", "test");
	}
	@Override
	public CommandResult execute(CommandSource src, CommandContext context) throws CommandException
	{
		src.sendMessage(Text.of("Hello rscp's World!"));
		return CommandResult.success();
	}
}
