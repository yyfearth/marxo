package marxo.controller;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import marxo.entity.MongoDbAware;
import marxo.entity.action.Action;
import marxo.entity.action.Content;
import marxo.entity.action.Submission;
import marxo.entity.user.User;
import marxo.entity.workflow.RunStatus;
import marxo.exception.EntityNotFoundException;
import marxo.security.MarxoAuthentication;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import java.util.List;

@Controller
@RequestMapping(value = "page{:s?}")
public class PageController extends BasicController implements MongoDbAware, InterceptorPreHandlable {
	protected static Sort defaultSort = new Sort(new Sort.Order(Sort.Direction.DESC, "updateTime")).and(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));

	protected User user;

	@Override
	public void preHandle() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication instanceof MarxoAuthentication) {
			MarxoAuthentication marxoAuthentication = (MarxoAuthentication) authentication;
			user = marxoAuthentication.getUser();
		} else {
			user = null;
		}
	}

	protected Criteria newDefaultCriteria() {
		return Criteria.where("type").is(Content.Type.PAGE);
	}

	@RequestMapping(value = "/{pageIdString:[\\da-fA-F]{24}}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Content read(@PathVariable String pageIdString) {
		ObjectId pageId = stringToObjectId(pageIdString);

		Query query = Query.query(newDefaultCriteria().and("_id").is(pageId));
		Content content = mongoTemplate.findOne(query, Content.class);

		if (content == null) {
			throw new EntityNotFoundException(Content.class, pageIdString);
		}

		Action action = content.getAction();

		if (action != null && !action.getStatus().equals(RunStatus.FINISHED)) {
			filter(content);
		}

		return content;
	}

	/**
	 * Should search only those contents which are Page type and under a started action
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public List<Content> search(@RequestParam(value = "tenant_id", required = false) ObjectId tenantId, @RequestParam(value = "project_id", required = false) ObjectId projectId) {
		Criteria criteria = Criteria
				.where("status").is(RunStatus.STARTED)
				.and("content").exists(true);

		if (tenantId != null) {
			criteria.and("tenantId").is(tenantId);
		}

		if (projectId != null) {
			criteria.and("workflowId").is(projectId);
			criteria.and("isProject").is(true);
		}

		Query query = Query.query(criteria).with(defaultSort);
		List<Action> actions = mongoTemplate.find(query, Action.class);

		List<Content> contents = Lists.transform(actions, new Function<Action, Content>() {
			@Nullable
			@Override
			public Content apply(@Nullable Action input) {
				return (input == null) ? null : input.getContent();
			}
		});

		for (Content content : contents) {
			filter(content);
		}

		return contents;
	}

	@RequestMapping(value = "/{idString:[\\da-fA-F]{24}}/view", method = RequestMethod.PUT)
	public int updateViewCount(@PathVariable String idString) {
		ObjectId contentId = stringToObjectId(idString);

		Criteria criteria = newDefaultCriteria()
				.and("_id").is(contentId);
		Content content = mongoTemplate.findOne(Query.query(criteria), Content.class);

		if (content == null) {
			throw new EntityNotFoundException(Content.class, contentId);
		}

		content.increaseViewCount();
		content.save();

		return content.getViewCount();
	}

	@RequestMapping(value = "/{pageIdString:[\\da-fA-F]{24}}/submission{:s?}", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public Submission addSubmission(@PathVariable String pageIdString, @RequestBody Submission submission) throws Exception {
		Assert.isTrue(ObjectId.isValid(pageIdString));
		ObjectId pageId = new ObjectId(pageIdString);

		Assert.notNull(submission);

		submission.createUserId = submission.updateUserId = user.id;
		submission.createTime = submission.updateTime = DateTime.now();

		Criteria criteria = newDefaultCriteria().and("_id").is(pageId);
		Query query = Query.query(criteria);

		Update update = new Update().addToSet("submissions", submission);

		Content content = mongoTemplate.findAndModify(query, update, Content.class);
		filter(content);

		return submission;
	}

	@RequestMapping(value = "/{pageIdString:[\\da-fA-F]{24}}/submission{:s?}/mine", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Submission getOwnSubmission(@PathVariable String pageIdString) throws Exception {
		Assert.isTrue(ObjectId.isValid(pageIdString));
		ObjectId pageId = new ObjectId(pageIdString);

		Criteria criteria = newDefaultCriteria().and("_id").is(pageId).and("submissions").elemMatch(
				Criteria.where("createUserId").is(user.id)
		);
		Query query = Query.query(criteria);
		query.fields().include("submissions");

		Content content = mongoTemplate.findOne(query, Content.class);

		if (content == null || content.submissions.size() < 1) {
			throw new EntityNotFoundException(Content.class, pageId);
		}

		return content.submissions.get(0);
	}

	protected void filter(Content content) {
		content.records = null;
		content.submissions = null;
	}
}
