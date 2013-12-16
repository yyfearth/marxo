package marxo;

import marxo.entity.MongoDbAware;
import marxo.test.BasicDataTests;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class SpringMongoDbAnnotationTests extends BasicDataTests {

	public static class PersonRepository implements MongoDbAware, CrudRepository<Person, ObjectId> {

		@Override
		public <S extends Person> S save(S entity) {
			mongoTemplate.save(entity);
			return entity;
		}

		@Override
		public <S extends Person> Iterable<S> save(Iterable<S> entities) {
			return null;
		}

		@Override
		public Person findOne(ObjectId objectId) {
			return mongoTemplate.findOne(Query.query(Criteria.where("_id").is(objectId)), Person.class);
		}

		@Override
		public boolean exists(ObjectId objectId) {
			return false;
		}

		@Override
		public Iterable<Person> findAll() {
			return null;
		}

		@Override
		public Iterable<Person> findAll(Iterable<ObjectId> objectIds) {
			return null;
		}

		@Override
		public long count() {
			return 0;
		}

		@Override
		public void delete(ObjectId objectId) {

		}

		@Override
		public void delete(Person entity) {

		}

		@Override
		public void delete(Iterable<? extends Person> entities) {

		}

		@Override
		public void deleteAll() {

		}
	}

	public static class Person {

		public Person(String name) {
			this.name = name;
		}

		@Id
		ObjectId id = new ObjectId();

//		@Transient
		String name;

		@Field("name")
		public String getName() {
			return name;
		}

		@Field("name")
		public void setName(String name) {
			this.name = name;
		}


		@DBRef
		List<Person> lovers = new ArrayList<>();
	}

	@Test
	public void useDbRef() throws Exception {
		Person person1 = new Person("James");
		Person person2 = new Person("Jane");
		person1.lovers.add(person2);
		person1.lovers.add(person2);

		PersonRepository personRepository = new PersonRepository();
		personRepository.save(person1);
		personRepository.save(person2);

		Person person = personRepository.findOne(person1.id);
		logger.info(person.toString());
	}
}
