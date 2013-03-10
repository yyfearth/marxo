package marxo.dao;

import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.Key;
import com.github.jmkgreen.morphia.query.Query;
import com.github.jmkgreen.morphia.query.UpdateOperations;
import com.github.jmkgreen.morphia.query.UpdateResults;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import marxo.Bean.Project;
import marxo.data.MongoDbConnector;

import java.util.UUID;

public class ProjectDao implements IDao<Project> {
	static Datastore datastore = null;

	static {
		datastore = MongoDbConnector.getDatastore();
	}

	@Override
	public boolean create(Project project) {
		Key<Project> key = datastore.save(project);

		return (key != null);
	}

	@Override
	public Project read(UUID id) {
		return datastore.get(Project.class, id);
	}

	/**
	 * Simple remove the previous object and insert the new one. If you would like to update a specific field, use updateField.
	 * <note>
	 * Updating might be more complicated than what you think, especially if you don't have NoSQL experience.
	 * <a href="https://github.com/jmkgreen/morphia/wiki/Updating">https://github.com/jmkgreen/morphia/wiki/Updating</a>
	 * </note>
	 *
	 * @param project
	 * @return
	 * @see
	 */
	@Override
	public boolean update(Project project) {
		DBCollection dbCollection = datastore.getDB().getCollection(this.getClass().getName());


		if (delete(project.getId()) == false) {
			return false;
		}

		return
	}

	public boolean updateField(UUID projectId, String fieldName, Object value) {
		Query<Project> query = datastore.createQuery(Project.class).field("id").equal(projectId);
		UpdateOperations<Project> updateOperations = datastore.createUpdateOperations(Project.class).set(fieldName, value);
		UpdateResults<Project> updateResults = datastore.update(query, updateOperations);

		return updateResults.getError() == null;
	}

	@Override
	public boolean createOrUpdate(Project project) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean delete(UUID id) {

	}

	@Override
	public Project findOne(UUID id) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Project[] find(BasicDBObject query) {
		return new Project[0];  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean removeAll() {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
