"use strict"

define 'import_data', ['models'], ({
Workflow, Workflows
}) ->

  data =
    workflows: [
#      name: 'Demo Workflow'
#      key: 'demo_workflow'
#      desc: 'Demo Workflow for App Dev and Logo Desgin'
#      start_node_id: 0
#      nodes: [
#        name: 'Post Idea'
#        key: 'post_idea'
#        desc: 'Post project idea'
#        offset:
#          x: 26, y: 43
#        actions: [
#          type: 'PAGE'
#        ,
#          type: 'FACEBOOK'
#        ,
#          type: 'WAIT'
#        ]
#      ,
#        name: 'Post Requirements'
#        key: 'post_requirements'
#        desc: 'Post requirements'
#        offset:
#          x: 334, y: 44
#        actions: [
#          type: 'PAGE'
#        ,
#          type: 'FACEBOOK'
#        ,
#          type: 'WAIT'
#        ]
#      ,
#        name: 'Cancel Notification'
#        key: 'cancel_notification'
#        desc: 'Cancel project for less response than expected'
#        offset:
#          x: 334, y: 204
#        actions: [
#          type: 'FACEBOOK'
#        ]
#      ,
#        name: 'Retrieve App Submissions'
#        key: 'retrieve_app_submissions'
#        desc: 'Retrieve app submissions'
#        offset:
#          x: 613, y: 44
#        actions: [
#          type: 'PAGE'
#        ,
#          type: 'FACEBOOK'
#        ,
#          type: 'WAIT'
#        ]
#      ,
#        name: 'Retrieve Logo Design'
#        key: 'retrieve_logo_design'
#        desc: 'Retrieve logo design'
#        offset:
#          x: 629, y: 282
#        actions: [
#          type: 'PAGE'
#        ,
#          type: 'FACEBOOK'
#        ,
#          type: 'WAIT'
#        ]
#      ,
#        name: 'Email to Evaluators'
#        key: 'email_to_evaluators'
#        desc: 'Email to evaluators'
#        offset:
#          x: 1018, y: 44
#        actions: [
#          type: 'PAGE'
#        ,
#          type: 'EMAIL'
#        ,
#          type: 'WAIT'
#        ]
#      ,
#        name: 'Post and Vote'
#        key: 'post_and_vote'
#        desc: 'Post and vote'
#        offset:
#          x: 1043, y: 282
#        actions: [
#          type: 'PAGE'
#        ,
#          type: 'FACEBOOK'
#        ,
#          type: 'WAIT'
#        ]
#      ,
#        name: 'Post Final Result and Reward'
#        key: 'post_final_result_and_reward'
#        desc: 'Post final result and reward'
#        offset:
#          x: 1420, y: 183
#        actions: [
#          type: 'PAGE'
#        ,
#          type: 'FACEBOOK'
#        ,
#          type: 'EMAIL'
#        ,
#          type: 'WAIT'
#        ]
#      ]
#      links: [
#        key: '10_days_likes_gte_300'
#        prev_node_id: 0
#        next_node_id: 1
#        name: '10 Days & Likes >= 300'
#      ,
#        key: '10_days_likes_lt_300'
#        prev_node_id: 0
#        next_node_id: 2
#        name: '10 Days & Likes < 300'
#      ,
#        key: 'post_req_to_retrieve_app'
#        prev_node_id: 1
#        next_node_id: 3
#      ,
#        key: 'post_req_to_retrieve_logo'
#        prev_node_id: 1
#        next_node_id: 4
#      ,
#        key: 'retrieve_app_to_evaluate'
#        prev_node_id: 3
#        next_node_id: 5
#        name: '15 Days & > 80% Response'
#      ,
#        key: 'retrieve_logo_to_post_and_vote'
#        prev_node_id: 4
#        next_node_id: 6
#        name: '10 Days & Submissions >= 3'
#      ,
#        name: '(fallback)'
#        key: 'faild_to_evaluate'
#        desc: 'Manual trigger back if failed to evaluate'
#        prev_node_id: 5
#        next_node_id: 3
#      ,
#        name: '(fallback)'
#        key: 'failed_to_vote'
#        desc: 'Manual trigger back if failed to vote'
#        prev_node_id: 6
#        next_node_id: 4
#      ,
#        key: 'evaluate_to_post_final'
#        prev_node_id: 5
#        next_node_id: 7
#      ,
#        key: 'post_and_vote_to_post_final'
#        prev_node_id: 6
#        next_node_id: 7
#      ]
#    ,
      name: 'Conference Check-in Mobile App'
      key: 'conf_app_dev'
      desc: 'Based on project requirements, use crowds sourcing to complete the development steps from ' +
      'design, implementation, testing, until the final result of this app. '
      start_node_id: 0
      nodes: [
        name: 'Requirement and Desgin'
        key: 'desgin'
        desc: 'Post requirement and collect desgins.'
        offset:
          x: 40, y: 112
        actions: [
          type: 'PAGE'
          name: 'Post Reqirement'
          key: 'post_req'
          content:
            type: 'PAGE'
            name: 'Conference Check-in Mobile App Reqirements and Design Submission'
            desc: ''
            sections: [
              name: 'Design Name'
              type: 'TEXT'
            ]
        ,
          type: 'FACEBOOK'
          name: 'Post Requirement to Facebook'
          key: 'post_req_fb'
          content:
            type: 'FACEBOOK'
            message: '''Attention! Conference Check-in Mobile App start to receive submission.
            See requirements and submit your desgin in '''
        ,
          type: 'WAIT'
          name: 'Wait for Design Submit'
          key: 'wait_for_design'
        ]
      ,
        name: 'Vote App Design'
        key: 'vote'
        desc: 'Public vote submitted designs.'
        offset:
          x: 62, y: 240
        actions: [
          type: 'PAGE'
          name: 'Post Design Vote Form'
          key: 'post_design_vote_form'
        ,
          type: 'FACEBOOK'
          name: 'Post Vote to Facebook'
          key: 'design_vote_fb'
          content:
            type: 'FACEBOOK'
            message: 'Vote your favorite desgin for our Conference Check-in Mobile App.'
        ,
          type: 'WAIT'
          name: 'Wait for Design Vote'
          key: 'wait_for_desgin_vote'
        ]
      ,
        name: 'Evaluate App Design'
        key: 'evaluate'
        desc: 'Evaluator judge the designs and votes.'
        offset:
          x: 50, y: 368
        actions: [
          type: 'PAGE'
          name: 'Post Evaluate Form'
          key: 'eval_form'
        ,
          type: 'EMAIL'
          name: 'Send Email to Evaluator'
          key: 'email_eval'
        ,
          type: 'WAIT'
          name: 'Wait for Evaluate'
          key: 'wait_for_eval'
        ]
      ,
        name: 'App Implemetation'
        key: 'implemetation'
        desc: 'Post final design and wait for implemetation submission.'
        offset:
          x: 57, y: 500
        actions: [
          type: 'WAIT'
          name: 'Wait Owner Process Final Desgin'
          key: 'proc_design'
        ,
          type: 'PAGE'
          name: 'Post Final Desgin and Implement Submit Form'
          key: 'post_design_submit_impl'
        ,
          type: 'FACEBOOK'
          name: 'Post Design to Facebook'
          key: 'desgin_impl_fb'
          content:
            type: 'FACEBOOK'
            message: '''The final desgin of Conference Check-in Mobile App is ready!
            Please submit your code in '''
        ,
          type: 'WAIT'
          name: 'Wait for Implementation'
          key: 'wait_for_impl'
        ]
      ,
        name: 'Review'
        key: 'review'
        desc: 'Evaluator review submitted app implemetations.'
        offset:
          x: 78, y: 623
        actions: [
          type: 'PAGE'
          name: 'Post Review Form'
          key: 'review_page'
        ,
          type: 'EMAIL'
          name: 'Send Review Request Email'
          key: 'review_email'
        ,
          type: 'WAIT'
          name: 'Wait for Review'
          key: 'wait_for_review'
        ]
      ,
        name: 'Testing'
        key: 'testing'
        desc: 'Post selected implementaion and open beta testing, collect feedbacks.'
        offset:
          x: 78, y: 756
        actions: [
          type: 'WAIT'
          name: 'Wait Owner Process Review and Implementation'
          key: 'proc_impl'
        ,
          type: 'PAGE'
          name: 'Post Selected Implementation and Test Request'
          key: 'post_impl_test'
        ,
          type: 'FACEBOOK'
          name: 'Post Implementation to Facebook'
          key: 'impl_fb'
          content:
            type: 'FACEBOOK'
            message: '''Our committee have the final result of the winner of implemetation of our app.
            Welcome to feedback any bugs or leave comments in '''
        ,
          type: 'WAIT'
          name: 'Wait for Testing'
          key: 'wait_for_test'
        ]
      ,
        name: 'Demo'
        key: 'demo'
        desc: 'Post final result to the public.'
        offset:
          x: 78, y: 883
        actions: [
          type: 'WAIT'
          name: 'Wait Owner Final Result'
          key: 'wait_for_result'
        ,
          type: 'PAGE'
          name: 'Post Final Result and Demo'
          key: 'post_result'
        ,
          type: 'FACEBOOK'
          name: 'Post Final Result to Facebook'
          key: 'result_fb'
          content:
            type: 'FACEBOOK'
            message: '''The Conference Check-in Mobile App is ready!
            See our demo here '''
        ]
      ]
      links: [
        key: 'desgin_to_vote'
        prev_node_id: 0
        next_node_id: 1
      ,
        #name: 'Vote >= 200'
        key: 'vote_to_evaluate'
        prev_node_id: 1
        next_node_id: 2
      #,
      #  name: 'Failed to Pass'
      #  key: 'evaluate_to_desgin'
      #  prev_node_id: 2
      #  next_node_id: 0
      #,
      #  name: 'Votes < 200'
      #  key: 'vote_to_vote'
      #  prev_node_id: 1
      #  next_node_id: 1
      ,
        #name: 'Passed'
        key: 'evaluate_to_implemetation'
        prev_node_id: 2
        next_node_id: 3
      ,
        key: 'implemetation_to_review'
        prev_node_id: 3
        next_node_id: 4
      #,
      #  name: 'Not satisfied'
      #  key: 'review_to_implemetation'
      #  prev_node_id: 4
      #  next_node_id: 3
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
