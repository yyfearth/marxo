package marxo.controller;

import com.mongodb.WriteResult;
import marxo.entity.Task;
import marxo.entity.action.Action;
import marxo.entity.action.TrackableAction;
import marxo.entity.node.Node;
import marxo.entity.workflow.Notification;
import marxo.entity.workflow.RunStatus;
import marxo.entity.workflow.Workflow;
import marxo.exception.EntityNotFoundException;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("action{:s?}")
public class ActionController extends EntityController<Action> {
	@Override
	public Action read(@PathVariable String idString) throws Exception {
		ObjectId objectId = stringToObjectId(idString);

		Criteria criteria = Criteria
				.where("tenantId").is(user.tenantId)
				.and("_id").is(objectId);
		Action action = mongoTemplate.findOne(Query.query(criteria), entityClass);

		if (action == null) {
			throw new EntityNotFoundException(entityClass, objectId);
		}

		return action;
	}

	@RequestMapping(value = "/{idString:[\\da-fA-F]{24}}/status", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public RunStatus updateStatus(@PathVariable String idString, @RequestBody RunStatus status) throws Exception {
		ObjectId objectId = stringToObjectId(idString);

		Update update = Update.update("status", status);
		WriteResult result = mongoTemplate.updateFirst(newDefaultQuery(objectId), update, Action.class);
		throwIfError(result);

		if (result.getN() == 0) {
			throw new EntityNotFoundException(Action.class, objectId);
		}

		Action action = Action.get(objectId);

		if (action.isFinished()) {
			Workflow workflow = action.getWorkflow();

			if (action instanceof TrackableAction) {
				workflow.removeTracableAction((TrackableAction) action);

				Node node = action.getNode();
				boolean isFinished = true;
				for (Action action1 : action.getNode().getActions()) {
					if (action1.isNot(RunStatus.FINISHED)) {
						isFinished = false;
						break;
					}
				}
				if (isFinished) {
					logger.info(String.format("%s finishes tracking", node));
					node.setStatus(RunStatus.FINISHED);
					node.save();
					Notification.saveNew(Notification.Level.NORMAL, node, Notification.Type.FINISHED);
					workflow.removeCurrentNode(node);

					if (workflow.getTrackedActions().isEmpty()) {
						logger.info(String.format("%s finishes tracking", workflow));
						workflow.setStatus(RunStatus.FINISHED);
						Notification.saveNew(Notification.Level.NORMAL, workflow, Notification.Type.FINISHED);
					}
				}

				workflow.save();
				Task.schedule(action.workflowId, DateTime.now());
			}
		}

		return status;
	}
}
