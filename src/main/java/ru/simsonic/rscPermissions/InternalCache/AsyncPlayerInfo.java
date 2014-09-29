package ru.simsonic.rscPermissions.InternalCache;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.simsonic.rscPermissions.DataTypes.RowInheritance;
import ru.simsonic.rscPermissions.DataTypes.RowPermission;
import ru.simsonic.rscPermissions.InternalCache.LocalCacheTree.ResolutionLeaf;

public class AsyncPlayerInfo
{
	public Player player;
	public String name;
	public UUID uuid;
	public int expirience;
	public Location location;
	public Set<String> regions;
	public AsyncPlayerInfo()
	{
	}
	public AsyncPlayerInfo(String playerName)
	{
		this.name = playerName;
	}
	public AsyncPlayerInfo(UUID playerUniqueId)
	{
		this.uuid = playerUniqueId;
	}
	public AsyncPlayerInfo(Player player, Set<String> regions)
	{
		if(player != null)
		{
			this.player = player;
			try
			{
				// minecraft <= 1.7
				this.name = player.getName();
			} catch(RuntimeException | NoSuchMethodError ex) {
				// minecraft >= 1.8
			}
			try
			{
				// minecraft >= 1.8
				this.uuid = player.getUniqueId();
			} catch(RuntimeException | NoSuchMethodError ex) {
				// minecraft <= 1.7
			}
			this.expirience = player.getLevel();
			this.location = player.getLocation();
			this.regions = regions;
		}
	}
	public boolean isPlayerEntityApplicable(String entity)
	{
		// Test by UUID (minecraft >= 1.8)
		try
		{
			if(this.uuid.compareTo(UUID.fromString(entity)) == 0)
				return true;
		} catch(RuntimeException ex) {
			// Server doesn't support this yet
		}
		// Test by name (minecraft <= 1.7)
		try
		{
			if(this.name.equalsIgnoreCase(entity))
				return true;
		} catch(RuntimeException ex) {
			// Server already doesn't support this
		}
		return false;
	}
	public boolean isPlayerPermissionApplicable(RowPermission row)
	{
		if(isPlayerEntityApplicable(row.entity) || "".equals(row.entity))
			return (row.destination.isLocationApplicable(location, regions, null) && row.expirience <= expirience);
		return false;
	}
	public boolean isGroupPermissionApplicable(RowPermission row, ResolutionLeaf leaf)
	{
		if(row.entity.equalsIgnoreCase(leaf.group) || "".equals(row.entity))
			return (row.destination.isLocationApplicable(location, regions, leaf.instance) && row.expirience <= expirience);
		return false;
	}
	public boolean isPlayerInheritanceApplicable(RowInheritance row)
	{
		if(isPlayerEntityApplicable(row.entity))
			return (row.destination.isLocationApplicable(location, regions, row.instance) && row.expirience <= expirience);
		return false;
	}
	public boolean isGroupInheritanceApplicable(RowInheritance row, ResolutionLeaf leaf)
	{
		if(row.entity.equalsIgnoreCase(leaf.group))
			return (row.destination.isLocationApplicable(location, regions, leaf.instance) && row.expirience <= expirience);
		return false;
	}
}