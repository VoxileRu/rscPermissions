package ru.simsonic.rscPermissions.DataTypes;
import java.util.ArrayList;

public class DatabaseContents
{
	public RowEntity      entities[];
	public RowPermission  permissions[];
	public RowInheritance inheritance[];
	public void normalize()
	{
		// Entities
		final ArrayList<RowEntity> listE = new ArrayList<>();
		if(entities != null)
			for(RowEntity rowE : entities)
			{
				listE.add(rowE);
			}
		entities = listE.toArray(new RowEntity[listE.size()]);
		// Permissions
		final ArrayList<RowPermission> listP = new ArrayList<>();
		if(permissions != null)
			for(RowPermission rowP : permissions)
			{
				listP.add(rowP);
			}
		permissions = listP.toArray(new RowPermission[listP.size()]);
		// Inheritance
		final ArrayList<RowInheritance> listI = new ArrayList<>();
		if(inheritance != null)
			for(RowInheritance rowI : inheritance)
			{
				listI.add(rowI);
			}
		inheritance = listI.toArray(new RowInheritance[listI.size()]);
	}
}
