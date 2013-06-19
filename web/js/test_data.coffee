"use strict"

define 'test_data', ['models'], (models) ->

  data =
    workflows: [
      id: '50447afb4728cb2036cf9ca1'
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
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde11'
        name: 'post_cancel'
        title: 'Post Cancel'
        desc: 'Post cancel notification'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde12'
        name: 'post_req'
        title: 'Post Requirement'
        desc: 'Post project requirement'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde13'
        name: 'submit_design'
        title: 'Submit Design'
        desc: 'Retrieve theme design submissions & e-mail to stackholders'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde14'
        name: 'notification'
        title: 'Notification'
        desc: 'Notification'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde15'
        name: 'post_result'
        title: 'Post Result'
        desc: 'Post & e-mail result everyone'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ]
      links: [
        id: '507f81413d070321728fde22'
        name: 'post_req_to_submit_design'
        workflow_id: '50447afb4728cb2036cf9ca1'
        prev_node_id: '507f81413d070321728fde12'
        next_node_id: '507f81413d070321728fde13'
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde24'
        name: 'pass_to_post'
        desc: 'Post & e-mail to everyone if pass rate > 50%'
        workflow_id: '50447afb4728cb2036cf9ca1'
        prev_node_id: '507f81413d070321728fde13'
        next_node_id: '507f81413d070321728fde15'
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde20'
        name: 'to_cancel'
        title: 'Like < 300'
        desc: 'Cancel if like count < 300'
        workflow_id: '50447afb4728cb2036cf9ca1'
        prev_node_id: '507f81413d070321728fde10'
        next_node_id: '507f81413d070321728fde11'
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde21'
        name: 'continue_to_req'
        title: 'Like >= 300'
        desc: 'Continue to post requirement if like count >= 300'
        workflow_id: '50447afb4728cb2036cf9ca1'
        prev_node_id: '507f81413d070321728fde10'
        next_node_id: '507f81413d070321728fde12'
        created_at: new Date(1363879402429)
        updated_at: new Date(1363879402429)
      ,
        id: '507f81413d070321728fde23'
        name: 'not_pass_to_notify'
        title: 'Pass rate <= 50%'
        desc: 'Notification if pass rate <= 50%'
        workflow_id: '50447afb4728cb2036cf9ca1'
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
    ]

    projects: [
      id: '50447afb4728cc2036cf9cf1'
      workflow_id: '50447afb4728cb2036cf9ca1'
      name: 'test_proj'
      title: 'Test Project'
      desc: 'The test project applying the test workflow'
      status: 'RUNNING'
      created_at: new Date(1371363975245)
      updated_at: new Date(1371363975245)
    ]

    contents: [
      id: 'e57bf4f6aad752f9fc37fcf1'
      title: 'SOMY X100 Project Initialization'
      desc: 'SOMY X100 Project Initialization Page'
      media: 'PAGE'
      project:
        id: '50447afb4728cc2036cf9cf1'
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
        id: '50447afb4728cc2036cf9cf1'
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
        id: '50447afb4728cc2036cf9cf1'
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
        id: '50447afb4728cc2036cf9cf1'
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
        id: '50447afb4728cc2036cf9cf1'
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
        id: '50447afb4728cc2036cf9cf1'
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
      id: 'e57bf4f6aad752f9fc37feff'
      title: 'Test Content'
      desc: 'Test Content'
      media: 'TWITTER'
      project:
        id: '51447afb4728cc2036cf9cf1'
        title: 'Test Project'
      node:
        id: '517f81413d070321728fde14'
        title: 'Test Node'
      action:
        id: 'f8e39128e0b51ecb5b1e6e40'
        title: 'Post to Multiple Socal Media'
      status: 'WAITING'
      created_at: new Date(1371191309656)
    ]

  exports = {}
  for name, list of data
    name = name.toLowerCase()
    cap = name.charAt(0).toUpperCase() + name[1..]
    throw 'unknown test data name ' + name unless models[cap]
    col = new models[cap]
    list.forEach (r) -> col.create r
    exports[name] = col
  console.log 'test data loaded'
  exports
