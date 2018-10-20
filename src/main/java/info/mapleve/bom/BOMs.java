package info.mapleve.bom;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.mapleve.bom.BOM.LineItem;
import info.mapleve.sde.Blueprints;
import info.mapleve.sde.Types;
import info.mapleve.sde.Blueprint.Activities.Manufacturing.QuantifiedType;


public class BOMs {
	private static final Pattern FITTING = Pattern.compile("\\s*\\[([\\w|\\s]+),([\\w|\\s]+)\\]\\s*");
	
	
	private final Types types;
	private final Blueprints blueprints;
	
	
	public BOMs(Types types, Blueprints blueprints) {
		this.types = Objects.requireNonNull(types);
		this.blueprints = Objects.requireNonNull(blueprints);
	}
	
	
	public BOM parse(String bom) {
		BOM result = new BOM();
		
		for (String line : bom.split("\\r\\n|[\\r\\n]")) {
			if (line.trim().isEmpty()) {
				continue;
			}

			String itemName = line;
			
			Matcher matcher = FITTING.matcher(line);
			if (matcher.matches()) {
				/* read ship name from first match */
				itemName = matcher.group(1);
			}
			
			types.findByName(itemName).ifPresent(result::add);
		}
		
		return result;
	}
	
	
	public BOM calculateMaterials(BOM bom) {
		for (LineItem item : bom.getLineItems()) {
			calculateMaterials(item);
		}
		
		return bom;
	}
	
	

	private void calculateMaterials(LineItem item) {
		blueprints.findByProduct(item.getType()).ifPresent(bp -> {
			List<LineItem> materials = new ArrayList<>();
			
			for (QuantifiedType qt : bp.getManufacturingMaterials()) {
				materials.add(new LineItem(qt.getQuantity(), types.require(qt.getTypeID())));
			}
			
			item.setMaterials(materials);
		});
		
		item.getMaterials().forEach(mat -> calculateMaterials(mat));
	}
}
