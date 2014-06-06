package ru.simsonic.rscPermissions.DataTypes;
import java.sql.Timestamp;

public class RowReward
{
	public int id;
	public String user;
	public String code;
	public boolean activated;
	public Timestamp activated_timestamp;
	public String execute_commands;
	public String command_permissions;
	public String add_group;
	public String add_group_destination;
	public int add_group_expirience;
	public String add_group_interval;	
}