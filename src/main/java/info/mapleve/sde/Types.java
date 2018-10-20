package info.mapleve.sde;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;


public class Types {
	public static final String TYPES_FILE_PATH = "typeIDs.yaml"; 
	
	
	public static Type loadSingleTypeFromYaml(InputStream is) throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory())
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return mapper.readValue(is, Type.class);
	}
	
	
	public static Map<Integer,Type> loadMultipleTypesFromYaml(InputStream is) throws IOException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory())
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		JavaType type = mapper.getTypeFactory().constructParametricType(Map.class, Integer.class, Type.class);
		
		return mapper.readValue(is, type);
	}

	
	public static Types loadFromYaml() throws IOException, JsonMappingException {
		return loadFromYaml(new FileInputStream(TYPES_FILE_PATH));
	}
	
	
	public static Types loadFromYaml(InputStream is) throws IOException, JsonMappingException {
		Types types = new Types();
		
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory())
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JavaType javatype = mapper.getTypeFactory().constructParametricType(Map.class, Long.class, Type.class);

		Map<Long,Type> read = mapper.readValue(is, javatype);
		read.forEach((id,type) -> {
			type.setId(id);
			
			types.byId.put(id, type);
			types.byName.put(type.getName().getEn(), type);
		});
		
		return types;
	}
	
	

	private Map<Long,Type> byId = new HashMap<>(50000);
	private Map<String,Type> byName = new HashMap<>(50000);
	
	
	public Optional<Type> findByName(String typeName) {
		return Optional.ofNullable(byName.get(typeName));
	}
	
	public Optional<Type> findById(long id) {
		return Optional.ofNullable(byId.get(id));
	}
	
	
	@Override
	public String toString() {
		return "Types (" + byId.size() + " entries)";
	}


	public Type require(long type) {
		Type match = byId.get(type);
		
		if (match == null) {
			throw new IllegalStateException("unknown type with id: " + type);
		}
		
		return match;
	}
}
