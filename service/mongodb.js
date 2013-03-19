db = connect('localhost:27020/marxo');
// create collections
db.createCollection('workflows');
db.createCollection('nodes');
db.createCollection('links');
// create indexes
db.workflows.ensureIndex({ name: 1 });
db.nodes.ensureIndex({ workflowId: 1, name: 1 });
db.links.ensureIndex({ workflowId: 1, name: 1 });

// test data
db.workflows.insert({
    "_id": ObjectId("51447afb4728cb2036cf9ca1"),
    "name": "test_wf",
    "title": "Test Workflow",
    "description": "The test workflow",
    "nodeIdList": [
    	ObjectId("507f81413d070321728fde10"),
    	ObjectId("507f81413d070321728fde11"),
    	ObjectId("507f81413d070321728fde12"),
    	ObjectId("507f81413d070321728fde13"),
    	ObjectId("507f81413d070321728fde14"),
    	ObjectId("507f81413d070321728fde15")
    ],
    "linkIdList": [
    	ObjectId("507f81413d070321728fde20"),
    	ObjectId("507f81413d070321728fde21"),
    	ObjectId("507f81413d070321728fde22"),
    	ObjectId("507f81413d070321728fde23"),
    	ObjectId("507f81413d070321728fde24")
    ]
});

db.nodes.insert({
	"_id": ObjectId("507f81413d070321728fde10"),
	"workflowId": ObjectId("51447afb4728cb2036cf9ca1"),
	"name": "post_idea",
	"title": "Post Idea",
	"description": "￼Post software project ideas"
});
db.nodes.insert({
	"_id": ObjectId("507f81413d070321728fde11"),
	"workflowId": ObjectId("51447afb4728cb2036cf9ca1"),
	"name": "post_cancel",
	"title": "Post Cancel",
	"description": "Post cancel notification"
});
db.nodes.insert({
	"_id": ObjectId("507f81413d070321728fde12"),
	"workflowId": ObjectId("51447afb4728cb2036cf9ca1"),
	"name": "post_req",
	"title": "Post Requirement",
	"description": "Post project requirement"
});
db.nodes.insert({
	"_id": ObjectId("507f81413d070321728fde13"),
	"workflowId": ObjectId("51447afb4728cb2036cf9ca1"),
	"name": "submit_design",
	"title": "Submit Design",
	"description": "Retrieve theme design submissions & e-mail to stackholders"
});
db.nodes.insert({
	"_id": ObjectId("507f81413d070321728fde14"),
	"workflowId": ObjectId("51447afb4728cb2036cf9ca1"),
	"name": "notification",
	"title": "Notification",
	"description": "Notification"
});
db.nodes.insert({
	"_id": ObjectId("507f81413d070321728fde15"),
	"workflowId": ObjectId("51447afb4728cb2036cf9ca1"),
	"name": "post_result",
	"title": "Post Result",
	"description": "Post & e-mail result everyone"
});

db.links.insert({
	"_id": ObjectId("507f81413d070321728fde20"),
	"workflowId": ObjectId("51447afb4728cb2036cf9ca1"),
	"name": "to_cancel",
	"description": "Cancel if link ccount < 300",
	"previousNodeId": ObjectId("507f81413d070321728fde10"),
	"nextNodeId": ObjectId("507f81413d070321728fde11")
});
db.links.insert({
	"_id": ObjectId("507f81413d070321728fde21"),
	"workflowId": ObjectId("51447afb4728cb2036cf9ca1"),
	"name": "continue_to_req",
	"description": "Continue to post requirement if like count >= 300",
	"previousNodeId": ObjectId("507f81413d070321728fde10"),
	"nextNodeId": ObjectId("507f81413d070321728fde12")
});
db.links.insert({
	"_id": ObjectId("507f81413d070321728fde22"),
	"workflowId": ObjectId("51447afb4728cb2036cf9ca1"),
    "name": "post_req_to_submit_design",
	"previousNodeId": ObjectId("507f81413d070321728fde12"),
	"nextNodeId": ObjectId("507f81413d070321728fde13")
});
db.links.insert({
	"_id": ObjectId("507f81413d070321728fde23"),
	"workflowId": ObjectId("51447afb4728cb2036cf9ca1"),
	"name": "not_pass_to_notify",
	"description": "Notification if pass rate <= 50%",
	"previousNodeId": ObjectId("507f81413d070321728fde13"),
	"nextNodeId": ObjectId("507f81413d070321728fde14")
});
db.links.insert({
	"_id": ObjectId("507f81413d070321728fde24"),
	"workflowId": ObjectId("51447afb4728cb2036cf9ca1"),
	"name": "pass_to_post",
	"description": "Post & e-mail to everyone if pass rate > 50%",
	"previousNodeId": ObjectId("507f81413d070321728fde13"),
	"nextNodeId": ObjectId("507f81413d070321728fde15")
});
