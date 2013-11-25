"use strict"

define 'import_data', ['models'], ({
Workflow, Workflows
}) ->

  data =
    workflows: [
      name: 'Demo Workflow'
      key: 'demo_workflow'
      desc: 'Demo Workflow for App Dev and Logo Desgin'
      start_node_id: 0
      nodes: [
        name: 'Post Idea'
        key: 'post_idea'
        desc: 'Post project idea'
        offset:
          x: 26, y: 43
        actions: [
          context_type: 'CREATE_PAGE'
        ,
          context_type: 'POST_FACEBOOK'
        ,
          context_type: 'GENERATE_REPORT'
        ]
      ,
        name: 'Post Requirements'
        key: 'post_requirements'
        desc: 'Post requirements'
        offset:
          x: 334, y: 44
        actions: [
          context_type: 'CREATE_PAGE'
        ,
          context_type: 'POST_FACEBOOK'
        ,
          context_type: 'GENERATE_REPORT'
        ]
      ,
        name: 'Cancel Notification'
        key: 'cancel_notification'
        desc: 'Cancel project for less response than expected'
        offset:
          x: 334, y: 204
        actions: [
          context_type: 'POST_FACEBOOK'
        ]
      ,
        name: 'Retrieve App Submissions'
        key: 'retrieve_app_submissions'
        desc: 'Retrieve app submissions'
        offset:
          x: 613, y: 44
        actions: [
          context_type: 'CREATE_PAGE'
        ,
          context_type: 'POST_FACEBOOK'
        ,
          context_type: 'GENERATE_REPORT'
        ]
      ,
        name: 'Retrieve Logo Design'
        key: 'retrieve_logo_design'
        desc: 'Retrieve logo design'
        offset:
          x: 629, y: 282
        actions: [
          context_type: 'CREATE_PAGE'
        ,
          context_type: 'POST_FACEBOOK'
        ,
          context_type: 'GENERATE_REPORT'
        ]
      ,
        name: 'Email to Evaluators'
        key: 'email_to_evaluators'
        desc: 'Email to evaluators'
        offset:
          x: 1018, y: 44
        actions: [
          context_type: 'CREATE_PAGE'
        ,
          context_type: 'SEND_EMAIL'
        ,
          context_type: 'GENERATE_REPORT'
        ]
      ,
        name: 'Post and Vote'
        key: 'post_and_vote'
        desc: 'Post and vote'
        offset:
          x: 1043, y: 282
        actions: [
          context_type: 'CREATE_PAGE'
        ,
          context_type: 'POST_FACEBOOK'
        ,
          context_type: 'GENERATE_REPORT'
        ]
      ,
        name: 'Post Final Result and Reward'
        key: 'post_final_result_and_reward'
        desc: 'Post final result and reward'
        offset:
          x: 1420, y: 183
        actions: [
          context_type: 'CREATE_PAGE'
        ,
          context_type: 'POST_FACEBOOK'
        ,
          context_type: 'SEND_EMAIL'
        ,
          context_type: 'GENERATE_REPORT'
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
      name: 'Conference Check-in Mobile App'
      key: 'conf_app_dev'
      desc: 'Dev WorkflowBased on project requirements, use crowds sourcing idea to complete the development by steps from design, implementation, testing, until the final result of this web application.Based on project requirements, use crowds sourcing idea to complete the development by steps to the result of this application.'
      start_node_id: 0
      nodes: [
        name: 'Requirement and Desgin'
        key: 'desgin'
        desc: 'Post requirement and collect desgins.'
        offset:
          x: 40
          y: 112
        actions: [
          context_type: 'CREATE_PAGE'
          name: 'Post Reqirement'
          key: 'post_req'
        ,
          context_type: 'POST_FACEBOOK'
          name: 'Post Requirement to Facebook'
          key: 'post_req_fb'
        ,
          context_type: 'MAKE_SCHEDULE'
          name: 'Wait for Design Submit'
          key: 'design_event'
        ,
          context_type: 'GENERATE_REPORT'
          name: 'Generate Submit Report'
          key: 'desgin_report'
        ]
      ,
        name: 'Vote App Design'
        key: 'vote'
        desc: 'Public vote submitted designs.'
        offset:
          x: 308
          y: 54
        actions: [
          context_type: 'CREATE_PAGE'
          name: 'Post Design Vote Form'
          key: 'post_design_vote_form'
        ,
          context_type: 'POST_FACEBOOK'
          name: 'Post Vote to Facebook'
          key: 'design_vote_fb'
        ,
          context_type: 'MAKE_SCHEDULE'
          name: 'Wait for Design Vote'
          key: 'desgin_vote_event'
        ,
          context_type: 'GENERATE_REPORT'
          name: 'Generate Design Vote Report'
          key: 'design_vote_report'
        ]
      ,
        name: 'Evaluate App Design'
        key: 'evaluate'
        desc: 'Evaluator judge the designs and votes.'
        offset:
          x: 588
          y: 54
        actions: [
          context_type: 'CREATE_PAGE'
          name: 'Post Evaluate Form'
          key: 'eval_form'
        ,
          context_type: 'SEND_EMAIL'
          name: 'Send Email to Evaluator'
          key: 'email_eval'
        ,
          context_type: 'MAKE_SCHEDULE'
          name: 'Wait for Evaluate'
          key: 'eval_event'
        ,
          context_type: 'GENERATE_REPORT'
          name: 'Generate Evaluate Report'
          key: 'eval_report'
        ]
      ,
        name: 'App Implemetation'
        key: 'implemetation'
        desc: 'Post final design and wait for implemetation submission.'
        offset:
          x: 866
          y: 54
        actions: [
          context_type: 'MAKE_SCHEDULE'
          name: 'Wait Owner Process Final Desgin'
          key: 'proc_design'
        ,
          context_type: 'CREATE_PAGE'
          name: 'Post Final Desgin and Implement Submit Form'
          key: 'post_design_submit_impl'
        ,
          context_type: 'POST_FACEBOOK'
          name: 'Post Design to Facebook'
          key: 'desgin_impl_fb'
        ,
          context_type: 'MAKE_SCHEDULE'
          name: 'Wait for Implementation'
          key: 'impl_event'
        ,
          context_type: 'GENERATE_REPORT'
          name: 'Generate Implementation Report'
          key: 'impl_report'
        ]
      ,
        name: 'Review'
        key: 'review'
        desc: 'Evaluator review submitted app implemetations.'
        offset:
          x: 1077
          y: 54
        actions: [
          context_type: 'CREATE_PAGE'
          name: 'Post Review Form'
          key: 'review_page'
        ,
          context_type: 'SEND_EMAIL'
          name: 'Send Review Request Email'
          key: 'review_email'
        ,
          context_type: 'MAKE_SCHEDULE'
          name: 'Wait for Review'
          key: 'review_event'
        ,
          context_type: 'GENERATE_REPORT'
          name: 'Generate Review Report'
          key: 'review_report'
        ]
      ,
        name: 'Testing'
        key: 'testing'
        desc: 'Post selected implementaion and open beta testing, collect feedbacks.'
        offset:
          x: 898
          y: 246
        actions: [
          context_type: 'MAKE_SCHEDULE'
          name: 'Wait Owner Process Review and Implementation'
          key: 'proc_impl'
        ,
          context_type: 'CREATE_PAGE'
          name: 'Post Selected Implementation and Test Request'
          key: 'post_impl_test'
        ,
          context_type: 'POST_FACEBOOK'
          name: 'Post Implementation to Facebook'
          key: 'impl_fb'
        ,
          context_type: 'MAKE_SCHEDULE'
          name: 'Wait for Testing'
          key: 'test_event'
        ,
          context_type: 'GENERATE_REPORT'
          name: 'Generate Testing Report'
          key: 'test_report'
        ]
      ,
        name: 'Demo'
        key: 'demo'
        desc: 'Post final result to the public.'
        offset:
          x: 1083
          y: 246
        actions: [
          context_type: 'MAKE_SCHEDULE'
          name: 'Wait Owner Final Result'
          key: 'result_event'
        ,
          context_type: 'CREATE_PAGE'
          name: 'Post Final Result and Demo'
          key: 'post_result'
        ,
          context_type: 'POST_FACEBOOK'
          name: 'Post Final Result to Facebook'
          key: 'result_fb'
        ]
      ]
      links: [
        key: 'desgin_to_vote'
        prev_node_id: 0
        next_node_id: 1
      ,
        name: 'Vote >= 200'
        key: 'vote_to_evaluate'
        prev_node_id: 1
        next_node_id: 2
      ,
        name: 'Failed to Pass'
        key: 'evaluate_to_desgin'
        prev_node_id: 2
        next_node_id: 0
      ,
        name: 'Votes < 200'
        key: 'vote_to_vote'
        prev_node_id: 1
        next_node_id: 1
      ,
        name: 'Passed'
        key: 'evaluate_to_implemetation'
        prev_node_id: 2
        next_node_id: 3
      ,
        key: 'implemetation_to_review'
        prev_node_id: 3
        next_node_id: 4
      ,
        name: 'Not satisfied'
        key: 'review_to_implemetation'
        prev_node_id: 4
        next_node_id: 3
      ,
        key: 'review_to_testing'
        prev_node_id: 4
        next_node_id: 5
      ,
        key: 'testing_to_demo'
        prev_node_id: 5
        next_node_id: 6
      ]
    ]

  console.log 'data start importing...'

  (new Workflows data.workflows).forEach (wf) -> wf.save()

  return
