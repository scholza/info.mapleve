package info.mapleve.sde;


public class Type {
	public static class Name {
		private String en;

		public String getEn() {
			return en;
		}

		public void setEn(String en) {
			this.en = en;
		}
	}
	
	
	private Name name;
	private long id;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	
	@Override
	public String toString() {
		return name == null ? "<unknown>" : name.en;
	}
}
