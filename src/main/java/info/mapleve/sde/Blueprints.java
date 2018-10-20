package info.mapleve.sde;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import info.mapleve.sde.Blueprint.Activities.Manufacturing.QuantifiedType;


public class Blueprints {
	private static final String BLUEPRINTS_FILE_PATH = "blueprints.yaml";


	public static Blueprint loadSingleBlueprintFromYaml(InputStream is) throws JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory())
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return mapper.readValue(is, Blueprint.class);
	}
	
	
	private final Types types;
	
	
	public Blueprints(Types types) {
		this.types = Objects.requireNonNull(types);
	}
	
	public Blueprints loadFromYaml() throws IOException, JsonMappingException {
		return loadFromYaml(new FileInputStream(BLUEPRINTS_FILE_PATH));
	}
	
	
	public Blueprints loadFromYaml(InputStream is) throws IOException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory())
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JavaType javatype = mapper.getTypeFactory().constructParametricType(Map.class, Long.class, Blueprint.class);

		Map<Long,Blueprint> read = mapper.readValue(is, javatype);
		read.forEach((id,blueprint) -> {
			blueprint.setId(id);
			
			Optional<Type> bptype = types.findById(id);
			if (!bptype.isPresent()) {
				System.err.println("ignoring invalid blueprint with id: " + id);
				return;
			}
			blueprint.setName(bptype.get().toString());

			
			byId.put(id, blueprint);
			
			List<QuantifiedType> products = blueprint.getManufacturingProducts();
			if (products.size() > 1) {
				throw new UnsupportedOperationException("multiple products not supported");
			}
			if (products.size() == 1) {
				Optional<Type> ptype = types.findById(products.get(0).getTypeID());
				if (!ptype.isPresent()) {
					System.err.println("ignoring invalid product in blueprint: " + blueprint);
				}
				else {
					byProduct.put(ptype.get(), blueprint);
				}
			}
		});
		
		return this;
	}
	
	
	public Optional<Blueprint> require(long id) {
		return Optional.ofNullable(byId.get(id));
	}
	
	
	public Optional<Blueprint> findByProduct(Type product) {
		return Optional.ofNullable(byProduct.get(product));
	}
	
	
	private Map<Long,Blueprint> byId = new HashMap<>(50000);
	private Map<Type,Blueprint> byProduct = new HashMap<>(50000);
}
