package ru.simsonic.rscPermissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import ru.simsonic.rscPermissions.DataTypes.*;
import ru.simsonic.rscPermissions.DataTypes.RowEntity.EntityType;

public class LocalCacheData
{
	protected final MainPluginClass plugin;
	protected final HashMap<String, RowEntity> entities_g = new HashMap<>();
	protected final HashMap<String, RowEntity> entities_u = new HashMap<>();
	protected final ConcurrentHashMap<String, String> prefixes_u = new ConcurrentHashMap<>();
	protected final ConcurrentHashMap<String, String> suffixes_u = new ConcurrentHashMap<>();
	protected final ArrayList<RowPermission> permissions_p2g = new ArrayList<>();
	protected final ArrayList<RowPermission> permissions_p2u = new ArrayList<>();
	protected final ArrayList<RowInheritance> inheritance_g2g = new ArrayList<>();
	protected final ArrayList<RowInheritance> inheritance_g2u = new ArrayList<>();
	protected final ArrayList<RowLadder> ladders_g = new ArrayList<>();
	protected final ArrayList<RowLadder> ladders_u = new ArrayList<>();
	protected final ArrayList<RowServer> servers = new ArrayList<>();
	protected LocalCacheData(MainPluginClass rscp)
	{
		this.plugin = rscp;
	}
	public synchronized int ImportEntities(RowEntity[] rows)
	{
		entities_g.clear();
		entities_u.clear();
		if(rows == null)
			return 0;
		for(RowEntity row : rows)
		{
			if(row.entity_type == EntityType.groupName)
				entities_g.put(row.entity.toLowerCase(), row);
			else
				entities_u.put(row.entity.toLowerCase(), row);
		}
		return entities_g.size() + entities_u.size();
	}
	public synchronized int ImportPermissions(RowPermission[] rows)
	{
		permissions_p2g.clear();
		permissions_p2u.clear();
		if(rows == null)
			return 0;
		for(RowPermission row : rows)
		{
			if(row.entity_type == EntityType.groupName)
				permissions_p2g.add(row);
			else
				permissions_p2u.add(row);
		}
		return permissions_p2g.size() + permissions_p2u.size();
	}
	public synchronized int ImportInheritance(RowInheritance[] rows)
	{
		inheritance_g2g.clear();
		inheritance_g2u.clear();
		if(rows == null)
			return 0;
		for(RowInheritance row : rows)
		{
			if(row.child_type == EntityType.groupName)
				inheritance_g2g.add(row);
			else
				inheritance_g2u.add(row);
		}
		return inheritance_g2g.size() + inheritance_g2u.size();
	}
	public synchronized int ImportLadders(RowLadder[] rows)
	{
		ladders_g.clear();
		ladders_u.clear();
		if(rows == null)
			return 0;
		for(RowLadder row : rows)
		{
			if(row.climber_type == EntityType.groupName)
				ladders_g.add(row);
			else
				ladders_u.add(row);	
		}
		return ladders_g.size() + ladders_u.size();
	}
	public synchronized int ImportServers(RowServer[] rows)
	{
		servers.clear();
		if(rows == null)
			return 0;
		servers.addAll(Arrays.asList(rows));
		return servers.size();
	}
}