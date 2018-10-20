package info.mapleve.bom;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import info.mapleve.sde.Type;


public class BOM {
	public static class LineItem {
		private List<LineItem> materials = new ArrayList<>();
		private long quantity;
		private Type type;
		
		public LineItem(long quantity, Type type) {
			this.quantity = quantity;
			this.type = Objects.requireNonNull(type);
		}
		
		public long getQuantity() {
			return quantity;
		}
		public void setQuantity(long quantity) {
			this.quantity = quantity;
		}
		public Type getType() {
			return type;
		}
		public void setType(Type type) {
			this.type = type;
		}
		public void setMaterials(List<LineItem> materials) {
			this.materials = materials;
		}
		/**
		 * Returns materials needed per item.
		 */
		public List<LineItem> getMaterials() {
			return materials;
		}
		
		public boolean isBaseMaterial() {
			return materials.isEmpty();
		}

		@Override
		public String toString() {
			return toString(1, 0);
		}
		
		public String toString(long multiplier, int indent) {
			StringBuilder buf = new StringBuilder();
			
			for (int i = 0; i < indent; i++) {
				buf.append(" ");
			}
			buf.append(quantity * multiplier).append("x ").append(type.toString());
			
			materials.forEach(mat -> {
				buf.append("\n");
				buf.append(mat.toString(quantity * multiplier, indent + 2));
			});
			
			return buf.toString();
		}
	}
	
	
	private List<LineItem> lineItems = new ArrayList<>();
	
	
	public List<LineItem> getLineItems() {
		return lineItems;
	}

	public void add(Type type) {
		add(1L, type);
	}
	
	public void add(long quantity, Type type) {
		lineItems.add(new LineItem(quantity, type));
	}
	
	public BOM aggregateLineItems() {
		/* n^2 complexity so not very efficient, but we assume BOMs have a few lines only */
		for (int i = lineItems.size() - 1; i >= 0; i--) {
			for (int j = 0; j < i; j++) {
				if (lineItems.get(i).type == lineItems.get(j).type) {
					lineItems.get(j).quantity += lineItems.get(i).quantity;
					lineItems.remove(i);
					break;
				}
			}
		}
		
		return this;
	}
	
	public BOM extractComponents() {
		BOM componentBom = new BOM();
		
		for (LineItem li : lineItems) {
			for (LineItem mat : li.materials) {
				if (!mat.isBaseMaterial()) {
					componentBom.add(li.quantity * mat.quantity, mat.type);
				}
			}
		}
		
		return componentBom;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		
		lineItems.forEach(li -> {
			buf.append(li).append("\n");
		});
		
		return buf.toString();
	}
}
