package marxo.test;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import marxo.entity.BasicEntity;
import marxo.entity.MongoDbAware;
import marxo.tool.Loggable;
import marxo.validation.SelectIdFunction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class BasicDataTests implements MongoDbAware, Loggable {
	protected List<BasicEntity> entitiesToRemove = new ArrayList<>();

	/**
	 * Save everything in `entitiesToInsert`, and add them in `entitiesToRemove`. Also clear `entitiesToInsert`.
	 */
	public void insertEntities(List<? extends BasicEntity> entities) {
		if (entities.size() > 0) {
			for (BasicEntity entity : entities) {
				entity.setName(entity.getName() + " for " + getClass().getSimpleName());
			}
			mongoTemplate.insertAll(entities);
			entitiesToRemove.addAll(entities);
		}
	}

	public void insertEntities(BasicEntity... entities) {
		insertEntities(Lists.newArrayList(entities));
	}

	@BeforeClass
	public void beforeClass() throws Exception {
	}

	@AfterClass
	public void afterClass() throws Exception {
		Criteria criteria = Criteria.where("_id").in(Collections2.transform(entitiesToRemove, SelectIdFunction.getInstance()));
		for (String collectionName : mongoTemplate.getCollectionNames()) {
			mongoTemplate.remove(Query.query(criteria), collectionName);
		}
	}

	Stopwatch stopwatch;

	@BeforeMethod
	public void beforeMethod() throws Exception {
		stopwatch = Stopwatch.createStarted();
	}

	@AfterMethod
	public void afterMethod(ITestResult result) throws Exception {
		stopwatch.stop();
		logger.info(String.format("%s in %d ms", result.getMethod().getMethodName(), stopwatch.elapsed(TimeUnit.MILLISECONDS)));
	}
}
