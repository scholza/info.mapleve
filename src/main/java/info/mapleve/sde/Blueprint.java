package info.mapleve.sde;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import info.mapleve.sde.Blueprint.Activities.Manufacturing.QuantifiedType;


public class Blueprint {
	public static class Activities {
		public static class Manufacturing {
			public static class QuantifiedType {
				public long getTypeID() {
					return typeID;
				}
				public void setTypeID(long type) {
					this.typeID = type;
				}
				public long getQuantity() {
					return quantity;
				}
				public void setQuantity(long quantity) {
					this.quantity = quantity;
				}
				
				private long typeID;
				private long quantity;
			}
			
			
			private long time;
			private List<QuantifiedType> materials = new ArrayList<>();
			private List<QuantifiedType> products = new ArrayList<>();

			
			public List<QuantifiedType> getProducts() {
				return products;
			}

			public void setProducts(List<QuantifiedType> products) {
				this.products = products;
			}

			public List<QuantifiedType> getMaterials() {
				return materials;
			}

			public void setMaterials(List<QuantifiedType> materials) {
				this.materials = materials;
			}

			public long getTime() {
				return time;
			}

			public void setTime(long time) {
				this.time = time;
			}
		}

		
		private Manufacturing manufacturing;

		public Manufacturing getManufacturing() {
			return manufacturing;
		}

		public void setManufacturing(Manufacturing manufacturing) {
			this.manufacturing = manufacturing;
		}
	}

	
	private Activities activities;
	private long id;
	private String name;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Activities getActivities() {
		return activities;
	}

	public void setActivities(Activities activities) {
		this.activities = activities;
	}

	public List<QuantifiedType> getManufacturingProducts() {
		if (activities != null) {
			if (activities.manufacturing != null) {
				return activities.manufacturing.products;
			}
		}
		
		return Collections.emptyList();
	}
	
	public List<QuantifiedType> getManufacturingMaterials() {
		if (activities != null) {
			if (activities.manufacturing != null) {
				return activities.manufacturing.materials;
			}
		}
		
		return Collections.emptyList();
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
