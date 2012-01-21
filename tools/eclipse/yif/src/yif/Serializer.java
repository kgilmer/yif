package yif;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import yif.model.Issue;

/**
 * Helper class to serialize and deserialize yaml to models.
 * @author kgilmer
 *
 */
public class Serializer {

	/**
	 * Serialize model
	 * @param issueList
	 * @param destination
	 * @throws IOException
	 */
	public static void serialize(List<Issue> issueList, File destination) throws IOException {
		Representer representer = new Representer();
		representer.addClassTag(Issue.class, new Tag("!issue"));
		representer.setPropertyUtils(new PropertyUtils() {
			@Override
			protected Set<Property> createPropertySet(Class<? extends Object> type, BeanAccess bAccess) throws IntrospectionException {

				Map<String, Property> props = getPropertiesMap(type, BeanAccess.FIELD);
				Property id = props.get("id");
				Property title = props.get("title");
				Property complete = props.get("complete");
				Property notes = props.get("notes");

				props.remove("id");
				props.remove("title");
				props.remove("notes");
				props.remove("complete");

				Set<Property> result = new LinkedHashSet<Property>();
				result.add(id);
				result.add(title);
				result.add(complete);
				result.addAll(props.values());
				result.add(notes);

				return result;
			}
		});
	
		Yaml yaml = new Yaml(representer, new DumperOptions());
	
		yaml.dump(issueList, new FileWriter(destination));		
	}

	/**
	 * @param file
	 * @return deserialized model
	 */
	public static List<Issue> deserialize(InputStream file) {
		Constructor constructor = new Constructor();

		constructor.addTypeDescription(new TypeDescription(Issue.class, "!issue"));

		Yaml yaml = new Yaml(constructor);
		
		return (List<Issue>) yaml.load(file);
	}
}
