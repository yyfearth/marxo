define 'test_data', ['models'], ({Workflows, Nodes, Links}) ->

  WORKFLOWS = [
    id: '51447afb4728cb2036cf9ca1'
    name: 'test_wf'
    title: 'Test Workflow'
    desc: 'The test workflow'
    nodes: [
      id: '507f81413d070321728fde10'
      name: 'post_idea'
      title: 'Post Idea'
      desc: 'Post software project ideas'
      workflowId: '51447afb4728cb2036cf9ca1'
      actions: []
      created: 1363879373649,
      modified: 1363879373649,
      objectType: 'Node'
    ,
      id: '507f81413d070321728fde11'
      name: 'post_cancel'
      title: 'Post Cancel'
      desc: 'Post cancel notification'
      workflowId: '51447afb4728cb2036cf9ca1'
      actions: []
      created: 1363879373649,
      modified: 1363879373649,
      objectType: 'Node'
    ,
      id: '507f81413d070321728fde12'
      name: 'post_req'
      title: 'Post Requirement'
      desc: 'Post project requirement'
      workflowId: '51447afb4728cb2036cf9ca1'
      actions: []
      created: 1363879373649,
      modified: 1363879373649,
      objectType: 'Node'
    ,
      id: '507f81413d070321728fde13'
      name: 'submit_design'
      title: 'Submit Design'
      desc: 'Retrieve theme design submissions & e-mail to stackholders'
      workflowId: '51447afb4728cb2036cf9ca1'
      actions: []
      created: 1363879373649,
      modified: 1363879373649,
      objectType: 'Node'
    ,
      id: '507f81413d070321728fde14'
      name: 'notification'
      title: 'Notification'
      desc: 'Notification'
      workflowId: '51447afb4728cb2036cf9ca1'
      actions: []
      created: 1363879373649,
      modified: 1363879373649,
      objectType: 'Node'
    ,
      id: '507f81413d070321728fde15'
      name: 'post_result'
      title: 'Post Result'
      desc: 'Post & e-mail result everyone'
      workflowId: '51447afb4728cb2036cf9ca1'
      actions: []
      created: 1363879373649,
      modified: 1363879373649,
      objectType: 'Node'
    ]
    links: [
      id: '507f81413d070321728fde22'
      name: 'post_req_to_submit_design'
      workflowId: '51447afb4728cb2036cf9ca1'
      prevNodeId: '507f81413d070321728fde12'
      nextNodeId: '507f81413d070321728fde13'
      created: 1363879373649,
      modified: 1363879373649,
      objectType: 'Link'
    ,
      id: '507f81413d070321728fde24'
      name: 'pass_to_post'
      desc: 'Post & e-mail to everyone if pass rate > 50%'
      workflowId: '51447afb4728cb2036cf9ca1'
      prevNodeId: '507f81413d070321728fde13'
      nextNodeId: '507f81413d070321728fde15'
      created: 1363879373649,
      modified: 1363879373649,
      objectType: 'Link'
    ,
      id: '507f81413d070321728fde20'
      name: 'to_cancel'
      title: 'Like < 300'
      desc: 'Cancel if like count < 300'
      workflowId: '51447afb4728cb2036cf9ca1'
      prevNodeId: '507f81413d070321728fde10'
      nextNodeId: '507f81413d070321728fde11'
      created: 1363879373649,
      modified: 1363879373649,
      objectType: 'Link'
    ,
      id: '507f81413d070321728fde21'
      name: 'continue_to_req'
      title: 'Like >= 300'
      desc: 'Continue to post requirement if like count >= 300'
      workflowId: '51447afb4728cb2036cf9ca1'
      prevNodeId: '507f81413d070321728fde10'
      nextNodeId: '507f81413d070321728fde12'
      created: 1363879402429
      modified: 1363879402429
      objectType: 'Link'
    ,
      id: '507f81413d070321728fde23'
      name: 'not_pass_to_notify'
      title: 'Pass rate <= 50%'
      desc: 'Notification if pass rate <= 50%'
      workflowId: '51447afb4728cb2036cf9ca1'
      prevNodeId: '507f81413d070321728fde13'
      nextNodeId: '507f81413d070321728fde14'
      created: 1363879447134
      modified: 1363879447134
      objectType: 'Link'
    ]
    nodeIds: [
      '507f81413d070321728fde10'
      '507f81413d070321728fde11'
      '507f81413d070321728fde12'
      '507f81413d070321728fde13'
      '507f81413d070321728fde14'
      '507f81413d070321728fde15'
    ]
    linkIds: [
      '507f81413d070321728fde20'
      '507f81413d070321728fde21'
      '507f81413d070321728fde22'
      '507f81413d070321728fde23'
      '507f81413d070321728fde24'
    ]
    created: 1363879373649
    modified: 1363879373649
    objectType: 'Workflow'
  ]

  workflows = new Workflows
  WORKFLOWS.forEach (wf) -> workflows.create wf

  console.log 'test data loaded'
  { workflows }
