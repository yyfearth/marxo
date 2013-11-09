"use strict"

define 'import_data', ['models'], ({
Workflow, Workflows
}) ->

  data =
    workflows: [
      name: 'Demo Workflow'
      key: 'demo_workflow'
      desc: 'Demo Workflow for App Dev and Logo Desgin'
      nodes: [
        name: 'Post Idea'
        key: 'post_idea'
        desc: 'Post project idea'
        offset:
          x: 26, y: 43
        actions: [
          type: 'CREATE_PAGE'
        ,
          type: 'POST_FACEBOOK'
        ,
          type: 'GENERATE_REPORT'
        ]
      ,
        name: 'Post Requirements'
        key: 'post_requirements'
        desc: 'Post requirements'
        offset:
          x: 334, y: 44
        actions: [
          type: 'CREATE_PAGE'
        ,
          type: 'POST_FACEBOOK'
        ,
          type: 'GENERATE_REPORT'
        ]
      ,
        name: 'Cancel Notification'
        key: 'cancel_notification'
        desc: 'Cancel project for less response than expected'
        offset:
          x: 334, y: 204
        actions: [
          type: 'POST_FACEBOOK'
        ]
      ,
        name: 'Retrieve App Submissions'
        key: 'retrieve_app_submissions'
        desc: 'Retrieve app submissions'
        offset:
          x: 613, y: 44
        actions: [
          type: 'CREATE_PAGE'
        ,
          type: 'POST_FACEBOOK'
        ,
          type: 'GENERATE_REPORT'
        ]
      ,
        name: 'Retrieve Logo Design'
        key: 'retrieve_logo_design'
        desc: 'Retrieve logo design'
        offset:
          x: 629, y: 282
        actions: [
          type: 'CREATE_PAGE'
        ,
          type: 'POST_FACEBOOK'
        ,
          type: 'GENERATE_REPORT'
        ]
      ,
        name: 'Email to Evaluators'
        key: 'email_to_evaluators'
        desc: 'Email to evaluators'
        offset:
          x: 1018, y: 44
        actions: [
          type: 'CREATE_PAGE'
        ,
          type: 'SEND_EMAIL'
        ,
          type: 'GENERATE_REPORT'
        ]
      ,
        name: 'Post and Vote'
        key: 'post_and_vote'
        desc: 'Post and vote'
        offset:
          x: 1043, y: 282
        actions: [
          type: 'CREATE_PAGE'
        ,
          type: 'POST_FACEBOOK'
        ,
          type: 'GENERATE_REPORT'
        ]
      ,
        name: 'Post Final Result and Reward'
        key: 'post_final_result_and_reward'
        desc: 'Post final result and reward'
        offset:
          x: 1420, y: 183
        actions: [
          type: 'CREATE_PAGE'
        ,
          type: 'POST_FACEBOOK'
        ,
          type: 'SEND_EMAIL'
        ,
          type: 'GENERATE_REPORT'
        ]
      ]
      links: [
        key: '10_days_likes_gte_300'
        prev_node_id: 0
        next_node_id: 1
        name: '10 Days & Likes >= 300'
      ,
        key: '10_days_likes_lt_300'
        prev_node_id: 0
        next_node_id: 2
        name: '10 Days & Likes < 300'
      ,
        key: 'post_req_to_retrieve_app'
        prev_node_id: 1
        next_node_id: 3
      ,
        key: 'post_req_to_retrieve_logo'
        prev_node_id: 1
        next_node_id: 4
      ,
        key: 'retrieve_app_to_evaluate'
        prev_node_id: 3
        next_node_id: 5
        name: '15 Days & > 80% Response'
      ,
        key: 'retrieve_logo_to_post_and_vote'
        prev_node_id: 4
        next_node_id: 6
        name: '10 Days & Submissions >= 3'
      ,
        name: '(fallback)'
        key: 'faild_to_evaluate'
        desc: 'Manual trigger back if failed to evaluate'
        prev_node_id: 5
        next_node_id: 3
      ,
        name: '(fallback)'
        key: 'failed_to_vote'
        desc: 'Manual trigger back if failed to vote'
        prev_node_id: 6
        next_node_id: 4
      ,
        key: 'evaluate_to_post_final'
        prev_node_id: 5
        next_node_id: 7
      ,
        key: 'post_and_vote_to_post_final'
        prev_node_id: 6
        next_node_id: 7
      ]
    ,
      key: 'test_wf'
      name: 'Test Workflow'
      desc: 'The test workflow'
      nodes: [
        key: 'post_idea'
        name: 'Post Idea'
        desc: 'Post software project ideas'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
      ,
        key: 'post_cancel'
        name: 'Post Cancel'
        desc: 'Post cancel notification'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
      ,
        key: 'post_req'
        name: 'Post Requirement'
        desc: 'Post project requirement'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
      ,
        key: 'submit_design'
        name: 'Submit Design'
        desc: 'Retrieve theme design submissions & e-mail to stackholders'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
      ,
        key: 'notification'
        name: 'Notification'
        desc: 'Notification'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
      ,
        key: 'post_result'
        name: 'Post Result'
        desc: 'Post & e-mail result everyone'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
      ]
      links: [
        key: 'post_req_to_submit_design'
        workflow_id: '50447afb4728cb2036cf9ca1'
        prev_node_id: 2
        next_node_id: 3
      ,
        key: 'pass_to_post'
        desc: 'Post & e-mail to everyone if pass rate > 50%'
        workflow_id: '50447afb4728cb2036cf9ca1'
        prev_node_id: 3
        next_node_id: 5
      ,
        key: 'to_cancel'
        name: 'Like < 300'
        desc: 'Cancel if like count < 300'
        workflow_id: '50447afb4728cb2036cf9ca1'
        prev_node_id: 0
        next_node_id: 1
      ,
        key: 'continue_to_req'
        name: 'Like >= 300'
        desc: 'Continue to post requirement if like count >= 300'
        workflow_id: '50447afb4728cb2036cf9ca1'
        prev_node_id: 0
        next_node_id: 2
      ,
        key: 'not_pass_to_notify'
        name: 'Pass rate <= 50%'
        desc: 'Notification if pass rate <= 50%'
        workflow_id: '50447afb4728cb2036cf9ca1'
        prev_node_id: 3
        next_node_id: 4
      ]
    ]

  console.log 'data start importing...'

  (new Workflows data.workflows).forEach (wf) -> wf.save()

  return
