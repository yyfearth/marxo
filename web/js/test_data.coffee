"use strict"

define 'test_data', ['models'], ({
Workflows, Nodes, Links
Contents
}) ->

  WORKFLOWS = [
    id: '51447afb4728cb2036cf9ca1'
    name: 'test_wf'
    title: 'Test Workflow'
    desc: 'The test workflow'
    created_at: new Date(1363879373649)
    updated_at: new Date(1363879373649)
    nodes: [
      id: '507f81413d070321728fde10'
      name: 'post_idea'
      title: 'Post Idea'
      desc: 'Post software project ideas'
      workflow_id: '51447afb4728cb2036cf9ca1'
      actions: []
      created_at: new Date(1363879373649)
      updated_at: new Date(1363879373649)
    ,
      id: '507f81413d070321728fde11'
      name: 'post_cancel'
      title: 'Post Cancel'
      desc: 'Post cancel notification'
      workflow_id: '51447afb4728cb2036cf9ca1'
      actions: []
      created_at: new Date(1363879373649)
      updated_at: new Date(1363879373649)
    ,
      id: '507f81413d070321728fde12'
      name: 'post_req'
      title: 'Post Requirement'
      desc: 'Post project requirement'
      workflow_id: '51447afb4728cb2036cf9ca1'
      actions: []
      created_at: new Date(1363879373649)
      updated_at: new Date(1363879373649)
    ,
      id: '507f81413d070321728fde13'
      name: 'submit_design'
      title: 'Submit Design'
      desc: 'Retrieve theme design submissions & e-mail to stackholders'
      workflow_id: '51447afb4728cb2036cf9ca1'
      actions: []
      created_at: new Date(1363879373649)
      updated_at: new Date(1363879373649)
    ,
      id: '507f81413d070321728fde14'
      name: 'notification'
      title: 'Notification'
      desc: 'Notification'
      workflow_id: '51447afb4728cb2036cf9ca1'
      actions: []
      created_at: new Date(1363879373649)
      updated_at: new Date(1363879373649)
    ,
      id: '507f81413d070321728fde15'
      name: 'post_result'
      title: 'Post Result'
      desc: 'Post & e-mail result everyone'
      workflow_id: '51447afb4728cb2036cf9ca1'
      actions: []
      created_at: new Date(1363879373649)
      updated_at: new Date(1363879373649)
    ]
    links: [
      id: '507f81413d070321728fde22'
      name: 'post_req_to_submit_design'
      workflow_id: '51447afb4728cb2036cf9ca1'
      prev_node_id: '507f81413d070321728fde12'
      next_node_id: '507f81413d070321728fde13'
      created_at: new Date(1363879373649)
      updated_at: new Date(1363879373649)
    ,
      id: '507f81413d070321728fde24'
      name: 'pass_to_post'
      desc: 'Post & e-mail to everyone if pass rate > 50%'
      workflow_id: '51447afb4728cb2036cf9ca1'
      prev_node_id: '507f81413d070321728fde13'
      next_node_id: '507f81413d070321728fde15'
      created_at: new Date(1363879373649)
      updated_at: new Date(1363879373649)
    ,
      id: '507f81413d070321728fde20'
      name: 'to_cancel'
      title: 'Like < 300'
      desc: 'Cancel if like count < 300'
      workflow_id: '51447afb4728cb2036cf9ca1'
      prev_node_id: '507f81413d070321728fde10'
      next_node_id: '507f81413d070321728fde11'
      created_at: new Date(1363879373649)
      updated_at: new Date(1363879373649)
    ,
      id: '507f81413d070321728fde21'
      name: 'continue_to_req'
      title: 'Like >= 300'
      desc: 'Continue to post requirement if like count >= 300'
      workflow_id: '51447afb4728cb2036cf9ca1'
      prev_node_id: '507f81413d070321728fde10'
      next_node_id: '507f81413d070321728fde12'
      created_at: new Date(1363879402429)
      updated_at: new Date(1363879402429)
    ,
      id: '507f81413d070321728fde23'
      name: 'not_pass_to_notify'
      title: 'Pass rate <= 50%'
      desc: 'Notification if pass rate <= 50%'
      workflow_id: '51447afb4728cb2036cf9ca1'
      prev_node_id: '507f81413d070321728fde13'
      next_node_id: '507f81413d070321728fde14'
      created_at: new Date(1363879447134)
      updated_at: new Date(1363879447134)
    ]
    node_ids: [
      '507f81413d070321728fde10'
      '507f81413d070321728fde11'
      '507f81413d070321728fde12'
      '507f81413d070321728fde13'
      '507f81413d070321728fde14'
      '507f81413d070321728fde15'
    ]
    link_ids: [
      '507f81413d070321728fde20'
      '507f81413d070321728fde21'
      '507f81413d070321728fde22'
      '507f81413d070321728fde23'
      '507f81413d070321728fde24'
    ]
  ,
    id: '507f81413d070321729fde06'
    name: 'demo_wf'
    desc: 'A Demo Workflow'
    title: 'Demo Workflow'
    tenant_id: '507f81413d070321729fdeff'
    created_at: new Date(1371357306807)
    updated_at: new Date(1371357306807)
    nodes: [
      id: '507f81413d070321729fde10'
      name: 'postidea'
      title: 'Post Ideas'
      desc: 'Post software project ideas'
      workflow_id: '507f81413d070321729fde06'
      tenant_id: '507f81413d070321729fdeff'
      actions: [ 'Action' ]
      created_at: new Date(1371357306807)
      updated_at: new Date(1371357306807)
    ,
      id: '507f81413d070321729fde11'
      name: 'post_cancel'
      title: 'Post Cancel'
      desc: 'Post cancel notification'
      workflow_id: '507f81413d070321729fde06'
      tenant_id: '507f81413d070321729fdeff'
      actions: [ 'Action' ]
      created_at: new Date(1371357306807)
      updated_at: new Date(1371357306807)
    ,
      id: '507f81413d070321729fde12'
      name: 'post_req'
      title: 'Post Requirement'
      desc: 'Post project requirement'
      workflow_id: '507f81413d070321729fde06'
      tenant_id: '507f81413d070321729fdeff'
      actions: [ 'Action' ]
      created_at: new Date(1371357306807)
      updated_at: new Date(1371357306807)
    ,
      id: '507f81413d070321729fde13'
      name: 'submit_design'
      title: 'Submit Design'
      desc: 'Retrieve theme design submissions'
      workflow_id: '507f81413d070321729fde06'
      tenant_id: '507f81413d070321729fdeff'
      actions: [ 'Action' ]
      created_at: new Date(1371357306807)
      updated_at: new Date(1371357306807)
    ,
      id: '507f81413d070321729fde14'
      name: 'notification'
      title: 'Notification'
      desc: 'Notification'
      workflow_id: '507f81413d070321729fde06'
      tenant_id: '507f81413d070321729fdeff'
      actions: [ 'Action' ]
      created_at: new Date(1371357306807)
      updated_at: new Date(1371357306807)
    ,
      id: '507f81413d070321729fde15'
      name: 'post_result'
      title: 'Post Result'
      desc: 'Post & e-mail result everyone'
      workflow_id: '507f81413d070321729fde06'
      tenant_id: '507f81413d070321729fdeff'
      actions: [ 'Action' ]
      created_at: new Date(1371357306807)
      updated_at: new Date(1371357306807)
    ]
    links: [
      id: '507f81413d070321729fde21'
      name: 'to_cancel'
      title: 'Like Count < 300'
      desc: 'Cancel if like count < 300'
      workflow_id: '507f81413d070321729fde06'
      tenant_id: '507f81413d070321729fdeff'
      condition:
        left_operand: 'like.account'
        left_operand_type: 'data.number'
        right_operand: '300'
        right_operand_type: 'number'
        operator: '<'
      prev_node_id: '507f81413d070321729fde10'
      next_node_id: '507f81413d070321729fde11'
      created_at: new Date(1371357306807)
      updated_at: new Date(1371357306807)
    ,
      id: '507f81413d070321729fde21'
      name: 'continue_to_req'
      title: 'Like Count >= 300'
      desc: 'Continue to post requirement if like count >= 300'
      workflow_id: '507f81413d070321729fde06'
      tenant_id: '507f81413d070321729fdeff'
      condition:
        left_operand: 'like.account'
        left_operand_type: 'data.number'
        right_operand: '300'
        right_operand_type: 'number'
        operator: '>='
      prev_node_id: '507f81413d070321729fde10'
      next_node_id: '507f81413d070321729fde12'
      created_at: new Date(1371357306807)
      updated_at: new Date(1371357306807)
    ,
      id: '507f81413d070321729fde22'
      workflow_id: '507f81413d070321729fde06'
      tenant_id: '507f81413d070321729fdeff'
      prev_node_id: '507f81413d070321729fde12'
      next_node_id: '507f81413d070321729fde13'
      created_at: new Date(1371357306807)
      updated_at: new Date(1371357306807)
    ,
      id: '507f81413d070321729fde22'
      name: 'not_pass_to_notify'
      title: 'Pass Rate <= 50%'
      desc: 'Notification if pass rate <= 50%'
      workflow_id: '507f81413d070321729fde06'
      tenant_id: '507f81413d070321729fdeff'
      condition:
        left_operand: 'pass.rate'
        left_operand_type: 'data.number'
        right_operand: '50'
        right_operand_type: 'number'
        operator: '<'
      prev_node_id: '507f81413d070321729fde13'
      next_node_id: '507f81413d070321729fde14'
      created_at: new Date(1371357306807)
      updated_at: new Date(1371357306807)
    ,
      id: '507f81413d070321729fde23'
      name: 'pass_to_post'
      title: 'Pass Rate > 50%'
      desc: 'Post & e-mail to everyone if pass rate > 50%'
      workflow_id: '507f81413d070321729fde06'
      tenant_id: '507f81413d070321729fdeff'
      condition:
        left_operand: 'pass.rate'
        left_operand_type: 'data.number'
        right_operand: '50'
        right_operand_type: 'number'
        operator: '>'
      prev_node_id: '507f81413d070321729fde13'
      next_node_id: '507f81413d070321729fde15'
      created_at: new Date(1371357306807)
      updated_at: new Date(1371357306807)
    ]
    node_ids: [
      '507f81413d070321729fde10'
      '507f81413d070321729fde11'
      '507f81413d070321729fde12'
      '507f81413d070321729fde13'
      '507f81413d070321729fde14'
      '507f81413d070321729fde15'
    ]
    link_ids: [
      '507f81413d070321729fde21'
      '507f81413d070321729fde21'
      '507f81413d070321729fde22'
      '507f81413d070321729fde22'
      '507f81413d070321729fde23'
    ]
  ]

  CONTENTS = [
    id: 'e57bf4f6aad752f9fc37fcf1'
    title: 'SOMY X100 Project Initialization'
    desc: 'SOMY X100 Project Initialization Page'
    media: 'PAGE'
    project:
      id: '577d415a8287e48327d8443c'
      title: 'SOMY X100'
    node:
      id: '507f81413d070321728fde10'
      title: 'Post Idea'
    action:
      id: 'f8e39128e0b51ecb5b1e6e40'
      title: 'Post to Multiple Socal Media'
    url: 'about:blank'
    status: 'POSTED'
    created_at: new Date(1371191307656)
    posted_at: new Date(1371191307656)
  ,
    id: 'e57bf4f6aad752f9fc37fcf2'
    title: 'SOMY X100 Project Initialization'
    desc: 'SOMY X100 Project Initialization Facebook Post'
    media: 'FACEBOOK'
    project:
      id: '577d415a8287e48327d8443c'
      title: 'SOMY X100'
    node:
      id: '507f81413d070321728fde10'
      title: 'Post Idea'
    action:
      id: 'f8e39128e0b51ecb5b1e6e40'
      title: 'Post to Multiple Socal Media'
    url: 'about:blank'
    status: 'POSTED'
    created_at: new Date(1371191307656)
    posted_at: new Date(1371191307656)
  ,
    id: 'e57bf4f6aad752f9fc37fcf3'
    title: 'SOMY X100 Project Initialization'
    desc: 'SOMY X100 Project Initialization Twitter Post'
    media: 'TWITTER'
    project:
      id: '577d415a8287e48327d8443c'
      title: 'SOMY X100'
    node:
      id: '507f81413d070321728fde10'
      title: 'Post Idea'
    action:
      id: 'f8e39128e0b51ecb5b1e6e40'
      title: 'Post to Multiple Socal Media'
    url: 'about:blank'
    status: 'POSTED'
    created_at: new Date(1371191307656)
    posted_at: new Date(1371191307656)
  ,
    id: 'e57bf4f6aad752f9fc37fef1'
    title: 'SOMY X100 Project Notification'
    desc: 'SOMY X100 Project Notification Page'
    media: 'PAGE'
    project:
      id: '577d415a8287e48327d8443c'
      title: 'SOMY X100'
    node:
      id: '507f81413d070321728fde14'
      title: 'Notification'
    action:
      id: 'f8e39128e0b51ecb5b1e6e40'
      title: 'Post to Multiple Socal Media'
    status: 'WAITING'
    created_at: new Date(1371191309656)
  ,
    id: 'e57bf4f6aad752f9fc37fef2'
    title: 'SOMY X100 Project Notification'
    desc: 'SOMY X100 Project Notification Facebook Post'
    media: 'FACEBOOK'
    project:
      id: '577d415a8287e48327d8443c'
      title: 'SOMY X100'
    node:
      id: '507f81413d070321728fde14'
      title: 'Notification'
    action:
      id: 'f8e39128e0b51ecb5b1e6e40'
      title: 'Post to Multiple Socal Media'
    status: 'WAITING'
    created_at: new Date(1371191309656)
  ,
    id: 'e57bf4f6aad752f9fc37fef3'
    title: 'SOMY X100 Project Notification'
    desc: 'SOMY X100 Project Notification Twitter Post'
    media: 'TWITTER'
    project:
      id: '577d415a8287e48327d8443c'
      title: 'SOMY X100'
    node:
      id: '507f81413d070321728fde14'
      title: 'Notification'
    action:
      id: 'f8e39128e0b51ecb5b1e6e40'
      title: 'Post to Multiple Socal Media'
    status: 'WAITING'
    created_at: new Date(1371191309656)
  ]

  workflows = new Workflows
  WORKFLOWS.forEach (wf) -> workflows.create wf

  contents = new Contents
  CONTENTS.forEach (r) -> contents.create r

  console.log 'test data loaded'
  { # exports
  workflows
  contents
  }
