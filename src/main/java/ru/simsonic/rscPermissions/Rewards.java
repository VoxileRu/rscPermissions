package ru.simsonic.rscPermissions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import ru.simsonic.rscPermissions.DataTypes.RowReward;
import ru.simsonic.utilities.TimeIntervalParser;

public class Rewards
{
	private static final String strCommandExecutorPlayer = "player:";
	private static final String strCommandExecutorConsole = "console:";
	private final MainPluginClass plugin;
	public Rewards(MainPluginClass rscp)
	{
		this.plugin = rscp;
	}
	protected final HashMap<String, ArrayList<RowReward>> rewards = new HashMap<>();
	public synchronized int ImportRewards(RowReward[] rows)
	{
		rewards.clear();
		if(rows == null)
			return 0;
		int total_rewards = 0;
		for(RowReward row : rows)
		{
			ArrayList<RowReward> userRewards = rewards.get(row.user.toLowerCase());
			if(userRewards == null)
			{
				userRewards = new ArrayList<>();
				rewards.put(row.user.toLowerCase(), userRewards);
			}
			userRewards.add(row);
			total_rewards += 1;
		}
		return total_rewards;
	}
	public synchronized HashMap<String, Integer> getAvailableRewards(String user)
	{
		final HashMap<String, Integer> result = new HashMap<>();
		ArrayList<RowReward> user_rewards = rewards.get(user.toLowerCase());
		if(user_rewards != null)
			for(RowReward reward : user_rewards)
				result.put(reward.code, result.get(reward.code) + 1);
		return result;
	}
	public synchronized RowReward getRewardDetails(String user, String code)
	{
		if((user == null) || (code == null))
			return null;
		if("".equals(user) || "".equals(code))
			return null;
		ArrayList<RowReward> user_rewards = rewards.get(user.toLowerCase());
		for(RowReward row : user_rewards)
			if(row.code.equalsIgnoreCase(code))
				return row;
		return null;
	}
	public void executeReward(final Player player, String reward)
	{
		if(plugin.settings.isRewardsEnabled() == false)
		{
			plugin.Message(player, "Rewards support has been disabled by administrator.");
			return;
		}
		final HashMap<String, Integer> reward_list = getAvailableRewards(player.getName());
		if(reward == null)
		{
			if(reward_list.isEmpty() == false)
			{
				String text = "";
				for(String code : reward_list.keySet())
				{
					Integer count = reward_list.get(code);
					text += ((count > 1) ? code + " (" + count.toString() + "), " : code + ", ");
				}
				plugin.Message(player, "Available rewards: {_R}" + text.substring(0, text.length() - 2) + ".");
			}
			else
				plugin.Message(player, "No rewards available.");
			return;
		}
		for(String code : reward_list.keySet())
			if(code.equalsIgnoreCase(reward))
				reward = code;
		final String correctFinalReward = reward;
		Integer count = reward_list.get(reward);
		if(count != null)
		{
			Thread rewardExecutor = new Thread()
			{
				@Override
				public void run()
				{
					Thread.currentThread().setName("rscp:Rewarder");
					Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
					try
					{
						plugin.connectionList.threadFetchTablesData().join();
						final RowReward details = getRewardDetails(player.getName(), correctFinalReward);
						threadApplyReward(player, details);
						plugin.connectionList.threadFetchTablesData().join();
					} catch(InterruptedException ex) {
					}
					plugin.Message(player, "You have received reward \"" + correctFinalReward + "\"!");
				}
			};
			rewardExecutor.start();
		}
		else
			plugin.Message(player, "No such reward available for you.");
	}
	public void threadApplyReward(final Player player, final RowReward reward)
	{
		if((reward == null) || (player == null))
			return;
		if(reward.add_group != null)
		{
			if(reward.add_group_destination != null)
			{
			}
			if(reward.add_group_expirience != 0)
			{
			}
			if(reward.add_group_interval != null)
			{
				int seconds = TimeIntervalParser.parseTimeInterval(reward.add_group_interval);
			}
		}
		if(reward.execute_commands != null)
		{
			final String[] commands = reward.execute_commands.split("[\r\n]+");
			plugin.getServer().getScheduler().runTask(plugin, new Runnable()
			{
				@Override
				public void run()
				{
					PermissionAttachment temporary = player.addAttachment(plugin);
					if(reward.command_permissions != null)
					{
						String[] permissions = reward.command_permissions.split("[\r\n]+");
						for(String permission : permissions)
						{
							MainPluginClass.consoleLog.log(Level.INFO, "[rscp] + temp perm: \"{0}\"", permission);
							temporary.setPermission(permission, true);
						}
					}
					player.recalculatePermissions();
					for(String command : commands)
					{
						boolean bConsole = false;
						if(command.toLowerCase().startsWith(strCommandExecutorConsole))
						{
							command = command.substring(strCommandExecutorConsole.length());
							bConsole = true;
						}
						if(command.toLowerCase().startsWith(strCommandExecutorPlayer))
						{
							command = command.substring(strCommandExecutorPlayer.length());
							bConsole = false;
						}
						MainPluginClass.consoleLog.log(Level.INFO,
							"[rscp] Reward \"{0}\" for user \"{1}\" executes command:\n{2} /{3}", new Object[]
							{
								reward.code,
								player.getName(),
								bConsole ? strCommandExecutorConsole : strCommandExecutorPlayer,
								command,
							});
						plugin.getServer().dispatchCommand(bConsole ? plugin.getServer().getConsoleSender() : player, command);
						player.sendMessage("You've received reward \"" + reward.code + "\".");
					}
					player.removeAttachment(temporary);
					player.recalculatePermissions();
				}
			});
		}
	}
}